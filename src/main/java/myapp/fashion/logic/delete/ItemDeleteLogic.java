package myapp.fashion.logic.delete;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import myapp.fashion.commom.exception.BusinessException;
import myapp.fashion.dto.item.ItemDto;
import myapp.fashion.mapper.ItemMapper;
import myapp.fashion.mapper.TagsMapper;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Service
@Slf4j
public class ItemDeleteLogic {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private TagsMapper tagsMapper;

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    /**
     * S3クライアントを初期化
     * 
     * @param s3Client S3クライアント
     */
    public ItemDeleteLogic(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * アイテムを削除
     * 
     * @param itemDto
     * @throws BusinessException 削除中にエラーが発生した場合
     */
    @Transactional
    public void deleteItem(ItemDto itemDto) {

        Integer itemId = itemDto.getItemId();
        try {
            // item_tagsテーブルからレコードを削除
            tagsMapper.deleteItemTagsByItemId(itemId);

            // itemテーブルからレコードを削除
            itemMapper.deleteItemById(itemId);

            // DBからファイル削除が成功したらS3からもファイルを削除
            // S3のURLからファイル名を取得
            String fileName = this.getFileNameFromUrl(itemDto.getS3Url());
            this.deleteItemFromS3(fileName);

        } catch (DataIntegrityViolationException e) {
            // 参照整合性違反が発生した場合、関連するタグ情報が存在する可能性がある
            log.error("アイテム削除に失敗しました（整合性違反） itemId=", itemId, e);
            throw new BusinessException("このアイテムは削除できません。関連するタグ情報が存在する可能性があります");

        } catch (Exception e) {
            log.error("アイテム削除中にエラーが発生しました itemId={}", itemId, e);
            throw new BusinessException("アイテム削除中にエラーが発生しました");
        }
    }

    /**
     * URLからファイル名を取得
     * 
     * @param url ファイルのURL
     * @return ファイル名
     */
    private String getFileNameFromUrl(String url) {

        try {
            // URLがnullまたは空の場合
            if (!StringUtils.hasText(url)) {
                // エラーを投げる
                throw new BusinessException("指定したファイルが存在しません");
            }

            // 最後のスラッシュ以降の部分を取得
            return url.substring(url.lastIndexOf("/") + 1);

        } catch (Exception e) {
            log.error("URLからファイル名の抽出に失敗しました url={}", url, e);
            throw new BusinessException("S3ファイルのパスが不正です", e);
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
