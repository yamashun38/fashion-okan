package myapp.fashion.dto.association;

import lombok.Data;

/**
 * プリセットとアイテムの関連付けDTO
 */
@Data
public class PresetItemsDto {

    private Integer presetId;
    private Integer itemId;
}
