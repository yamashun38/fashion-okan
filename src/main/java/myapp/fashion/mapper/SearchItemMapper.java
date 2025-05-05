package myapp.fashion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import myapp.fashion.dto.SearchItemDto;
import myapp.fashion.entity.SearchItemEntity;

@Mapper
public interface SearchItemMapper {

    /**
     * アイテム検索
     */
    List<SearchItemEntity> searchItems(SearchItemDto searchItemDto);
}
