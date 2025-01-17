package com.example.LibraryManagement.repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.LibraryManagement.model.Book;
import com.example.LibraryManagement.model.Category;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    List<Book> findByCategory_Name(String categoryName);
    
    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.bookDetails LEFT JOIN FETCH b.category WHERE b.id = :bookId")
    Book findByIdWithDetails(Long bookId);
    
    @Query("SELECT b FROM Book b WHERE b.releaseDate >= :recentDate ORDER BY b.releaseDate DESC")
    List<Book> findNewReleases(Date recentDate);
    
    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.bookDetails LEFT JOIN FETCH b.category")
    List<Book> findAllWithDetails();
    
    // Sửa lại phương thức tìm kiếm để đúng với tên trường trong Entity
    List<Book> findByTitleContainingIgnoreCase(String title);

	List<Book> searchBooksByTitle(String query);

	long countByCategory(Category category);

	List<Book> findTop5ByOrderByIdDesc();

	List<Book> findTop5ByOrderByDownloadCountDesc();

	List<Book> findTop6ByOrderByDownloadCountDesc();

	@Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
		   "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))")
	List<Book> searchByTitleOrAuthor(String query);

	// Tìm top 10 sách xem nhiều nhất
	List<Book> findTop10ByOrderByViewCountDesc();

	// Tìm sách xem nhiều trong khoảng thời gian
	@Query("SELECT b FROM Book b WHERE b.releaseDate >= :fromDate ORDER BY b.viewCount DESC LIMIT 10")
	List<Book> findMostViewedBooksInPeriod(LocalDateTime fromDate);

	// Tìm sách mới nhất

	List<Book> findTop5ByCategory(Category category);

	List<Book> findTop5ByCategoryIdOrderByViewCountDesc(Long categoryId);

	List<Book> findByReleaseDateAfterOrderByReleaseDateDesc(Date date);

	List<Book> findTop10ByOrderByReleaseDateDesc();

}
