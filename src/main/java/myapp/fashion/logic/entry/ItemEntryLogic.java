package myapp.fashion.logic.entry;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import myapp.fashion.commom.exception.BusinessException;
import myapp.fashion.dto.item.ItemDto;
import myapp.fashion.mapper.ItemMapper;
import myapp.fashion.mapper.TagsMapper;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@Slf4j
public class ItemEntryLogic {

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
    public ItemEntryLogic(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * S3にファイルをアップロード
     * 
     * @param file アップロードするファイル
     * @return アップロードしたファイルのURL
     * @throws BusinessException ファイルが存在しない場合やアップロード中にエラーが発生した場合
     */
    public String uploadItemToS3(MultipartFile file) {

        // ファイルが存在しない場合は例外をスロー
        if (file == null || file.isEmpty()) {
            throw new BusinessException("ファイルが存在しません");
        }

        // ファイル名を取得して拡張子を抽出
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {

            // ファイル名が空またはnullの場合は例外をスロー
            throw new BusinessException("ファイル名が不正です");
        }

        // ファイル名から拡張子を抽出
        String extension = extractExtension(originalFilename);

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
            // return "https://" + bucketName + ".s3.ap-northeast-1.amazonaws.com/" +
            // fileName;
            return s3Client.utilities()
                    .getUrl(GetUrlRequest.builder().bucket(bucketName).key(fileName).build())
                    .toExternalForm();

        } catch (Exception e) {
            log.error("S3へのファイルアップロードに失敗しました fileName={}", fileName, e);
            throw new BusinessException("ファイルのアップロード中にエラーが発生しました", e);
        }
    }

    /**
     * ファイル名から拡張子を抽出
     * 
     * @param fileName
     * @return ファイル名から抽出した拡張子
     */
    private String extractExtension(String fileName) {

        // ファイル名がnullまたは空でないことを確認し、拡張子を抽出
        return fileName != null && fileName.contains(".")
                ? fileName.substring(fileName.lastIndexOf('.'))
                : "";
    }

    /**
     * アイテムを登録
     * 
     * @param itemDto 登録するアイテム情報
     * @throws BusinessException 登録中にエラーが発生した場合
     */
    @Transactional
    public void entryItem(ItemDto itemDto) {

        try {
            // お気に入りのチェックが入っていない場合は0をセット
            if (itemDto.getFavorite() == null) {
                itemDto.setFavorite(0);
            }
            // アイテムを登録
            itemMapper.insertItem(itemDto);

            // 選択されたタグをitem_tagsテーブルに登録
            if (itemDto.getSelectedTagIds() != null && !itemDto.getSelectedTagIds().isEmpty()) {
                for (Integer tagId : itemDto.getSelectedTagIds()) {
                    tagsMapper.insertItemTags(itemDto.getItemId(), tagId);
                }
            }

        } catch (Exception e) {
            log.error("アイテム登録中にエラーが発生しました itemId={}, itemName={}", itemDto.getItemId(), itemDto.getItemName(), e);
            throw new BusinessException("アイテム登録中にエラーが発生しました");
        }
    }
}
