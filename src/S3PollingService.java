
public class S3PollingService implements Runnable {

    private S3LocalDrive drive;
    private boolean terminated = false;
    private S3Client s3;
    
    public S3PollingService(S3LocalDrive drive) {
        this.drive = drive;
        s3 = S3Client.getInstance();
    }
    
    @Override
    public void run() {
        while (!terminated) {
            
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void terminate() {
        synchronized (this) {
            terminated = true;
        }
    }
}
