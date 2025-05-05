package myapp.fashion.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import myapp.fashion.dto.ItemDto;
import myapp.fashion.entity.ItemEntity;

@Component
public class ItemConverter {

    @Autowired
    private ModelMapper modelMapper;

    /**
     * EntityからDTOに変換
     * 
     * @param itemEntity
     * @return ItemDto
     */
    public ItemDto toDto(ItemEntity itemEntity) {
        return modelMapper.map(itemEntity, ItemDto.class);
    }

    /**
     * EntityのリストからDTOのリストに変換
     * 
     * @param itemEntityList
     * @return List<ItemDto>
     */
    public List<ItemDto> toDto(List<ItemEntity> itemEntityList) {
        return itemEntityList.stream()
                    .map(entity -> modelMapper.map(entity, ItemDto.class))
                    .collect(Collectors.toList());
    }

    /**
     * DTOからEntityに変換
     * 
     * @param itemDto
     * @return ItemEntity
     */
    public ItemEntity toEntity(ItemDto itemDto) {
        return modelMapper.map(itemDto, ItemEntity.class);
    }

    /**
     * DTOのリストからEntityのリストに変換
     * 
     * @param itemEntityList
     * @return List<ItemDto>
     */
    public List<ItemEntity> toEntity(List<ItemDto> itemDtoList) {
        return itemDtoList.stream()
                    .map(dto -> modelMapper.map(dto, ItemEntity.class))
                    .collect(Collectors.toList());
    }
}
