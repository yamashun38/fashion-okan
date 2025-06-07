package myapp.fashion.dto.association;

import lombok.Data;

/**
 * プリセットとタグの関連付けDTO
 */
@Data
public class PresetTagsDto {

    private Integer presetId;
    private Integer tagId;
}
