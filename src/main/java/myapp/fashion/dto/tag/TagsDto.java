package myapp.fashion.dto.tag;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * タグDTO
 */
@Data
public class TagsDto {

    private Integer tagId;

    @NotBlank(message = "タグ名は必須です")
    private String tagName;
}
