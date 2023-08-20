package nomad.backend.oauth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Home {
    @GetMapping("/token")
    public String home() {
        return "home 제발";
    }

    @GetMapping("/home")
    public String home2() {
        return "test";
    }

    @GetMapping("/test")
    public String test(HttpServletRequest request, @CookieValue(value = "refresh", required = false) String refresh) {
        System.out.println("i'm test acessToken = " + request.getHeader("Authorization"));
        return "good";
    }

    @GetMapping("/test2")
    public String test2() {
        System.out.println("test2 진입");
        return "so good";
    }
}
