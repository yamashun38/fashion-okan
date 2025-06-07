package myapp.fashion.entity.association;

import lombok.Data;

/**
 * プリセットとアイテムの関連付けエンティティ
 */
@Data
public class PresetItemsEntity {

    private Integer presetId;
    private Integer itemId;
}
