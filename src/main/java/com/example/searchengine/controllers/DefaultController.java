package com.example.searchengine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    /**
     * Главная страница приложения.
     */
    @GetMapping("/")
    public String index(Model model) {
        return "index"; // Отображает шаблон "index.html"
    }
}