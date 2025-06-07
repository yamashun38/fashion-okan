package myapp.fashion.logic.fetch;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import myapp.fashion.commom.exception.BusinessException;
import myapp.fashion.converter.GenericConverter;
import myapp.fashion.dto.item.ItemDto;
import myapp.fashion.dto.item.SearchItemDto;
import myapp.fashion.entity.item.ItemEntity;
import myapp.fashion.mapper.ItemMapper;
import myapp.fashion.mapper.SearchItemMapper;

@Service
@Slf4j
public class ItemFetchLogic {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private SearchItemMapper searchItemMapper;

    @Autowired
    private GenericConverter genericConverter;

    /**
     * 全アイテムを取得
     * 
     * @return 全アイテムのDTOリスト
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
     * @return 指定したIDのアイテムDTO
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
     * アイテム検索
     * 
     * @param searchItemDto 検索条件
     * @return 検索結果のアイテムDTOリスト
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
}
