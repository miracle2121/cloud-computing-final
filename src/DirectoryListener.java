import net.contentobjects.jnotify.JNotifyListener;


public class DirectoryListener implements JNotifyListener {
    
    private S3LocalDrive drive;
    
    public DirectoryListener(S3LocalDrive drive) {
        this.drive = drive;
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
