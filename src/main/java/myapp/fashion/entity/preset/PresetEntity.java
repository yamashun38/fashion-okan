package myapp.fashion.entity.preset;

import lombok.Data;

/**
 * プリセットエンティティ
 */
@Data
public class PresetEntity {

    private Integer userId;
    private Integer presetId;
    private String presetName;
    private String description;
}
