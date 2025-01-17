package com.example.LibraryManagement.repository;

import com.example.LibraryManagement.model.BookDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookDetailsRepository extends JpaRepository<BookDetails, Long> {
    BookDetails findByBookId(Long bookId);
}
