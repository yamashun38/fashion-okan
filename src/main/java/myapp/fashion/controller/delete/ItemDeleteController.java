package myapp.fashion.controller.delete;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import myapp.fashion.commom.exception.BusinessException;
import myapp.fashion.dto.item.ItemDto;
import myapp.fashion.logic.delete.ItemDeleteLogic;
import myapp.fashion.logic.fetch.ItemFetchLogic;

@Controller
public class ItemDeleteController {

    @Autowired
    private ItemFetchLogic itemFetchLogic;

    @Autowired
    private ItemDeleteLogic itemDeleteLogic;

    /**
     * アイテム削除処理
     * 
     * @param itemId             アイテムID
     * @param model              モデルオブジェクト
     * @param redirectAttributes リダイレクト属性
     * @return アイテム一覧画面へリダイレクト
     * @throws BusinessException ビジネスロジックでエラーが発生した場合
     */
    @GetMapping("/delete-item/{id}")
    public String deleteItemById(@PathVariable("id") Integer itemId, Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // S3からファイルを削除
            // まずDBから該当のアイテム情報を取得（ファイル名を得るため）
            ItemDto deleteItem = itemFetchLogic.findItemById(itemId);

            // アイテムを削除(DBから削除→ S3から削除)
            itemDeleteLogic.deleteItem(deleteItem);

            // 削除完了メッセージをリダイレクト属性に追加し、アイテム一覧画面へリダイレクト
            redirectAttributes.addFlashAttribute("flashMessage", "削除が完了しました");
            return "redirect:/all-items";

        } catch (BusinessException e) {
            // ビジネスロジックでエラーが発生した場合、エラーメッセージを表示
            model.addAttribute("errorMessage", e.getMessage());

            return "items/item-detail";
        }
    }
}
