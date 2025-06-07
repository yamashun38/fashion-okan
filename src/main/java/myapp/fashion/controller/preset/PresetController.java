package myapp.fashion.controller.preset;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PresetController {

    /**
     * プリセットトップ画面表示
     * 
     * @return プリセットトップ画面
     */
    @GetMapping("/preset-top")
    public String showPresetTopPage() {

        // プリセットトップ画面を表示
        return "preset/preset-top";
    }

    /**
     * プリセット登録画面表示(Step1)
     * プリセット名や説明文を入力する画面
     * 
     * @return プリセット登録画面(Step1)
     */
    @GetMapping("/preset-entry-step1")
    public String showPresetEntryStep1Page() {

        // プリセット登録画面(Step1)を表示
        return "preset/preset-entry-step1";
    }

    /**
     * プリセット登録画面表示(Step2)
     * プリセットに含めるアイテムを選択する画面
     * 
     * @return プリセット登録画面(Step2)
     */
    @GetMapping("/preset-entry-step2")
    public String showPresetEntryStep2Page() {

        // プリセット登録画面(Step2)を表示
        return "preset/preset-entry-step2";
    }
}
