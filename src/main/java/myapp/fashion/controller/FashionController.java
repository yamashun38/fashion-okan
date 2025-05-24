package myapp.fashion.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import myapp.fashion.commom.exception.BusinessException;
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
     * 
     * @param model モデルオブジェクト
     * @return アイテム一覧画面
     * @throws BusinessException ビジネスロジックでエラーが発生した場合
     */
    @GetMapping("/all-items")
    public String allItems(Model model) {

        try {
            List<ItemDto> itemList = fashionLogic.findAllItems();
            List<TagsDto> allTags = tagsLogic.findAllTags();

            model.addAttribute("items", itemList);
            model.addAttribute("allTags", allTags);

            return "items/all-items";

        } catch (BusinessException e) {
            // ビジネスロジックでエラーが発生した場合、エラーメッセージを表示
            model.addAttribute("errorMessage", e.getMessage());

            // アイテム情報が取得できなかった場合、空のリストを表示
            // model.addAttribute("items", Collections.emptyList());
            // model.addAttribute("allTags", Collections.emptyList());

            // 元の画面に戻す
            return "items/all-items";
        }
    }

    /**
     * アイテム詳細表示
     * 
     * @param itemId アイテムID
     * @param model  モデルオブジェクト
     * @return アイテム詳細画面
     * @throws BusinessException ビジネスロジックでエラーが発生した場合
     */
    @GetMapping("/item-detail/{id}")
    public String itemDetail(@PathVariable("id") Integer itemId, Model model, RedirectAttributes redirectAttributes) {

        try {
            ItemDto itemDto = fashionLogic.findItemById(itemId);
            List<TagsDto> selectedTags = tagsLogic.findTagsByItemId(itemId);
            List<TagsDto> allTags = tagsLogic.findAllTags();

            model.addAttribute("itemDto", itemDto);
            model.addAttribute("selectedTags", selectedTags);
            model.addAttribute("allTags", allTags);

            return "items/item-detail";

        } catch (BusinessException e) {
            // ビジネスロジックでエラーが発生した場合、エラーメッセージを表示し、一覧画面へリダイレクト
            redirectAttributes.addFlashAttribute("flashMessage", e.getMessage());
            return "redirect:/all-items";
        }
    }

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
    public String itemModify(@Valid ItemDto itemDto, BindingResult bindingResult, RedirectAttributes redirectAttributes,
            Model model) {

        try {
            // バリデーションエラーがあれば、エラーメッセージを表示
            if (bindingResult.hasErrors()) {

                String s3Url = fashionLogic.findItemById(itemDto.getItemId()).getS3Url();
                itemDto.setS3Url(s3Url);

                model.addAttribute("itemDto", itemDto);
                model.addAttribute("selectedTags", tagsLogic.findTagsByItemId(itemDto.getItemId()));
                model.addAttribute("allTags", tagsLogic.findAllTags());
                model.addAttribute("editMode", true);

                return "items/item-detail";
            }

            // DBを更新
            fashionLogic.updateItem(itemDto);

            redirectAttributes.addFlashAttribute("flashMessage", "変更が完了しました");
            return "redirect:/all-items";

        } catch (BusinessException e) {
            // ビジネスロジックでエラーが発生した場合、エラーメッセージを表示
            // String s3Url = fashionLogic.findItemById(itemDto.getItemId()).getS3Url();
            // itemDto.setS3Url(s3Url);

            // model.addAttribute("itemDto", itemDto);
            // model.addAttribute("selectedTags",
            // tagsLogic.findTagsByItemId(itemDto.getItemId()));
            // model.addAttribute("allTags", tagsLogic.findAllTags());
            // model.addAttribute("editMode", true);
            model.addAttribute("errorMessage", e.getMessage());

            return "items/item-detail";
        }
    }

    /**
     * アイテム検索画面表示
     * 
     * @param searchItemDto 検索条件
     * @param model         モデルオブジェクト
     * @return アイテム検索画面
     * @throws BusinessException ビジネスロジックでエラーが発生した場合
     */
    @GetMapping("/item-search")
    public String itemSearch(@ModelAttribute SearchItemDto searchItemDto, Model model) {

        try {
            // 既存の検索条件がない場合
            if (searchItemDto == null) {

                // 空のDTOを作成
                searchItemDto = new SearchItemDto();
            }

            // 全タグを取得
            List<TagsDto> allTags = tagsLogic.findAllTags();

            model.addAttribute("searchItemDto", searchItemDto);
            model.addAttribute("allTags", allTags);

            return "search/item-search";

        } catch (BusinessException e) {
            // ビジネスロジックでエラーが発生した場合、エラーメッセージを表示
            model.addAttribute("errorMessage", e.getMessage());

            return "search/item-search";
        }
    }

    /**
     * アイテム検索結果表示
     * 
     * @param searchItemDto 検索条件
     * @param model         モデルオブジェクト
     * @return アイテム検索結果画面
     * @throws BusinessException ビジネスロジックでエラーが発生した場合
     */
    @GetMapping("/item-search-result")
    public String itemSearchResult(@ModelAttribute SearchItemDto searchItemDto, Model model) {

        try {
            // 選択したタグの個数を取得
            searchItemDto.setSelectedTagCount(searchItemDto.getSelectedTagIds().size());

            // DBを検索
            List<ItemDto> serachItemresults = fashionLogic.searchItems(searchItemDto);

            model.addAttribute("searchItemsResults", serachItemresults);

            return "search/item-search-result";

        } catch (BusinessException e) {
            model.addAttribute("searchItemResults", Collections.emptyList());
            model.addAttribute("errorMessage", e.getMessage());

            return "search/item-search-result";
        }
    }

    /**
     * アイテムアップロード画面表示
     * 
     * @param model モデルオブジェクト
     * @return アイテムアップロード画面
     * @throws BusinessException ビジネスロジックでエラーが発生した場合
     */
    @GetMapping("/item-upload")
    public String itemUpload(Model model) {

        try {
            ItemDto itemDto = new ItemDto();
            itemDto.setDate(LocalDate.now());

            List<TagsDto> allTags = tagsLogic.findAllTags();

            model.addAttribute("itemDto", itemDto);
            model.addAttribute("allTags", allTags);

            return "upload/item-upload";

        } catch (BusinessException e) {
            // ビジネスロジックでエラーが発生した場合、エラーメッセージを表示
            model.addAttribute("errorMessage", e.getMessage());

            return "upload/item-upload";
        }
    }

    /**
     * アイテムアップロード処理
     * 
     * @param itemDto            アイテム情報
     * @param bindingResult      バリデーション結果
     * @param model              モデルオブジェクト
     * @param redirectAttributes リダイレクト属性
     * @return アイテム一覧画面へリダイレクト
     * @throws BusinessException ビジネスロジックでエラーが発生した場合
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
            itemDto.setS3Url(s3Url);

            // アイテムを登録
            fashionLogic.insertItem(itemDto);

        } catch (BusinessException e) {
            // ビジネスロジックでエラーが発生した場合、エラーメッセージを表示
            model.addAttribute("errorMessage", e.getMessage());
            // model.addAttribute("allTags", tagsLogic.findAllTags());
            // model.addAttribute("itemDto", itemDto);

            return "upload/item-upload";
        }

        redirectAttributes.addFlashAttribute("flashMessage", "登録が完了しました");
        return "redirect:/all-items";
    }

    /**
     * アイテム削除処理
     * 
     * @param itemId             アイテムID
     * @param model              モデルオブジェクト
     * @param redirectAttributes リダイレクト属性
     * @return アイテム一覧画面へリダイレクト
     * @throws BusinessException ビジネスロジックでエラーが発生した場合
     */
    @GetMapping("/delete-item/{itemId}")
    public String deleteItem(@PathVariable("itemId") Integer itemId, Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // S3からファイルを削除
            // まずDBから該当のアイテム情報を取得（ファイル名を得るため）
            ItemDto deleteItem = fashionLogic.findItemById(itemId);

            // S3からファイル削除
            if (deleteItem != null && deleteItem.getS3Url() != null) {

                String fileName = this.getFileNameFromUrl(deleteItem.getS3Url());
                s3UploadLogic.deleteItemFromS3(fileName);
            }

            // DBをからアイテムを削除
            fashionLogic.deleteItemById(itemId);

            redirectAttributes.addFlashAttribute("flashMessage", "削除が完了しました");
            return "redirect:/all-items";

        } catch (BusinessException e) {
            // ビジネスロジックでエラーが発生した場合、エラーメッセージを表示
            model.addAttribute("errorMessage", e.getMessage());

            return "items/item-detail";
        }
    }

    /**
     * URLからファイル名を取得
     * 
     * @param url ファイルのURL
     * @return ファイル名
     */
    private String getFileNameFromUrl(String url) {

        // URLがnullまたは空の場合
        if (!StringUtils.hasText(url)) {
            // エラーを投げる
            throw new BusinessException("指定したファイルが存在しません");
        }

        // 最後のスラッシュ以降の部分を取得
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
