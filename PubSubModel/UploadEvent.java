package MovieUploadpProcessing;

public class UploadEvent {
  private final MovieMetada movieMetada;
  private final String filepath;
  public UploadEvent(MovieMetada movieMetada, String filepath) {
    this.movieMetada = movieMetada;
    this.filepath = filepath;
  }
  public MovieMetada getMovieMetada() {
    return movieMetada;
  }
  public String getFilepath() {
    return filepath;
  }
  
}
