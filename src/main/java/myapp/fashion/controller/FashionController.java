package myapp.fashion.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import myapp.fashion.dto.ItemDto;
import myapp.fashion.dto.SearchItemDto;
import myapp.fashion.dto.TagsDto;
import myapp.fashion.logic.FashionLogic;
import myapp.fashion.logic.S3UploadLogic;
import myapp.fashion.logic.TagsLogic;

@Controller
public class FashionController {

    @Autowired
    private FashionLogic fashionLogic;

    @Autowired
    private S3UploadLogic s3UploadLogic;

    @Autowired
    private TagsLogic tagsLogic;

    ItemDto itemDto = new ItemDto();

    /**
     * メイン画面表示
     */
    @GetMapping("/main")
    public String main() {

        return "main";
    }

    /**
     * 全アイテム表示
     */
    @GetMapping("/all-items")
    public String allItems(Model model) {

        List<ItemDto> itemList = fashionLogic.findAllItems();
        List<TagsDto> allTags = tagsLogic.findAllTags();

        model.addAttribute("items", itemList);
        model.addAttribute("allTags", allTags);
        model.addAttribute("searchItemDto", new SearchItemDto());

        return "items/all-items";
    }

    /**
     * アイテム詳細表示
     */
    @GetMapping("/item-detail/{id}")
    public String itemDetail(@PathVariable("id") Integer itemId, Model model) {

        ItemDto itemDto = fashionLogic.findItemById(itemId);
        List<TagsDto> selectedTags = tagsLogic.findTagsByItemId(itemId);
        List<TagsDto> allTags = tagsLogic.findAllTags();

        model.addAttribute("itemDto", itemDto);
        model.addAttribute("selectedTags", selectedTags);
        model.addAttribute("allTags", allTags);
        return "items/item-detail";
    }

    /**
     * アイテム情報変更
     * アイテム一覧画面へリダイレクト
     */
    @PostMapping("/item-update")
    public String itemModify(@Valid ItemDto itemDto, BindingResult bindingResult, RedirectAttributes redirectAttributes,
            Model model) {

        // バリデーションエラーがあれば、エラーメッセージを表示
        if (bindingResult.hasErrors()) {

            model.addAttribute("itemDto", itemDto);
            model.addAttribute("selectedTags", tagsLogic.findTagsByItemId(itemDto.getItemId()));
            model.addAttribute("allTags", tagsLogic.findAllTags());
            return "items/item-detail";
        }

        // DBを更新
        fashionLogic.updateItem(itemDto);

        redirectAttributes.addFlashAttribute("flashMessage", "変更が完了しました");
        return "redirect:/all-items";
    }

    /**
     * アイテム検索画面
     */
    @GetMapping("/item-search")
    public String itemSearch(@ModelAttribute SearchItemDto searchItemDto, Model model) {

        List<TagsDto> allTags = tagsLogic.findAllTags();

        // 既存の検索条件があればそのまま使い、なければ新規生成
        if (searchItemDto == null) {

            searchItemDto = new SearchItemDto();
        }

        model.addAttribute("searchItemDto", searchItemDto);
        model.addAttribute("allTags", allTags);
        return "search/item-search";
    }

    /**
     * アイテム検索結果表示
     */
    @GetMapping("/item-search-result")
    public String itemSearchResult(@ModelAttribute SearchItemDto searchItemDto, Model model) {

        // 選択したタグの個数を取得
        searchItemDto.setSelectedTagCount(searchItemDto.getSelectedTagIds().size());

        // DBを検索
        List<ItemDto> serachItemresults = fashionLogic.searchItems(searchItemDto);

        model.addAttribute("searchItemsResults", serachItemresults);
        return "search/item-search-result";
    }

    /**
     * アイテムアップロード画面表示
     */
    @GetMapping("/item-upload")
    public String itemUpload(Model model) {

        List<TagsDto> allTags = tagsLogic.findAllTags();

        model.addAttribute("itemDto", new ItemDto());
        model.addAttribute("allTags", allTags);
        return "upload/item-upload";
    }

    /**
     * アイテムアップロード完了
     * アイテム一覧画面へリダイレクト
     */
    @PostMapping("/upload-result")
    public String uploadComplete(@Valid ItemDto itemDto, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {

        // アップロードされたファイルを取得
        MultipartFile file = itemDto.getFile();

        // ファイル未選択の場合はバリデーションエラーに追加
        if (file == null || file.isEmpty()) {
            bindingResult.rejectValue("file", "file.empty", "ファイルは必須です");
        }

        // バリデーションエラーがあれば、エラーメッセージを表示
        if (bindingResult.hasErrors()) {

            model.addAttribute("allTags", tagsLogic.findAllTags());
            return "upload/item-upload";
        }

        try {

            // S3にアップロードしてファイルのURLを取得
            String s3Url = s3UploadLogic.uploadItem(file);

            // DTOにファイルのURLを格納
            itemDto.setS3Url(s3Url);

            // アイテムをDBに登録
            fashionLogic.insertItem(itemDto);

        } catch (Exception e) {

            model.addAttribute("message", "登録に失敗しました");
            e.printStackTrace();
            return "items/upload-result";
        }

        redirectAttributes.addFlashAttribute("flashMessage", "登録が完了しました");
        return "redirect:/all-items";
    }

    /**
     * アイテム削除完了
     * アイテム一覧画面へリダイレクト
     */
    @GetMapping("/delete-item/{itemId}")
    public String deleteItem(@PathVariable("itemId") Integer itemId, Model model,
            RedirectAttributes redirectAttributes) {

        // S3からファイルを削除
        // まずDBから該当のアイテム情報を取得（ファイル名を得るため）
        ItemDto deleteItem = fashionLogic.findItemById(itemId);

        // S3からファイル削除
        if (deleteItem != null && deleteItem.getS3Url() != null) {

            String fileName = extractFileNameFromUrl(deleteItem.getS3Url());
            s3UploadLogic.deleteItemFromS3(fileName);
        }

        // DBをからアイテムを削除
        fashionLogic.deleteItemById(itemId);

        redirectAttributes.addFlashAttribute("flashMessage", "削除が完了しました");
        return "redirect:/all-items";
    }

    /**
     * URLからファイル名を取得
     */
    private String extractFileNameFromUrl(String url) {

        if (url == null || url.isEmpty())
            return "";
        return url.substring(url.lastIndexOf("/") + 1);
    }

}
