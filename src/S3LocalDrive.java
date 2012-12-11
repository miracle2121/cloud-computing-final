import java.util.Scanner;

import net.contentobjects.jnotify.JNotify;


public class S3LocalDrive {
    
    private static final String driveDir = "";
    
    public static void main(String[] args) throws Exception {
        boolean stop = false;
        int mask = JNotify.FILE_CREATED  |
                   JNotify.FILE_DELETED  |
                   JNotify.FILE_MODIFIED |
                   JNotify.FILE_RENAMED;
        
        int watchId = JNotify.addWatch(driveDir, mask, true, new DirectoryListener());
        S3PollingService pollService = new S3PollingService();
        Thread pollingThread = new Thread(pollService);
        pollingThread.start();
        
        System.out.println("Listening directory : " + driveDir);
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
}
