package myapp.fashion.dto.association;

import lombok.Data;

/**
 * アイテムとタグの関連付けDTO
 */
@Data
public class ItemTagsDto {

    private Integer itemId;
    private Integer tagId;
}
