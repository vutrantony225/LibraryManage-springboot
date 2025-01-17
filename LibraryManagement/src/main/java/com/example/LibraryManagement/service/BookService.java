package com.example.LibraryManagement.service;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.LibraryManagement.model.Book;
import com.example.LibraryManagement.model.BookDetails;
import com.example.LibraryManagement.repository.BookDetailsRepository;
import com.example.LibraryManagement.repository.BookRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookDetailsRepository bookDetailsRepository;

    // Lấy tất cả sách
    public List<Book> getAllBooks() {
        return bookRepository.findAllWithDetails();
    }

    // Lấy sách theo thể loại
    public List<Book> getBooksByCategory(String categoryName) {
        return bookRepository.findByCategory_Name(categoryName);
    }

    // Lấy sách theo ID kèm theo thông tin nhà xuất bản
    public Book getBookById(Long id) {
        System.out.println("Fetching book with ID: " + id);
        return bookRepository.findByIdWithDetails(id);
    }

    // Lấy sách mới phát hành trong 60 ngày gần đây
    public List<Book> getNewReleases() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -60); // Lấy sách phát hành trong 60 ngày gần đây
        Date recentDate = calendar.getTime();
        return bookRepository.findByReleaseDateAfterOrderByReleaseDateDesc(recentDate);
    }

    public void updateBook(Long id, Book updatedBook) {
        Book existingBook = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        // Cập nhật thông tin cơ bản của sách
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setDescription(updatedBook.getDescription());
        existingBook.setCoverImage(updatedBook.getCoverImage());
        existingBook.setReleaseDate(updatedBook.getReleaseDate());
        
        if (updatedBook.getCategory() != null) {
            existingBook.setCategory(updatedBook.getCategory());
        }

        // Cập nhật book details nếu có
        if (updatedBook.getBookDetails() != null) {
            BookDetails existingDetails = existingBook.getBookDetails();
            if (existingDetails == null) {
                existingDetails = new BookDetails();
                existingDetails.setBook(existingBook);
                existingBook.setBookDetails(existingDetails);
            }
            
            // Chỉ cập nhật các trường nếu giá trị mới không null
            if (updatedBook.getBookDetails().getPublisher() != null) {
                existingDetails.setPublisher(updatedBook.getBookDetails().getPublisher());
            }
            if (updatedBook.getBookDetails().getLanguage() != null) {
                existingDetails.setLanguage(updatedBook.getBookDetails().getLanguage());
            }
            if (updatedBook.getBookDetails().getIsbn() != null) {
                existingDetails.setIsbn(updatedBook.getBookDetails().getIsbn());
            }
            if (updatedBook.getBookDetails().getSummary() != null) {
                existingDetails.setSummary(updatedBook.getBookDetails().getSummary());
            }
            
            // Giữ lại filepath cũ nếu không có filepath mới
            if (updatedBook.getBookDetails().getFilepath() != null) {
                existingDetails.setFilepath(updatedBook.getBookDetails().getFilepath());
            } else if (existingDetails.getFilepath() == null) {
                existingDetails.setFilepath(""); // Đặt giá trị mặc định nếu không có
            }
            
            existingDetails.setPageCount(updatedBook.getBookDetails().getPageCount());
            
            // Lưu book details
            bookDetailsRepository.save(existingDetails);
        }

        // Lưu book
        bookRepository.save(existingBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        
        // Xóa book details trước nếu có
        if (book.getBookDetails() != null) {
            bookDetailsRepository.delete(book.getBookDetails());
        }
        
        // Xóa book
        bookRepository.delete(book);
    }
    
    // Tìm kiếm sách theo tiêu đề
    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return bookRepository.searchByTitleOrAuthor(query.trim());
    }

    @Transactional
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getFeaturedBooks() {
        return bookRepository.findTop6ByOrderByDownloadCountDesc();
    }

    // Lấy sách xem nhiều nhất theo ngày
    public List<Book> getMostViewedBooksDaily() {
        return bookRepository.findTop10ByOrderByViewCountDesc();
    }

    // Lấy sách xem nhiều nhất theo tuần
    public List<Book> getMostViewedBooksWeekly() {
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        return bookRepository.findMostViewedBooksInPeriod(weekAgo);
    }

    // Lấy sách xem nhiều nhất theo tháng
    public List<Book> getMostViewedBooksMonthly() {
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
        return bookRepository.findMostViewedBooksInPeriod(monthAgo);
    }

    // Lấy sách mới nhất
    public List<Book> getNewestBooks() {
        return bookRepository.findTop10ByOrderByReleaseDateDesc();
    }

    // Tăng lượt xem khi xem chi tiết sách
    public void incrementViewCount(Book book) {
        Integer currentViewCount = book.getViewCount();
        if (currentViewCount == null) {
            book.setViewCount(1); // Nếu là lần đầu xem, set là 1
        } else {
            book.setViewCount(currentViewCount + 1);
        }
        bookRepository.save(book);
    }

    public List<Book> getRelatedBooks(Book book) {
        // Implement logic to get related books, for example:
        return bookRepository.findTop5ByCategory(book.getCategory());
    }

    public List<Book> findRelatedBooks(Long categoryId) {
        return bookRepository.findTop5ByCategoryIdOrderByViewCountDesc(categoryId);
    }
}
