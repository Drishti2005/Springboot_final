package trivedi.gmail.com.BulkOrderManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Redirects the root URL to Swagger UI so visiting
 * http://localhost:8080 takes you straight to the API docs.
 */
@Controller
public class WelcomeController {

    @GetMapping("/")
    public String root() {
        return "redirect:/swagger-ui.html";
    }
}
