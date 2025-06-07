package myapp.fashion.controller.tags;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import myapp.fashion.dto.tag.TagsDto;
import myapp.fashion.logic.tags.TagsLogic;

@Controller
public class TagsController {

    @Autowired
    private TagsLogic tagsLogic;

    /**
     * タグ設定画面表示
     */
    @GetMapping("/tags-setting")
    public String tagsSetting(Model model) {

        // タグ一覧を取得
        List<TagsDto> allTags = tagsLogic.findAllTags();

        model.addAttribute("allTags", allTags);
        return "tags/tags-setting";
    }

    /**
     * タグ情報変更
     */
    @PostMapping("/tags-modify")
    public String tagsModify(@Valid TagsDto tagsDto, BindingResult bindingResult, RedirectAttributes redirectAttributes,
            Model model) {

        // タグ情報を変更
        // tagsLogic.updateTags(tagsDto);

        redirectAttributes.addFlashAttribute("flashMessage", "変更が完了しました");
        return "redirect:/tags-setting";
    }
}
