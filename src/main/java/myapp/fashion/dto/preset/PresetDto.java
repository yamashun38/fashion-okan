package myapp.fashion.dto.preset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * プリセットDTO
 */
@Data
public class PresetDto {

    private Integer userId;
    private Integer presetId;

    @NotBlank(message = "プリセット名は必須です")
    @Size(max = 45, message = "プリセット名は45文字以内で入力してください")
    private String presetName;
    
    private String description;
}
