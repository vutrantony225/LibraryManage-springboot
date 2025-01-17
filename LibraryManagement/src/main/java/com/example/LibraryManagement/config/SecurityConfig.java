package com.example.LibraryManagement.config;

import com.example.LibraryManagement.model.User;
import com.example.LibraryManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    // Cấu hình UserDetailsService để lấy thông tin người dùng từ cơ sở dữ liệu
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            // Tìm người dùng từ cơ sở dữ liệu và đảm bảo không có lỗi nếu không tìm thấy người dùng
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Trả về đối tượng UserDetails cho Spring Security
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())  // Mật khẩu đã mã hóa trong cơ sở dữ liệu
                    .roles(user.getRole().name())  // Chỉ sử dụng tên vai trò mà không thêm "ROLE_"
                    .build();
        };
    }

    // Cấu hình PasswordEncoder để mã hóa và xác thực mật khẩu
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Mã hóa mật khẩu sử dụng BCryptPasswordEncoder
    }

    // Cấu hình các quy tắc bảo mật cho ứng dụng
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .requestMatchers("/register", "/login", "/about").permitAll()  // Cho phép truy cập các trang này mà không cần đăng nhập
                .requestMatchers("/admin/**").hasRole("ADMIN") // Chỉ ADMIN mới truy cập được các URL bắt đầu bằng /admin
                .requestMatchers("/home", "/categories", "/contact").authenticated()  // Các trang này yêu cầu người dùng đã đăng nhập
                .anyRequest().authenticated()  // Các trang khác yêu cầu đăng nhập
            .and()
            .formLogin()
                .loginPage("/login")  // Trang login tùy chỉnh
                .defaultSuccessUrl("/home", true)  // Điều hướng đến trang home sau khi đăng nhập thành công
                .successHandler((request, response, authentication) -> {
                    // Kiểm tra vai trò của người dùng và điều hướng tương ứng
                    String role = authentication.getAuthorities().toString();
                    if (role.contains("ADMIN")) {
                        response.sendRedirect("/admin");  // Điều hướng đến trang admin nếu là admin
                    } else {
                        response.sendRedirect("/home");  // Điều hướng đến trang home cho người dùng thông thường
                    }
                })
                .permitAll()
            .and()
            .logout()
                .logoutSuccessUrl("/login?logout")  // Quay lại trang login sau khi logout
                .permitAll()
            .and()
            .csrf().disable();  // Tắt CSRF nếu không cần thiết

        return http.build();
    }
}
