package myapp.fashion.controller.entry;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import myapp.fashion.commom.exception.BusinessException;
import myapp.fashion.dto.ItemDto;
import myapp.fashion.dto.TagsDto;
import myapp.fashion.logic.entry.ItemEntryLogic;
import myapp.fashion.logic.tags.TagsLogic;

@Controller
public class ItemEntryController {

    @Autowired
    ItemEntryLogic itemEntryLogic;

    @Autowired
    TagsLogic tagsLogic;

    /**
     * アイテム登録画面表示
     * 
     * @param model モデルオブジェクト
     * @return アイテム登録画面
     * @throws BusinessException ビジネスロジックでエラーが発生した場合
     */
    @GetMapping("/item-entry")
    public String showItemEntryPage(Model model) {

        try {
            ItemDto itemDto = new ItemDto();
            itemDto.setDate(LocalDate.now());

            List<TagsDto> allTags = tagsLogic.findAllTags();

            model.addAttribute("itemDto", itemDto);
            model.addAttribute("allTags", allTags);

            return "items/item-entry";

        } catch (BusinessException e) {
            // ビジネスロジックでエラーが発生した場合、エラーメッセージを表示
            model.addAttribute("errorMessage", e.getMessage());

            return "items/item-entry";
        }
    }

    /**
     * アイテム登録処理
     * 
     * @param itemDto            アイテム情報
     * @param bindingResult      バリデーション結果
     * @param model              モデルオブジェクト
     * @param redirectAttributes リダイレクト属性
     * @return アイテム一覧画面へリダイレクト
     * @throws BusinessException ビジネスロジックでエラーが発生した場合
     */
    @PostMapping("/item-entry")
    public String itemEntry(@Valid ItemDto itemDto, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {

        itemDto.setUserId(0); // ユーザーIDはいったん0で初期化（ログイン機能実装後に削除）

        // アップロードされたファイルを取得
        MultipartFile file = itemDto.getFile();

        // ファイル未選択の場合はバリデーションエラーに追加
        if (file == null || file.isEmpty()) {
            bindingResult.rejectValue("file", "file.empty", "ファイルは必須です");
        }

        List<TagsDto> allTags = null;
        try {

            // 全てのタグを取得
            allTags = tagsLogic.findAllTags();

            // バリデーションエラーがあれば、エラーメッセージを表示
            if (bindingResult.hasErrors()) {

                model.addAttribute("allTags", allTags);
                return "items/item-entry";
            }

            // S3にアップロードしてファイルのURLを取得
            String s3Url = itemEntryLogic.uploadItemToS3(file);
            itemDto.setS3Url(s3Url);

            // アイテムを登録
            itemEntryLogic.entryItem(itemDto);

            // 登録完了メッセージをリダイレクト属性に追加し、アイテム一覧画面へリダイレクト
            redirectAttributes.addFlashAttribute("flashMessage", "登録が完了しました");
            return "redirect:/all-items";

        } catch (BusinessException e) {

            // アイテム登録中にエラーが発生した場合、空のリストを設定
            allTags = Collections.emptyList();

            // ビジネスロジックでエラーが発生した場合、エラーメッセージを表示
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("allTags", allTags);

            // エラーが発生した場合はアップロード画面に戻る
            return "items/item-entry";
        }
    }
}
