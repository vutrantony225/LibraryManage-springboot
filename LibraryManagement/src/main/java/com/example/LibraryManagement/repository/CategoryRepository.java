package com.example.LibraryManagement.repository;

import com.example.LibraryManagement.model.Category;
import com.example.LibraryManagement.service.CategoryService;
import com.example.LibraryManagement.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
} 