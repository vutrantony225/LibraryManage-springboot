package com.example.LibraryManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.LibraryManagement.model.Book;
import com.example.LibraryManagement.model.BookDetails;
import com.example.LibraryManagement.service.BookDetailsService;
import com.example.LibraryManagement.service.BookService;
import com.example.LibraryManagement.service.CategoryService;
import com.example.LibraryManagement.util.FileUploadUtil;

import java.util.Date;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private BookDetailsService bookDetailsService;

    @GetMapping("")
    public String adminDashboard() {
        return "admin";
    }

    @GetMapping("/books")
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "admin-books";
    }

    @GetMapping("/add-book")
    public String showAddBookForm(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "add-book";
    }
    
    @PostMapping("/add-book")
    public String addBook(@RequestParam("title") String title,
                         @RequestParam("author") String author,
                         @RequestParam("categoryId") Long categoryId,
                         @RequestParam("description") String description,
                         @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
                         @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile,
                         @RequestParam(value = "publisher", required = false) String publisher,
                         @RequestParam(value = "pageCount", required = false) Integer pageCount,
                         @RequestParam(value = "language", required = false) String language,
                         @RequestParam(value = "isbn", required = false) String isbn,
                         @RequestParam(value = "summary", required = false) String summary,
                         RedirectAttributes redirectAttributes) {
        try {
            // Tạo book mới
            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setDescription(description);
            book.setCategory(categoryService.getCategoryById(categoryId));
            book.setViewCount(0);
            
            // Thêm ngày phát hành
            book.setReleaseDate(new Date()); // Hoặc lấy từ form nếu có trường nập ngày
            
            // Xử lý file ảnh
            if (coverImage != null && !coverImage.isEmpty()) {
                String fileName = StringUtils.cleanPath(coverImage.getOriginalFilename());
                book.setCoverImage(fileName);
                String uploadDir = "src/main/resources/static/images/";
                FileUploadUtil.saveFile(uploadDir, fileName, coverImage);
            }
            
            // Lưu book trước để có ID
            book = bookService.saveBook(book);
            
            // Tạo và lưu BookDetails
            BookDetails bookDetails = new BookDetails();
            bookDetails.setBook(book);
            bookDetails.setPublisher(publisher);
            bookDetails.setPageCount(pageCount);
            bookDetails.setLanguage(language);
            bookDetails.setIsbn(isbn);
            bookDetails.setSummary(summary);
            
            // Xử lý file PDF
            if (pdfFile != null && !pdfFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(pdfFile.getOriginalFilename());
                bookDetails.setFilepath(fileName);
                String uploadDir = "src/main/resources/static/pdfs/";
                FileUploadUtil.saveFile(uploadDir, fileName, pdfFile);
            }
            
            // Lưu BookDetails
            bookDetails = bookDetailsService.saveBookDetails(bookDetails);
            
            // Cập nhật lại book với BookDetails
            book.setBookDetails(bookDetails);
            bookService.saveBook(book);
            
            redirectAttributes.addFlashAttribute("success", "Book added successfully!");
            return "redirect:/admin/books";
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error adding book: " + e.getMessage());
            return "redirect:/admin/add-book";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("success", "Book deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting book: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }
}
