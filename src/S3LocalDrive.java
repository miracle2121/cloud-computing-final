import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import net.contentobjects.jnotify.JNotify;


public class S3LocalDrive {
    
    private String driveDir;
    private String userBucketName;
    private Set<String> fileNames;
    private DirectoryListener listener;
    private S3PollingService pollService;
    
    public S3LocalDrive(String userBucketName, String driveDir) {
        this.userBucketName = userBucketName;
        this.driveDir = driveDir;
        fileNames = new HashSet<String>();
    }
    
    public synchronized boolean hasFileName(String fileName) {
        return fileNames.contains(fileName);
    }

    public synchronized void addFileName(String fileName) {
        fileNames.add(fileName);
    }
    
    public synchronized void removeFileName(String fileName) {
        fileNames.remove(fileName);
    }
    
    public synchronized Set<String> getFileNames() {
        return new HashSet<String>(fileNames);
    }
    
    public String getUserBucketName() {
        return userBucketName;
    }
    
    public String getdriveDir() {
        return driveDir;
    }
    
    public DirectoryListener getListener() {
        return listener;
    }
    
    public S3PollingService getPollService() {
        return pollService;
    }
    
    private void initFileSet(String dirName) {
        File rootDir = new File(dirName);
        File[] files = rootDir.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                initFileSet(f.getAbsolutePath());
            }
            fileNames.add(f.getAbsolutePath());
        }
    }
    
    public void start() throws Exception {
        boolean stop = false;
        int mask = JNotify.FILE_CREATED  |
                   JNotify.FILE_DELETED  |
                   JNotify.FILE_MODIFIED |
                   JNotify.FILE_RENAMED;
        listener = new DirectoryListener(this);
        int watchId = JNotify.addWatch(driveDir, mask, true, listener);
        initFileSet(driveDir);
        pollService = new S3PollingService(this);
        //Thread pollingThread = new Thread(pollService);
        //pollingThread.start();
        
        System.out.println("Listening to directory: " + driveDir);
        System.out.println("Enter \"S\" to stop.");
        Scanner sc = new Scanner(System.in);
        
        while (!stop) {
            while (sc.hasNext()) {
                String str = sc.nextLine();
                if (str.toLowerCase().equals("s")) {
                    stop = true;
                    break;
                }
            }
            Thread.sleep(5000);
        }
        JNotify.removeWatch(watchId);
        pollService.terminate();
    }
    
    public static void main(String[] args) throws Exception {
        Properties prop = new Properties();
        prop.load(new FileInputStream(new File("bin/AwsCredentials.properties")));
        String userBucketName = prop.getProperty("userBucketName");
        String driveDir = prop.getProperty("driveDir");
        S3LocalDrive drive = new S3LocalDrive(userBucketName, driveDir);
        drive.start();
    }
    
}
