package com.example.LibraryManagement.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.LibraryManagement.model.Book;
import com.example.LibraryManagement.service.BookDetailsService;
import com.example.LibraryManagement.service.BookService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class BookDetailsController {

    @Autowired
    private BookDetailsService bookDetailsService;
    
    @Autowired
    private BookService bookService;

    @GetMapping("/book-details/{id}")
    public String getBookDetails(@PathVariable Long id, Model model) {
        try {
            Book book = bookService.getBookById(id);
            if (book == null) {
                return "redirect:/?error=Book+not+found";
            }

            // Tăng lượt xem
            bookService.incrementViewCount(book);
            
            // Thêm thông tin sách vào model
            model.addAttribute("book", book);
            
            // Lấy sách liên quan (cùng category)
            if (book.getCategory() != null) {
                List<Book> relatedBooks = bookService.findRelatedBooks(book.getCategory().getId());
                model.addAttribute("relatedBooks", relatedBooks);
            }
            
            return "book-details";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/?error=Error+loading+book+details";
        }
    }

    @GetMapping("/books/{id}/download")
    public void downloadPdf(@PathVariable Long id, HttpServletResponse response) throws IOException {
        try {
            Book book = bookService.getBookById(id);
            if (book != null && book.getBookDetails() != null && 
                book.getBookDetails().getFilepath() != null && 
                !book.getBookDetails().getFilepath().isEmpty()) {
                
                // Tăng số lượt tải
                book.setDownloadCount(book.getDownloadCount() + 1);
                bookService.saveBook(book);
                
                // Xử lý download
                String filepath = book.getBookDetails().getFilepath();
                File file = new File("uploads/pdfs/" + filepath);
                
                if (file.exists()) {
                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition", 
                        "attachment; filename=\"" + book.getTitle() + ".pdf\"");
                    
                    Files.copy(file.toPath(), response.getOutputStream());
                    response.getOutputStream().flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @GetMapping("/books/{id}/view")
    public void viewPdf(@PathVariable Long id, HttpServletResponse response) throws IOException {
        try {
            Book book = bookService.getBookById(id);
            if (book != null && book.getBookDetails() != null && 
                book.getBookDetails().getFilepath() != null && 
                !book.getBookDetails().getFilepath().isEmpty()) {
                
                String filepath = book.getBookDetails().getFilepath();
                File file = new File("uploads/pdfs/" + filepath);
                
                if (file.exists()) {
                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition", 
                        "inline; filename=\"" + book.getTitle() + ".pdf\"");
                    
                    Files.copy(file.toPath(), response.getOutputStream());
                    response.getOutputStream().flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
