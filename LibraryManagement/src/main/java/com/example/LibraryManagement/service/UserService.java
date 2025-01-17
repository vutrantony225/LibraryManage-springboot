package com.example.LibraryManagement.service;

import com.example.LibraryManagement.model.User;
import com.example.LibraryManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Xử lý đăng ký người dùng (mã hóa mật khẩu)
    public String registerUser(String username, String password, String email) {
        if (userRepository.existsByUsername(username)) {
            return "Username already exists!";
        }

        // Tạo đối tượng người dùng mới
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password)); // Mã hóa mật khẩu
        newUser.setEmail(email);
        newUser.setRole(User.Role.USER); // Gán vai trò bằng enum USER

        userRepository.save(newUser);
        return "User registered successfully!";
    }

    // Xử lý đăng nhập người dùng (kiểm tra mật khẩu đã mã hóa)
    public boolean authenticateUser(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(password, user.getPassword())) // So sánh mật khẩu đã mã hóa
                .orElse(false); // Trả về false nếu không tìm thấy người dùng
    }
}