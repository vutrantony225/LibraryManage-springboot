package com.example.LibraryManagement.service;

import com.example.LibraryManagement.model.Book;
import com.example.LibraryManagement.repository.BookRepository;
import com.example.LibraryManagement.repository.UserRepository;
import com.example.LibraryManagement.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatisticsService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public long getTotalBooks() {
        return bookRepository.count();
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public Map<String, Long> getBooksByCategory() {
        Map<String, Long> stats = new HashMap<>();
        categoryRepository.findAll().forEach(category -> {
            long count = bookRepository.countByCategory(category);
            stats.put(category.getName(), count);
        });
        return stats;
    }

    public List<Book> getRecentlyAddedBooks() {
        return bookRepository.findTop5ByOrderByIdDesc();
    }

    public List<Book> getMostDownloadedBooks() {
        return bookRepository.findTop5ByOrderByDownloadCountDesc();
    }

    public Map<String, Object> getMonthlyStatistics() {
        Map<String, Object> stats = new HashMap<>();
        // Thêm logic để lấy thống kê theo tháng
        return stats;
    }
} 