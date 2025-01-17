package com.example.LibraryManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.LibraryManagement.model.BookDetails;
import com.example.LibraryManagement.repository.BookDetailsRepository;

@Service
public class BookDetailsService {

    @Autowired
    private BookDetailsRepository bookDetailsRepository;

    public BookDetails getBookDetails(Long bookId) {
        return bookDetailsRepository.findByBookId(bookId);
    }
    
    @Transactional
    public BookDetails saveBookDetails(BookDetails bookDetails) {
        return bookDetailsRepository.save(bookDetails);
    }
}
