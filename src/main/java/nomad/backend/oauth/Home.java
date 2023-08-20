package nomad.backend.oauth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Home {
    @GetMapping("/token")
    public String home() {
        return "home 제발";
    }
}
