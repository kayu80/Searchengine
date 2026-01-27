package com.example.searchengine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    /**
     * Перенаправляет запрос на главную страницу приложения.
     */
    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }
}