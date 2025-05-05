package myapp.fashion.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GenericConverter {

    @Autowired
    private ModelMapper modelMapper;

    /**
     * 任意のオブジェクトを指定した型へ変換
     */
    public <S, T> T convert(S source, Class<T> targetClass) {
        return modelMapper.map(source, targetClass);
    }

    /**
     * 任意のオブジェクトのリストを指定した型のリストに変換
     */
    public <S, T> List<T> convertList(List<S> sourceList, Class<T> targetClass) {
        return sourceList.stream()
                         .map(source -> modelMapper.map(source, targetClass))
                         .collect(Collectors.toList());
    }
}
