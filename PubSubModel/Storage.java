package MovieUploadpProcessing;

import java.util.concurrent.ConcurrentHashMap;

public class Storage {
  private final ConcurrentHashMap<String,MovieMetada> storage =new ConcurrentHashMap<>();
  private Storage() {}
  private static class Holder{
    private static final Storage INSTANCE =new Storage();
  }
public static Storage getInstance(){
  return Holder.INSTANCE;
}
public void save(MovieMetada metadata){
  storage.put(metadata.getTitle(),metadata);
  System.out.println("Movie stored"+metadata.getTitle());
}
}
