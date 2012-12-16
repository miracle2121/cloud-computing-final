import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3Client {
    
    private AmazonS3Client s3;
    private static S3Client instance;
    
    private S3Client() {
        try {
            AWSCredentials credentials = new PropertiesCredentials(
                    S3Client.class.getResourceAsStream("AwsCredentials.properties"));
            s3 = new AmazonS3Client(credentials);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static S3Client getInstance() {
        if (instance == null) {
            synchronized (S3Client.class) {
                if (instance == null) {
                    instance = new S3Client();
                }
            }
        }
        return instance;
    }
    
    public void putFile(String bucketName, String key, File file) {        
        try {
            s3.putObject(bucketName, key, file);
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (AmazonClientException e) {
            e.printStackTrace();
        }
    }
    
    public void saveFile(String bucketName, String key, String dir) {
        try {
            S3Object obj = s3.getObject(bucketName, key);
            String filePath = dir + File.separator + obj.getKey();
            File newFile = new File(filePath);
            if (filePath.endsWith("/")) {
                newFile.mkdir();
            } else {
                S3ObjectInputStream ois = null;
                BufferedOutputStream bos = null;
                try {
                    byte[] buffer = new byte[4096];
                    ois = obj.getObjectContent();
                    bos = new BufferedOutputStream(new FileOutputStream(newFile));
                    while (ois.read(buffer) != -1) {
                        bos.write(buffer);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        ois.close();
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (AmazonClientException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteFile(String bucketName, String key) {
        try {
            s3.deleteObject(bucketName, key);
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (AmazonClientException e) {
            e.printStackTrace();
        }
    }
    
    public List<S3ObjectSummary> listFiles(String bucketName) {
        List<S3ObjectSummary> result = new ArrayList<S3ObjectSummary>();
        try {
            ObjectListing listing = s3.listObjects(bucketName);
            result.addAll(listing.getObjectSummaries());
            while (listing.isTruncated()) {
                listing = s3.listObjects(bucketName);
                result.addAll(listing.getObjectSummaries());
            }
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (AmazonClientException e) {
            e.printStackTrace();
        }
        return result;
    }
}
