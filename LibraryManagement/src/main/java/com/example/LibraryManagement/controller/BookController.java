package com.example.LibraryManagement.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.LibraryManagement.model.Book;
import com.example.LibraryManagement.model.BookDetails;
import com.example.LibraryManagement.service.BookService;
import com.example.LibraryManagement.service.CategoryService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class BookController {
    @Value("${upload.path.images}")
    private String imageUploadPath;
    
    @Value("${upload.path.pdfs}")
    private String pdfUploadPath;
    
    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/genre/{categoryName}")
    public String getBooksByCategory(@PathVariable String categoryName, Model model) {
        model.addAttribute("books", bookService.getBooksByCategory(categoryName));
        model.addAttribute("categoryName", categoryName);
        return "genre_books";
    }
    @GetMapping("/new-releases")
    public String getNewReleases(Model model) {
        List<Book> newReleases = bookService.getNewReleases();
        model.addAttribute("books", newReleases);
        return "new_releases"; // Trả về view new_releases.html
    }
    @GetMapping("/admin/admin-books")
    public String manageBooks(Model model) {
        List<Book> books = bookService.getAllBooks();  // Lấy tất cả sách
        model.addAttribute("books", books);  // Truyền danh sách sách vào view
        return "admin-books";  // Trả về view admin_books.html
    }
    @GetMapping("/books/{id}")
    public String showBookDetails(@PathVariable Long id, Model model) {
        try {
            Book book = bookService.getBookById(id);
            if (book == null) {
                return "error/404";
            }
            
            // Đảm bảo BookDetails không null
            if (book.getBookDetails() == null) {
                BookDetails bookDetails = new BookDetails();
                bookDetails.setBook(book);
                book.setBookDetails(bookDetails);
            }
            
            // Tăng số lượt xem
            Integer currentViewCount = book.getViewCount();
            book.setViewCount(currentViewCount == null ? 1 : currentViewCount + 1);
            bookService.saveBook(book);
            
            model.addAttribute("book", book);
            
            // Lấy sách liên quan (cùng category)
            if (book.getCategory() != null) {
                List<Book> relatedBooks = bookService.findRelatedBooks(book.getCategory().getId());
                model.addAttribute("relatedBooks", relatedBooks);
            }
            
            return "book-details";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "error/500";
        }
    }
    @GetMapping("/admin/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "edit-book";
    }
    @PostMapping("/admin/edit/{id}")
    public String updateBook(@PathVariable Long id, @ModelAttribute Book updatedBook, 
                            RedirectAttributes redirectAttributes) {
        try {
            bookService.updateBook(id, updatedBook);
            redirectAttributes.addFlashAttribute("success", "Book updated successfully!");
            return "redirect:/admin/admin-books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update book: " + e.getMessage());
            return "redirect:/admin/edit/" + id;
        }
    }
    
    // API tìm kiếm sách theo tiêu đề
    @GetMapping("/")
    public String searchBooks(@RequestParam(value = "query", required = false) String query, Model model) {
        if (query != null && !query.isEmpty()) {
            List<Book> searchResults = bookService.searchBooks(query);
            model.addAttribute("searchResults", searchResults);
        } else {
            model.addAttribute("searchResults", Collections.emptyList());
        }
        return "home";
    }
    public String home(Model model) {
        // Lấy sách xem nhiều theo các khoảng thời gian
        model.addAttribute("dailyTopBooks", bookService.getMostViewedBooksDaily());
        model.addAttribute("weeklyTopBooks", bookService.getMostViewedBooksWeekly());
        model.addAttribute("monthlyTopBooks", bookService.getMostViewedBooksMonthly());
        
        // Lấy sách nổi bật (featured books)
        model.addAttribute("featuredBooks", bookService.getFeaturedBooks());
        
        // Lấy sách mới nhất
        model.addAttribute("newestBooks", bookService.getNewestBooks());
        
        return "home";
    }

    @GetMapping("/book-details/{bookId}/download")
    public void downloadPdf(@PathVariable Long bookId, HttpServletResponse response) throws IOException {
        try {
            Book book = bookService.getBookById(bookId);
            // Tăng số lượt tải
            book.setDownloadCount(book.getDownloadCount() + 1);
            bookService.saveBook(book);
            
            // Lấy thông tin book và book details
            if (book == null || book.getBookDetails() == null || book.getBookDetails().getFilepath() == null) {
                throw new RuntimeException("PDF file not found");
            }

            // Tạo đường dẫn file
            File file = new File(pdfUploadPath + book.getBookDetails().getFilepath());
            if (!file.exists()) {
                throw new RuntimeException("File not found: " + book.getBookDetails().getFilepath());
            }

            // Set up response với encoding UTF-8
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/pdf");
            // Encode tên file để tránh lỗi với ký tự Unicode
            String fileName = URLEncoder.encode(book.getTitle(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName + ".pdf");
            response.setContentLength((int) file.length());

            // Copy file to response output stream
            try (FileInputStream inputStream = new FileInputStream(file);
                 OutputStream outputStream = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
        }
    }

    @GetMapping("/book-details/{bookId}/view")
    public void viewPdf(@PathVariable Long bookId, HttpServletResponse response) throws IOException {
        try {
            // Lấy thông tin book và book details
            Book book = bookService.getBookById(bookId);
            if (book == null || book.getBookDetails() == null || book.getBookDetails().getFilepath() == null) {
                throw new RuntimeException("PDF file not found");
            }

            // Tạo đường dẫn file
            File file = new File(pdfUploadPath + book.getBookDetails().getFilepath());
            if (!file.exists()) {
                throw new RuntimeException("File not found: " + book.getBookDetails().getFilepath());
            }

            // Set up response để hiển thị PDF trong trình duyệt
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=\"" + book.getTitle() + ".pdf\"");
            response.setContentLength((int) file.length());

            // Copy file to response output stream
            try (FileInputStream inputStream = new FileInputStream(file);
                 OutputStream outputStream = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
        }
    }
}


