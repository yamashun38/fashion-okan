package myapp.fashion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagsDto {

    private Integer tagId;

    @NotBlank(message = "タグ名は必須です")
    private String tagName;
}
