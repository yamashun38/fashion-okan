package myapp.fashion.dto.item;

import java.time.LocalDate;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

/**
 * アイテム共通DTO
 */
@Data
public class BaseDto {

    private Integer userId;
    private Integer itemId;
    private String s3Url;

    @NotBlank(message = "アイテム名は必須です")
    @Size(max = 45, message = "アイテム名は45文字以内で入力してください")
    private String itemName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Size(max = 200, message = "説明は200文字以内で入力してください")
    private String description;
    private Integer favorite;
    private List<Integer> selectedTagIds;
}
