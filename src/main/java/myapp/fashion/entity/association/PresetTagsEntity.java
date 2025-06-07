package myapp.fashion.entity.association;

import lombok.Data;

/**
 * プリセットとタグの関連付けエンティティ
 */
@Data
public class PresetTagsEntity {

    private Integer presetId;
    private Integer tagId;
}
