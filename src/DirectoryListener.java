import net.contentobjects.jnotify.JNotifyListener;


public class DirectoryListener implements JNotifyListener {
    
    private S3LocalDrive drive;
    private S3Client s3;
    
    public DirectoryListener(S3LocalDrive drive) {
        this.drive = drive;
        s3 = S3Client.getInstance();
    }
    
    @Override
    public void fileCreated(int wd, String rootPath, String name) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void fileDeleted(int wd, String rootPath, String name) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void fileModified(int wd, String rootPath, String name) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void fileRenamed(int wd, String rootPath, String oldName, String newName) {
        // TODO Auto-generated method stub
        
    }
    
}
