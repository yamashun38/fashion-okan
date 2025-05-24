package myapp.fashion.logic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import myapp.fashion.commom.exception.BusinessException;
import myapp.fashion.converter.GenericConverter;
import myapp.fashion.dto.ItemDto;
import myapp.fashion.dto.SearchItemDto;
import myapp.fashion.entity.ItemEntity;
import myapp.fashion.mapper.ItemMapper;
import myapp.fashion.mapper.SearchItemMapper;
import myapp.fashion.mapper.TagsMapper;

@Component
@Service
@Slf4j
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
     * @return 全アイテムIDとS3のURL
     * @throws BusinessException 取得中にエラーが発生した場合
     */
    public List<ItemDto> findAllItems() {

        try {
            // 全アイテムを取得しDTOに変換して返す
            // アイテムが登録されていない場合は空のリストを返す
            return genericConverter.convertList(itemMapper.findAllItems(), ItemDto.class);

        } catch (Exception e) {
            log.error("アイテム情報の取得に失敗しました", e);
            throw new BusinessException("アイテム情報の取得中にエラーが発生しました");
        }
    }

    /**
     * 指定されたIDのアイテムを取得
     * 
     * @param itemId 情報取得対象のアイテムID
     * @return アイテム情報
     * @throws BusinessException アイテムが見つからない場合や情報の取得中にエラーが発生した場合
     */
    public ItemDto findItemById(Integer itemId) {

        try {
            // アイテムを取得
            ItemEntity itemInfo = itemMapper.findItemById(itemId);

            // アイテムが存在しない場合は例外をスロー
            if (itemInfo == null) {
                log.error("アイテムが見つかりません itemId={}", itemId);
                throw new BusinessException("アイテムが存在しません");
            }

            // 取得したアイテム情報をDTOに変換して返す
            return genericConverter.convert(itemInfo, ItemDto.class);

        } catch (Exception e) {
            log.error("アイテム情報の取得に失敗しました itemId={}", itemId, e);
            throw new BusinessException("アイテム情報の取得中にエラーが発生しました");
        }
    }

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
            if (itemDto.getSelectedTagIds() != null && !itemDto.getSelectedTagIds().isEmpty()) {
                for (Integer tagId : itemDto.getSelectedTagIds()) {
                    tagsMapper.insertItemTags(itemDto.getItemId(), tagId);
                }
            }

        } catch (Exception e) {
            log.error("予期せぬエラーが発生しました itemId={}, itemName={}", itemDto.getItemId(), itemDto.getItemName(), e);
            throw new BusinessException("アイテム情報変更中にエラーが発生しました");
        }
    }

    /**
     * アイテム検索
     * 
     * @param searchItemDto 検索条件
     * @return 検索結果のアイテムIDとS3のURL
     * @throws BusinessException 検索中にエラーが発生した場合
     */
    public List<ItemDto> searchItems(SearchItemDto searchItemDto) {

        try {
            // 検索結果をDTOに変換して返す
            // 検索結果が0件の場合は空のリストを返す
            return genericConverter.convertList(searchItemMapper.searchItems(searchItemDto), ItemDto.class);

        } catch (Exception e) {
            log.error("アイテム検索に失敗しました searchCondition={}", searchItemDto, e);
            throw new BusinessException("アイテム検索中にエラーが発生しました");
        }
    }

    /**
     * アイテムを登録
     * 
     * @param itemDto 登録するアイテム情報
     * @throws BusinessException 登録中にエラーが発生した場合
     */
    @Transactional
    public void insertItem(ItemDto itemDto) {

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
            log.error("予期せぬエラーが発生しました itemId={}, itemName={}", itemDto.getItemId(), itemDto.getItemName(), e);
            throw new BusinessException("アイテム登録中にエラーが発生しました");
        }
    }

    /**
     * アイテムを削除
     * 
     * @param itemId 削除対象のアイテムID
     * @throws BusinessException 関連データが存在するなどで削除できない場合
     */
    public void deleteItemById(Integer itemId) {

        try {
            // item_tagsテーブルからレコードを削除
            tagsMapper.deleteItemTagsByItemId(itemId);

            // itemテーブルからレコードを削除
            itemMapper.deleteItemByItemId(itemId);

        } catch (DataIntegrityViolationException e) {
            // 参照整合性違反が発生した場合、関連するタグ情報が存在する可能性がある
            log.error("アイテム削除に失敗しました itemId=" + itemId, e);
            throw new BusinessException("このアイテムは削除できません。関連するタグ情報が存在する可能性があります");

        } catch (Exception e) {
            log.error("予期せぬエラーが発生しました itemId=" + itemId, e);
            throw new BusinessException("アイテム削除中にエラーが発生しました");
        }
    }
}
