package com.example.LibraryManagement.controller;

import com.example.LibraryManagement.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatisticsController {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/admin/statistics")
    public String showStatistics(Model model) {
        logger.info("Accessing statistics page");
        try {
            model.addAttribute("totalBooks", statisticsService.getTotalBooks());
            model.addAttribute("totalUsers", statisticsService.getTotalUsers());
            model.addAttribute("booksByCategory", statisticsService.getBooksByCategory());
            model.addAttribute("recentlyAddedBooks", statisticsService.getRecentlyAddedBooks());
            model.addAttribute("mostDownloadedBooks", statisticsService.getMostDownloadedBooks());
            model.addAttribute("monthlyStats", statisticsService.getMonthlyStatistics());
            logger.info("Statistics data loaded successfully");
            return "admin/statistics";
        } catch (Exception e) {
            logger.error("Error loading statistics: ", e);
            return "error";
        }
    }
} 