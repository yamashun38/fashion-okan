package myapp.fashion.logic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import myapp.fashion.converter.GenericConverter;
import myapp.fashion.dto.ItemDto;
import myapp.fashion.dto.SearchItemDto;
import myapp.fashion.mapper.ItemMapper;
import myapp.fashion.mapper.SearchItemMapper;
import myapp.fashion.mapper.TagsMapper;

@Component
@Service
public class FashionLogic {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private TagsMapper tagsMapper;

    @Autowired
    private SearchItemMapper searchItemMapper;

    @Autowired
    private GenericConverter genericConverter;

    /**
     * 全アイテムを取得
     * 
     * @return itemId, s3Url
     */
    public List<ItemDto> findAllItems() {

        return genericConverter.convertList(itemMapper.findAllItems(), ItemDto.class);
    }

    /**
     * 指定されたIDのアイテムを取得
     * 
     * @param itemId
     */
    public ItemDto findItemById(Integer itemId) {

        // アイテムを取得してDTOに変換して返す
        return genericConverter.convert(itemMapper.findItemById(itemId), ItemDto.class);
    }

    /**
     * 指定されたIDのアイテム情報を更新
     * 
     * @param itemDto
     */
    @Transactional
    public void updateItem(ItemDto itemDto) {

        // お気に入りのチェックが入っていない場合は0をセット
        if (itemDto.getFavorite() == null) {
            itemDto.setFavorite(0);
        }
        // アイテムを更新
        itemMapper.updateItem(itemDto);

        // アイテムに登録されているタグを削除
        tagsMapper.deleteTagsByItemId(itemDto.getItemId());

        // 選択されたタグをitem_tagsテーブルに再登録
        if (itemDto.getSelectedTagIds() != null && !itemDto.getSelectedTagIds().isEmpty()) {
            for (Integer tagId : itemDto.getSelectedTagIds()) {
                tagsMapper.insertItemTags(itemDto.getItemId(), tagId);
            }
        }
    }

    /**
     * アイテム検索
     * 
     * @param searchItemDto
     * @return itemId, s3Url
     */
    public List<ItemDto> searchItems(SearchItemDto searchItemDto) {

        return genericConverter.convertList(searchItemMapper.searchItems(searchItemDto), ItemDto.class);
    }

    /**
     * アイテムを登録
     * 
     * @param itemDto
     */
    @Transactional
    public void insertItem(ItemDto itemDto) {

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
    }

    /**
     * アイテムを削除
     * 
     * @param itemId
     */
    public void deleteItemById(Integer itemId) {

        itemMapper.deleteItemById(itemId);
    }
}
