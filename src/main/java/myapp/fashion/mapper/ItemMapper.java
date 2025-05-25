package myapp.fashion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import myapp.fashion.dto.ItemDto;
import myapp.fashion.entity.ItemEntity;

@Mapper
public interface ItemMapper {

    /**
     * 全アイテムを取得
     */
    List<ItemEntity> findAllItems();

    /**
     * 指定されたIDのアイテムを取得
     */
    ItemEntity findItemById(Integer id);

    /**
     * 指定されたIDのアイテム情報を更新
     */
    void updateItem(ItemDto itemDto);

    /**
     * アイテムを登録
     */
    void insertItem(ItemDto itemDto);

    /**
     * itemテーブルからアイテムを削除
     */
    void deleteItemById(Integer id);
}
