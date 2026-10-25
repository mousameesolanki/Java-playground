//Add logging caching +retry to a Datafetchedr without subclassing every combination
public class Decorator {
   public static void main(String[] args) {
      DataFetcher fetcher = new LoggingFetcher(new CachingFetcher(new BaseFetcher()));
    } 
}
interface DataFetcher {
    String fetchData(String url);
}
class BaseFetcher implements DataFetcher {
    @Override
    public String fetchData(String url) {
        // Simulate fetching data from the URL
        return "Data from " + url;
    }
}
class LoggingFetcher implements DataFetcher {
    private final DataFetcher wrappedFetcher;
//constructor injection
    public LoggingFetcher(DataFetcher wrappedFetcher) {
        this.wrappedFetcher = wrappedFetcher;
    }

    @Override
    public String fetchData(String url) {
        System.out.println("Fetching data from: " + url);
        String data = wrappedFetcher.fetchData(url);
        System.out.println("Fetched data: " + data);
        return data;
    }
}
class CachingFetcher implements DataFetcher {
    private final DataFetcher wrappedFetcher;
    private final java.util.Map<String, String> cache = new java.util.concurrent.ConcurrentHashMap<>();

    public CachingFetcher(DataFetcher wrappedFetcher) {
        this.wrappedFetcher = wrappedFetcher;
    }

    @Override
    public String fetchData(String url) {
        if(cache.containsKey(url)) {
            System.out.println("Returning cached data for: " + url);
            return cache.get(url);
        }
        String data = wrappedFetcher.fetchData(url);
        cache.put(url, data);
        return data;
    }
}
