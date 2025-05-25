package myapp.fashion.controller.fetch;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import myapp.fashion.commom.exception.BusinessException;
import myapp.fashion.dto.ItemDto;
import myapp.fashion.dto.SearchItemDto;
import myapp.fashion.dto.TagsDto;
import myapp.fashion.logic.fetch.ItemFetchLogic;
import myapp.fashion.logic.tags.TagsLogic;

@Controller
public class ItemFetchController {

    @Autowired
    private ItemFetchLogic itemFetchLogic;

    @Autowired
    private TagsLogic tagsLogic;

    /**
     * 全アイテム表示
     * 
     * @param model モデルオブジェクト
     * @return アイテム一覧画面
     * @throws BusinessException ビジネスロジックでエラーが発生した場合
     */
    @GetMapping("/all-items")
    public String showAllItemsPage(Model model) {

        try {
            List<ItemDto> itemList = itemFetchLogic.findAllItems();
            List<TagsDto> allTags = tagsLogic.findAllTags();

            model.addAttribute("items", itemList);
            model.addAttribute("allTags", allTags);

            return "items/all-items";

        } catch (BusinessException e) {
            // ビジネスロジックでエラーが発生した場合、エラーメッセージを表示
            model.addAttribute("errorMessage", e.getMessage());

            return "items/all-items";
        }
    }

    /**
     * アイテム詳細画面表示
     * 
     * @param itemId アイテムID
     * @param model  モデルオブジェクト
     * @return アイテム詳細画面
     * @throws BusinessException ビジネスロジックでエラーが発生した場合
     */
    @GetMapping("/item-detail/{id}")
    public String showItemDetailPage(@PathVariable("id") Integer itemId, Model model,
            RedirectAttributes redirectAttributes) {

        try {
            ItemDto itemDto = itemFetchLogic.findItemById(itemId);
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
     * アイテム検索画面表示
     * 
     * @param searchItemDto 検索条件
     * @param model         モデルオブジェクト
     * @return アイテム検索画面
     * @throws BusinessException ビジネスロジックでエラーが発生した場合
     */
    @GetMapping("/item-search")
    public String showItemSearchPage(@ModelAttribute SearchItemDto searchItemDto, Model model) {

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

            return "items/item-search";

        } catch (BusinessException e) {
            // ビジネスロジックでエラーが発生した場合、エラーメッセージを表示
            model.addAttribute("errorMessage", e.getMessage());

            return "items/item-search";
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
    public String showItemSearchResultPage(@ModelAttribute SearchItemDto searchItemDto, Model model) {

        try {
            // 選択したタグの個数を取得
            searchItemDto.setSelectedTagCount(searchItemDto.getSelectedTagIds().size());

            // DBを検索
            List<ItemDto> searchItemResults = itemFetchLogic.searchItems(searchItemDto);

            model.addAttribute("searchItemsResults", searchItemResults);

            return "items/item-search-result";

        } catch (BusinessException e) {
            // ビジネスロジックでエラーが発生した場合、エラーメッセージを表示
            model.addAttribute("errorMessage", e.getMessage());

            return "items/item-search-result";
        }
    }
}
