package myapp.fashion.controller.setting;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingController {

    /**
     * 設定画面表示
     */
    @GetMapping("/setting")
    public String setting() {

        return "setting/setting";
    }
}
