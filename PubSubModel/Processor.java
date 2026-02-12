package MovieUploadpProcessing;
//processors cpu heaavy
public class Processor implements Runnable{
  private final UploadEvent event;
  public Processor(UploadEvent e){
   this.event=e;
  }
  @Override
  public void run(){
    try{
      validate(event);
      generateThumbnail(event);
      transcode(event);
      Storage.getInstance().save(event.getMovieMetada());
      System.out.println("Finished Processing"+ event.getMovieMetada().getTitle());
    }
    catch(Exception e){
      System.out.println("Error in prpcessing"+e.getMessage());
    }
  }
  private void validate(UploadEvent e) throws InterruptedException{
   System.out.println("Validating event"+e.getMovieMetada().getTitle());
   Thread.sleep(500);//simulate cpu work
  }
  private void generateThumbnail(UploadEvent e) throws InterruptedException{
   System.out.println("generating thumbnail for event"+e.getMovieMetada().getTitle());
   Thread.sleep(700);//simulate cpu work
  }
  private void transcode(UploadEvent e) throws InterruptedException{
 System.out.println("Transcoding  event"+e.getMovieMetada().getTitle());
   Thread.sleep(1200);//simulate cpu work
  }
}
