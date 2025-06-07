package myapp.fashion.logic.update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import myapp.fashion.commom.exception.BusinessException;
import myapp.fashion.dto.item.ItemDto;
import myapp.fashion.mapper.ItemMapper;
import myapp.fashion.mapper.TagsMapper;

@Service
@Slf4j
public class ItemUpdateLogic {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private TagsMapper tagsMapper;

    /**
     * アイテム情報を更新
     * 
     * @param itemDto 更新するアイテム情報
     * @throws BusinessException 更新中にエラーが発生した場合
     */
    @Transactional
    public void updateItem(ItemDto itemDto) {

        try {
            // お気に入りのチェックが入っていない場合は0をセット
            if (itemDto.getFavorite() == null) {
                itemDto.setFavorite(0);
            }
            // アイテムを更新
            itemMapper.updateItem(itemDto);

            // item_tagsテーブルからレコードを削除
            tagsMapper.deleteItemTagsByItemId(itemDto.getItemId());

            // 選択されたタグをitem_tagsテーブルに再登録
            if (!CollectionUtils.isEmpty(itemDto.getSelectedTagIds())) {
                for (Integer tagId : itemDto.getSelectedTagIds()) {
                    tagsMapper.insertItemTags(itemDto.getItemId(), tagId);
                }
            }

        } catch (Exception e) {
            log.error("アイテム情報変更中にエラーが発生しました itemId={}, itemName={}", itemDto.getItemId(), itemDto.getItemName(), e);
            throw new BusinessException("アイテム情報変更中にエラーが発生しました");
        }
    }
}
