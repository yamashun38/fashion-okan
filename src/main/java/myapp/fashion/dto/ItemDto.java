package myapp.fashion.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemDto extends BaseDto {

    private MultipartFile file;
}
