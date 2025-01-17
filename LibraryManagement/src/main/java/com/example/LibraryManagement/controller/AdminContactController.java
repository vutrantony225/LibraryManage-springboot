package com.example.LibraryManagement.controller;

import com.example.LibraryManagement.model.Contact;
import com.example.LibraryManagement.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping("/contacts")
    public String viewContacts(Model model) {
        model.addAttribute("contacts", contactService.getAllContacts());
        return "admin/contacts";
    }

    @PostMapping("/contacts/{id}/delete")
    public String deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return "redirect:/admin/contacts";
    }
} 