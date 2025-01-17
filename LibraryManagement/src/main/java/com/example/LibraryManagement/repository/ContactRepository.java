package com.example.LibraryManagement.repository;

import com.example.LibraryManagement.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
}
