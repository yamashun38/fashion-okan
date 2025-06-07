package myapp.fashion.controller.udpate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import myapp.fashion.commom.exception.BusinessException;
import myapp.fashion.dto.item.ItemDto;
import myapp.fashion.logic.fetch.ItemFetchLogic;
import myapp.fashion.logic.tags.TagsLogic;
import myapp.fashion.logic.update.ItemUpdateLogic;

@Controller
public class ItemUpdateController {

    @Autowired
    private ItemFetchLogic itemFetchLogic;

    @Autowired
    private ItemUpdateLogic itemUpdateLogic;

    @Autowired
    private TagsLogic tagsLogic;

    /**
     * アイテム情報更新処理
     * 
     * @param itemDto            アイテム情報
     * @param bindingResult      バリデーション結果
     * @param redirectAttributes リダイレクト属性
     * @param model              モデルオブジェクト
     * @return アイテム一覧画面へリダイレクト
     * @throws BusinessException ビジネスロジックでエラーが発生した場合
     */
    @PostMapping("/item-update")
    public String itemUpdate(@Valid ItemDto itemDto, BindingResult bindingResult, RedirectAttributes redirectAttributes,
            Model model) {

        try {
            // バリデーションエラーがあれば、エラーメッセージを表示
            if (bindingResult.hasErrors()) {

                String s3Url = itemFetchLogic.findItemById(itemDto.getItemId()).getS3Url();
                itemDto.setS3Url(s3Url);

                model.addAttribute("itemDto", itemDto);
                model.addAttribute("selectedTags", tagsLogic.findTagsByItemId(itemDto.getItemId()));
                model.addAttribute("allTags", tagsLogic.findAllTags());
                model.addAttribute("editMode", true);

                return "items/item-detail";
            }

            // DBを更新
            itemUpdateLogic.updateItem(itemDto);

            // 更新完了メッセージをリダイレクト属性に追加し、アイテム一覧画面へリダイレクト
            redirectAttributes.addFlashAttribute("flashMessage", "変更が完了しました");
            return "redirect:/all-items";

        } catch (BusinessException e) {
            // ビジネスロジックでエラーが発生した場合、エラーメッセージを表示
            model.addAttribute("errorMessage", e.getMessage());

            return "items/item-detail";
        }
    }
}
