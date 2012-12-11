import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

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
    
    public void putFile() {
        
    }
    
    public void getFile() {
        
    }
}
