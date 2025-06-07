package myapp.fashion.dto.item;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * アイテム検索用DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchItemDto extends BaseDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer selectedTagCount;
}
