import java.io.File;
import java.io.IOException;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
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
        s3.putObject(bucketName, key, file);
    }
    
    public void getFile(String bucketName, String key) {
        S3Object obj = s3.getObject(bucketName, key);
        ObjectMetadata meta = obj.getObjectMetadata();
    }
    
    public List<S3ObjectSummary> listFiles(String bucketName) {
        ObjectListing list = s3.listObjects(bucketName);
        return list.getObjectSummaries();
    }
}
