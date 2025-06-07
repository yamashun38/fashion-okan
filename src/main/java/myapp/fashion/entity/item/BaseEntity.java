package myapp.fashion.entity.item;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

/**
 * アイテム共通エンティティ
 */
@Data
public class BaseEntity {

    private Integer userId;
    private Integer itemId;
    private String s3Url;
    private String itemName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    // private String tags;
    private String description;
    private Integer favorite;
    private List<Integer> selectedTagIds;
}
