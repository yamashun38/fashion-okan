package myapp.fashion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import myapp.fashion.entity.tag.TagsEntity;

@Mapper
public interface TagsMapper {

    /**
     * 全タグを取得
     */
    List<TagsEntity> findAllTags();

    /**
     * アイテム登録時に選択されたタグを登録
     */
    void insertItemTags(@Param("itemId") Integer itemId, @Param("tagsId") Integer tagId);

    /**
     * 指定されたアイテムIDに登録されているタグを取得
     */
    List<TagsEntity> findTagsByItemId(Integer itemId);

    /**
     * 指定されたアイテムIDに登録されているタグを削除
     */
    void deleteItemTagsByItemId(Integer itemId);
}
