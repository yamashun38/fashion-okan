package myapp.fashion.dto.item;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * アイテムDTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ItemDto extends BaseDto {

    private MultipartFile file;
}
