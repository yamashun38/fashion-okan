package myapp.fashion.logic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import myapp.fashion.converter.GenericConverter;
import myapp.fashion.dto.TagsDto;
import myapp.fashion.mapper.TagsMapper;

@Component
@Service
public class TagsLogic {

    @Autowired
    private TagsMapper tagsMapper;

    @Autowired
    private GenericConverter genericConverter;

    /**
     * 全タグを取得
     * 
     * @return tagId, tagName
     */
    public List<TagsDto> findAllTags() {

        return genericConverter.convertList(tagsMapper.findAllTags(), TagsDto.class);
    }

    /**
     * 指定されたアイテムIDに登録されているタグを取得
     * 
     * @param itemId
     */
    public List<TagsDto> findTagsByItemId(Integer itemId) {

        return genericConverter.convertList(tagsMapper.findTagsByItemId(itemId), TagsDto.class);
    }

    /**
     * 指定されたタグIDに登録されているタグを削除
     * 
     * @param itemId
     */
    public void deleteTagsByItemId(Integer itemId) {

        tagsMapper.deleteTagsByItemId(itemId);
    }
}
