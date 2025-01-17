package com.example.LibraryManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.LibraryManagement.model.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    // Các phương thức tìm kiếm tài liệu có thể thêm vào đây
}
