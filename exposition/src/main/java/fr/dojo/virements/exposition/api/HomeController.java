package fr.dojo.virements.exposition.api;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Hidden
@Controller
class HomeController {
    @GetMapping("/")
    String home() {
        return "redirect:/swagger-ui/index.html";
    }
}
