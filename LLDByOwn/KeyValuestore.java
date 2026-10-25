package LLDByOwn;
import java.util.*;

public class KeyValuestore {

    /*========================
        ENUM
    ========================*/
    enum CommandType {
        SET,
        GET,
        DELETE,
        BEGIN,
        COMMIT,
        ROLLBACK
    }

    /*========================
        ENTITY
    ========================*/
    static class Entry {
        private String value;
        private Long expiryTime; // epoch millis, null => no expiry

        public Entry(String value, Long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

        public String getValue() {
            return value;
        }

        public boolean isExpired() {
            return expiryTime != null &&
                    System.currentTimeMillis() > expiryTime;
        }

        public Entry copy() {
            return new Entry(value, expiryTime);
        }
    }

    /*========================
        REPOSITORY
    ========================*/
    static class KeyValueRepository {

        private final Map<String, Entry> storage = new HashMap<>();

        public void put(String key, Entry entry) {
            storage.put(key, entry);
        }

        public Entry get(String key) {
            return storage.get(key);
        }

        public void delete(String key) {
            storage.remove(key);
        }

        public boolean contains(String key) {
            return storage.containsKey(key);
        }
    }

    /*========================
        TRANSACTION
    ========================*/
    static class TransactionContext {

        Map<String, Entry> updates = new HashMap<>();

        Set<String> deleted = new HashSet<>();
    }

    /*========================
        TRANSACTION MANAGER
    ========================*/
    static class TransactionManager {

        private final Deque<TransactionContext> stack = new ArrayDeque<>();

        public void begin() {
            stack.push(new TransactionContext());
        }

        public boolean hasTransaction() {
            return !stack.isEmpty();
        }

        public TransactionContext current() {
            return stack.peek();
        }

        public TransactionContext pop() {
            return stack.pop();
        }

        public void push(TransactionContext ctx) {
            stack.push(ctx);
        }

        public int size() {
            return stack.size();
        }
    }

    /*========================
        SERVICE
    ========================*/
    static class KeyValueStoreService {

        private final KeyValueRepository repository;
        private final TransactionManager txManager;

        public KeyValueStoreService(KeyValueRepository repository,
                                    TransactionManager txManager) {
            this.repository = repository;
            this.txManager = txManager;
        }

        public String set(String key, String value, Long ttlMillis) {

            Long expiry = ttlMillis == null
                    ? null
                    : System.currentTimeMillis() + ttlMillis;

            Entry entry = new Entry(value, expiry);

            if (!txManager.hasTransaction()) {
                repository.put(key, entry);
            } else {
                TransactionContext tx = txManager.current();
                tx.deleted.remove(key);
                tx.updates.put(key, entry);
            }

            return "OK";
        }

        public String get(String key) {

            Entry entry = resolve(key);

            if (entry == null)
                return "NULL";

            if (entry.isExpired()) {
                if (!txManager.hasTransaction())
                    repository.delete(key);
                return "NULL";
            }

            return entry.getValue();
        }

        private Entry resolve(String key) {

            Iterator<TransactionContext> itr =
                    txManager.stack.iterator();

            while (itr.hasNext()) {

                TransactionContext tx = itr.next();

                if (tx.deleted.contains(key))
                    return null;

                if (tx.updates.containsKey(key))
                    return tx.updates.get(key);
            }

            return repository.get(key);
        }

        public String delete(String key) {

            if (resolve(key) == null)
                return "false";

            if (!txManager.hasTransaction()) {
                repository.delete(key);
            } else {
                TransactionContext tx = txManager.current();
                tx.updates.remove(key);
                tx.deleted.add(key);
            }

            return "true";
        }

        public String begin() {
            txManager.begin();
            return "OK";
        }

        public String rollback() {

            if (!txManager.hasTransaction())
                return "false";

            txManager.pop();

            return "OK";
        }

        public String commit() {

            if (!txManager.hasTransaction())
                return "false";

            TransactionContext current = txManager.pop();

            if (txManager.hasTransaction()) {

                TransactionContext parent = txManager.current();

                parent.updates.putAll(current.updates);

                parent.deleted.addAll(current.deleted);

                return "OK";
            }

            for (Map.Entry<String, Entry> e :
                    current.updates.entrySet()) {

                repository.put(e.getKey(), e.getValue());
            }

            for (String key : current.deleted) {

                repository.delete(key);
            }

            return "OK";
        }
    }

    /*========================
        FACADE
    ========================*/
    static class KeyValueStoreFacade {

        private final KeyValueStoreService service;

        public KeyValueStoreFacade(KeyValueStoreService service) {
            this.service = service;
        }

        public String execute(String[] tokens) {

            CommandType command =
                    CommandType.valueOf(tokens[0]);

            switch (command) {

                case SET:

                    Long ttl = null;

                    if (tokens.length == 4)
                        ttl = Long.parseLong(tokens[3]);

                    return service.set(tokens[1],
                            tokens[2],
                            ttl);

                case GET:
                    return service.get(tokens[1]);

                case DELETE:
                    return service.delete(tokens[1]);

                case BEGIN:
                    return service.begin();

                case COMMIT:
                    return service.commit();

                case ROLLBACK:
                    return service.rollback();

                default:
                    return "INVALID";
            }
        }
    }

    /*========================
        COMMAND PROCESSOR
    ========================*/
    static class CommandProcessor {

        private final KeyValueStoreFacade facade;

        public CommandProcessor(KeyValueStoreFacade facade) {
            this.facade = facade;
        }

        public void process(List<String> commands) {

            for (String cmd : commands) {

                String[] tokens = cmd.split(" ");

                System.out.println(facade.execute(tokens));
            }
        }
    }

    /*========================
        MAIN
    ========================*/
    public static void main(String[] args) {

        KeyValueRepository repository = new KeyValueRepository();

        TransactionManager txManager = new TransactionManager();

        KeyValueStoreService service =
                new KeyValueStoreService(repository, txManager);

        KeyValueStoreFacade facade =
                new KeyValueStoreFacade(service);

        CommandProcessor processor =
                new CommandProcessor(facade);

        List<String> demo = Arrays.asList(
                "SET a 10",
                "GET a",
                "BEGIN",
                "SET a 20",
                "GET a",
                "BEGIN",
                "SET a 30",
                "GET a",
                "ROLLBACK",
                "GET a",
                "COMMIT",
                "GET a"
        );

        processor.process(demo);
    }
}