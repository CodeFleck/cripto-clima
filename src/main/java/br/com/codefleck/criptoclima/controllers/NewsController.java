package br.com.codefleck.criptoclima.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/news")
public class NewsController {

    @GetMapping
    String news(Model model) {
        model.addAttribute("breadcrumbs", "noticias");
        return "news";
    }
}
