package com.example.LibraryManagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.LibraryManagement.model.Book;
import com.example.LibraryManagement.service.BookService;

@Controller
public class SearchController {

    @Autowired
    private BookService bookService;

    @GetMapping("/search")
    public String searchBooks(@RequestParam(required = false) String query, Model model) {
        if (query != null && !query.trim().isEmpty()) {
            List<Book> searchResults = bookService.searchBooks(query);
            model.addAttribute("searchResults", searchResults);
            model.addAttribute("query", query);
        }
        return "search";
    }
}