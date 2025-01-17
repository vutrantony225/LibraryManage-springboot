package com.example.LibraryManagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.LibraryManagement.model.Book;
import com.example.LibraryManagement.service.BookService;

@Controller
public class HomeController {

	@Autowired
	private BookService bookService;

	@GetMapping({"/", "/home"})
	public String showHomePage(Model model) {
		try {
			// Lấy thông tin xác thực hiện tại
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			
			// Lấy danh sách sách nổi bật
			List<Book> featuredBooks = bookService.getFeaturedBooks();
			model.addAttribute("featuredBooks", featuredBooks);
			
			// Thêm danh sách sách xem nhiều
			model.addAttribute("dailyTopBooks", bookService.getMostViewedBooksDaily());
			model.addAttribute("weeklyTopBooks", bookService.getMostViewedBooksWeekly());
			model.addAttribute("monthlyTopBooks", bookService.getMostViewedBooksMonthly());
			
			// Thêm danh sách sách mới nhất
			model.addAttribute("newestBooks", bookService.getNewestBooks());
			
			// Kiểm tra và thêm thông tin người dùng nếu đã đăng nhập
			if (authentication != null && authentication.isAuthenticated()) {
				Object principal = authentication.getPrincipal();
				if (principal instanceof UserDetails) {
					String username = ((UserDetails) principal).getUsername();
					model.addAttribute("username", username);
				}
			}
			
			return "home";
			
		} catch (Exception e) {
			e.printStackTrace();
			return "home";
		}
	}

	@GetMapping("/userHome")
	public String showUserHomePage(Model model) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && authentication.isAuthenticated()) {
				Object principal = authentication.getPrincipal();
				if (principal instanceof UserDetails) {
					String username = ((UserDetails) principal).getUsername();
					model.addAttribute("username", username);
				}
			}
			return "userHome";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/login";
		}
	}
}