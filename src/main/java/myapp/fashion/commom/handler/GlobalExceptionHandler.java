package myapp.fashion.commom.handler;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import myapp.fashion.commom.exception.BusinessException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException ex, Model model) {

        log.warn("BusinessException: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/default";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {

        String uri = request.getRequestURI();
        if (uri != null && uri.startsWith("/.well-known/")) {
            // 開発ツールのリクエストなどは無視してログを出力しない
            return null;
        }

        log.warn("NoResourceFound: {}", uri);
        return "error/default";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {

        log.error("Unexpected exception occurred", ex);
        model.addAttribute("errorMessage", "予期せぬエラーが発生しました");
        return "error/default";
    }
}
