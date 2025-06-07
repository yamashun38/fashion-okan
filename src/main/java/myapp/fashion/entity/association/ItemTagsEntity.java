package myapp.fashion.entity.association;

import lombok.Data;

/**
 * アイテムとタグの関連付けエンティティ
 */
@Data
public class ItemTagsEntity {

    private Integer itemId;
    private Integer tagId;
}
