package myapp.fashion.logic.tags;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import myapp.fashion.commom.exception.BusinessException;
import myapp.fashion.converter.GenericConverter;
import myapp.fashion.dto.tag.TagsDto;
import myapp.fashion.mapper.TagsMapper;

@Component
@Service
@Slf4j
public class TagsLogic {

    @Autowired
    private TagsMapper tagsMapper;

    @Autowired
    private GenericConverter genericConverter;

    /**
     * 全タグを取得
     * 
     * @return 全タグ情報
     * @throws BusinessException 取得中にエラーが発生した場合
     */
    public List<TagsDto> findAllTags() {

        try {
            // 全タグを取得しDTOに変換して返す
            return genericConverter.convertList(tagsMapper.findAllTags(), TagsDto.class);

        } catch (Exception e) {
            log.error("タグの取得に失敗しました", e);
            throw new BusinessException("タグの取得中にエラーが発生しました");
        }
    }

    /**
     * 指定されたアイテムIDに登録されているタグを取得
     * 
     * @param itemId アイテムID
     * @return アイテムIDに登録されているタグ情報
     * @throws BusinessException 取得中にエラーが発生した場合
     */
    public List<TagsDto> findTagsByItemId(Integer itemId) {

        try {
            // アイテムIDに登録されているタグを取得しDTOに変換して返す
            // タグが登録されていない場合は空のリストを返す
            return genericConverter.convertList(tagsMapper.findTagsByItemId(itemId), TagsDto.class);

        } catch (Exception e) {
            log.error("アイテムIDに登録されているタグの取得に失敗しました itemId={}", itemId, e);
            throw new BusinessException("アイテムIDに登録されているタグの取得中にエラーが発生しました");
        }
    }

    /**
     * 指定されたタグIDに登録されているタグを削除
     * 
     * @param itemId
     * @throws BusinessException 削除中にエラーが発生した場合
     */
    public void deleteItemTagsByItemId(Integer itemId) {

        try {
            // アイテムIDに登録されているタグを削除
            tagsMapper.deleteItemTagsByItemId(itemId);

        } catch (Exception e) {
            log.error("アイテムIDに登録されているタグの削除に失敗しました itemId={}", itemId, e);
            throw new BusinessException("アイテムIDに登録されているタグの削除中にエラーが発生しました");
        }
    }
}
