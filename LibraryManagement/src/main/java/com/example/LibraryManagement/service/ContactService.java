package com.example.LibraryManagement.service;

import com.example.LibraryManagement.model.Contact;
import com.example.LibraryManagement.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public Contact saveContact(Contact contact) {
        contact.setTimestamp(LocalDateTime.now());
        return contactRepository.save(contact);
    }

    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }
}
