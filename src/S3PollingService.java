import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.amazonaws.services.s3.model.S3ObjectSummary;


public class S3PollingService implements Runnable {

    private S3LocalDrive drive;
    private boolean terminated = false;
    private S3Client s3;
    private Set<String> s3FileNames = new HashSet<String>();
    private Set<String> modificationFromS3 = new HashSet<String>();
    
    public S3PollingService(S3LocalDrive drive) {
        this.drive = drive;
        s3 = S3Client.getInstance();
    }
    
    public synchronized boolean isModifiedFromS3(String fileName) {
        return modificationFromS3.contains(fileName);
    }
    
    public synchronized void removeModificationFromS3(String fileName) {
        modificationFromS3.remove(fileName);
    }
    
    public synchronized void addModificationFromS3(String fileName) {
        modificationFromS3.add(fileName);
    }
    
    @Override
    public void run() {
        while (!terminated) {
            List<S3ObjectSummary> summaries = s3.listFiles(drive.getUserBucketName());
            s3FileNames.clear();
            String bucketName = drive.getUserBucketName();
            String driveDir = drive.getdriveDir();
            for (S3ObjectSummary summary : summaries) {
                String key = summary.getKey();
                s3FileNames.add(key);
                if (!drive.hasFileName(driveDir + "/" + key)) {
                    String modId = key.endsWith("/") ? key.substring(0, key.length() - 1) : key;
                    addModificationFromS3(modId);
                    s3.saveFile(bucketName, key, driveDir);
                    String absolutePath = driveDir + "/" + key;
                    drive.addFileName(absolutePath);
                    System.out.println("Found file on S3: " + key + ", downloading.");
                }
            }
            Set<String> localFileNames = drive.getFileNames();
            for (String fileName : localFileNames) {
                String key = fileName.replace(driveDir + "/", "");
                if (!s3FileNames.contains(key)) {
                    String modId = key.endsWith("/") ? key.substring(0, key.length() - 1) : key;
                    addModificationFromS3(modId);
                    File fileToDelete = new File(fileName);
                    fileToDelete.delete();
                    String absolutePath = driveDir + "/" + key;
                    drive.removeFileName(absolutePath);
                    System.out.println("Cannot find file on S3: " + key + ", deleted local file.");
                }
            }
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
