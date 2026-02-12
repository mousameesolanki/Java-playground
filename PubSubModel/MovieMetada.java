/*// Movie Upload & Processing Pipeline
// 📘 Background Story
// You are building a video streaming platform (like Netflix / Prime).
// When a user uploads a movie:
// The movie file is uploaded to the system
// The system must:
// Validate the file
// Generate thumbnails
// Transcode the movie into multiple resolutions (480p, 720p, 1080p)
// These processing steps are CPU - intensive
// The platform receives thousands of uploads concurrently
// Functional Requirements
// Upload requests can come faster than processing
// Processing workers are limited (CPU - bound)
// Upload requests must not be dropped
// System should not run out of memory
// Processing should happen in the order uploads are received
// If processors are busy, uploads should wait safely

//File
//FileMetada
//upoaders-threadpools for high concurrent uploads simulation
//consumers-runnable dispatcher startConsumers(->
//workers fixed no of workers for cpu intensive task
//concurrent hashmap for storage thread safe
//blockingqueue for persistence backpressure ordering and its bounded workers also limited
//HLD you need to be clear about have flow in ming
//upload->storage->prpcessing */
package MovieUploadpProcessing;

public class MovieMetada {
  private  final String title;
  private  final String userId;
  
  private  final long fileSize;
  public MovieMetada(String title, String userId, long fileSize) {
    this.title = title;
    this.userId = userId;
    this.fileSize = fileSize;
  }
  //getters
  public String getTitle() {
    return title;
  }   
  public String getUserId() {
    return userId;
  } 
  public long getFileSize() {
    return fileSize;
  }
  
}
