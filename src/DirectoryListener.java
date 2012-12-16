import java.io.File;
import java.io.IOException;

import net.contentobjects.jnotify.JNotifyListener;


public class DirectoryListener implements JNotifyListener {
    
    private S3LocalDrive drive;
    private S3Client s3;
    
    public DirectoryListener(S3LocalDrive drive) {
        this.drive = drive;
        s3 = S3Client.getInstance();
    }
    
    private void uploadFile(String rootPath, String name) {
        String absolutePath = rootPath + File.separator + name;
        File file = new File(absolutePath);
        if (file.isDirectory()) {
            try {
                file = File.createTempFile(name, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            name += "/";
        }
        s3.putFile(drive.getUserBucketName(), name, file);
        drive.addFileName(absolutePath);
    }
    
    private void deleteFile(String rootPath, String name) {
        String absolutePath = rootPath + File.separator + name;
        File file = new File(absolutePath);
        if (file.isDirectory()) {
            name += "/";
        }
        s3.deleteFile(drive.getUserBucketName(), name);
        drive.removeFileName(absolutePath);
    }
    
    private void renameFile(String rootPath, String oldName, String newName) {
        String newAbsolutePath = rootPath + File.separator + newName;
        String oldAbsolutePath = rootPath + File.separator + oldName;
        File newFile = new File(newAbsolutePath);
        if (newFile.isDirectory()) {
            try {
                newFile = File.createTempFile(newName, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            oldName += "/";
            newName += "/";
        }
        s3.deleteFile(drive.getUserBucketName(), oldName);
        drive.removeFileName(oldAbsolutePath);
        s3.putFile(drive.getUserBucketName(), newName, newFile);
        drive.addFileName(newAbsolutePath);
    }
    
    private boolean isFromS3(String key) {
        return drive.getPollService().isModifiedFromS3(key);
    }
    
    @Override
    public void fileCreated(int wd, String rootPath, String name) {
        if (!isFromS3(name)) {
            uploadFile(rootPath, name);
            System.out.println("File created: " + name);
        } else {
            drive.getPollService().removeModificationFromS3(name);
            System.out.println("File " + name + " comes from s3, ignore.");
        }
    }

    @Override
    public void fileDeleted(int wd, String rootPath, String name) {
        if (!isFromS3(name)) {
            deleteFile(rootPath, name);
            System.out.println("File deleted: " + name);
        } else {
            drive.getPollService().removeModificationFromS3(name);
            System.out.println("File " + name + " comes from s3, ignore.");
        }
    }

    @Override
    public void fileModified(int wd, String rootPath, String name) {
        if (!isFromS3(name)) {
            uploadFile(rootPath, name);
            System.out.println("File modified: " + name);
        } else {
            drive.getPollService().removeModificationFromS3(name);
            System.out.println("File " + name + " comes from s3, ignore.");
        }
    }

    @Override
    public void fileRenamed(int wd, String rootPath, String oldName, String newName) {
        if (!isFromS3(newName)) {
            renameFile(rootPath, oldName, newName);
            System.out.println("File renamed: " + oldName + " -> " + newName);
        } else {
            drive.getPollService().removeModificationFromS3(newName);
            System.out.println("File " + newName + " comes from s3, ignore.");
        }
    }
    
}
