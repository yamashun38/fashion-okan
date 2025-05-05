package myapp.fashion.logic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@Service
public class S3UploadLogic {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3UploadLogic(@Value("${aws.credentials.access-key}") String accessKey,
            @Value("${aws.credentials.secret-key}") String secretKey,
            @Value("${aws.region}") String region) {

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    /**
     * S3にファイルをアップロード
     */
    public String uploadItem(MultipartFile file) {

        // ファイル名を取得
        String fileName = file.getOriginalFilename();

        // S3にファイルをアップロード
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        try {
            s3Client.putObject(putRequest, software.amazon.awssdk.core.sync.RequestBody
                    .fromInputStream(file.getInputStream(), file.getSize()));
        } catch (Exception e) {
            throw new RuntimeException("登録に失敗しました", e);
        }

        // S3にアップロードしたファイルのURLを返す
        return "https://" + bucketName + ".s3.ap-northeast-1.amazonaws.com/" + fileName;
    }

    /**
     * S3からファイルを削除
     */
    public void deleteItemFromS3(String fileName) {

        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteRequest);
        } catch (Exception e) {
            throw new RuntimeException("S3からの削除に失敗しました", e);
        }
    }
}
