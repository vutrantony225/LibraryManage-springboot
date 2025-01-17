package com.example.LibraryManagement.controller;

import com.example.LibraryManagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.LibraryManagement.model.User;  // Sửa lại import đúng
import com.example.LibraryManagement.repository.UserRepository;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository; // Inject UserRepository vào controller

    // Trang đăng ký
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register"; // Trả về view cho trang đăng ký
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password,
                           @RequestParam String email, Model model) {
        String result = userService.registerUser(username, password, email);
        if ("User registered successfully!".equals(result)) {
            return "redirect:/login"; // Chuyển đến trang đăng nhập sau khi đăng ký thành công
        } else {
            model.addAttribute("error", result);
            return "register"; // Trả về trang đăng ký với thông báo lỗi
        }
    }

    // Trang đăng nhập
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Trả về view cho trang đăng nhập
    }

    // Xử lý đăng nhập
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        boolean isAuthenticated = userService.authenticateUser(username, password);
        if (isAuthenticated) {
            // Tìm lại người dùng để kiểm tra vai trò
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                // Kiểm tra vai trò của người dùng
                if (user.getRole() == User.Role.ADMIN) {
                    return "redirect:/admin"; // Điều hướng đến trang admin nếu là admin
                } else {
                    return "redirect:/home"; // Điều hướng đến trang chủ nếu là người dùng thường
                }
            }
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login"; // Quay lại trang đăng nhập nếu thất bại
        }
        return "login"; // Quay lại trang đăng nhập nếu thất bại
    }
}
