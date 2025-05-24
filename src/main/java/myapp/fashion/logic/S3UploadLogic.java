package myapp.fashion.logic;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import myapp.fashion.commom.exception.BusinessException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@Service
@Slf4j
public class S3UploadLogic {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    /**
     * S3クライアントを初期化
     * 
     * @param accessKey AWSのアクセスキー
     * @param secretKey AWSのシークレットキー
     * @param region    AWSのリージョン
     */
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
     * 
     * @param file アップロードするファイル
     * @return アップロードしたファイルのURL
     * @throws BusinessException ファイルが存在しない場合やアップロード中にエラーが発生した場合
     */
    public String uploadItem(MultipartFile file) {

        // ファイルが存在しない場合は例外をスロー
        if (file == null || file.isEmpty()) {
            throw new BusinessException("ファイルが存在しません");
        }

        // ファイル名を取得して拡張子を抽出
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                : "";

        // UUIDを使用して一意のファイル名を生成
        String fileName = UUID.randomUUID().toString() + extension;

        // S3にファイルをアップロード
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        try {
            s3Client.putObject(putRequest, software.amazon.awssdk.core.sync.RequestBody
                    .fromInputStream(file.getInputStream(), file.getSize()));

            // S3にアップロードしたファイルのURLを返す
            return "https://" + bucketName + ".s3.ap-northeast-1.amazonaws.com/" + fileName;

        } catch (Exception e) {
            log.error("S3へのファイルアップロードに失敗しました fileName={}", fileName, e);
            throw new BusinessException("ファイルのアップロード中にエラーが発生しました", e);
        }
    }

    /**
     * S3からファイルを削除
     * 
     * @param fileName S3から削除するファイル名
     * @throws BusinessException 削除中にエラーが発生した場合
     */
    public void deleteItemFromS3(String fileName) {

        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteRequest);

        } catch (Exception e) {
            log.error("S3からのファイル削除に失敗しました fileName={}", fileName, e);
            throw new BusinessException("ファイルの削除中にエラーが発生しました", e);
        }
    }
}
