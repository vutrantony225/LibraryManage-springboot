package com.example.LibraryManagement.controller;

import com.example.LibraryManagement.model.Contact;
import com.example.LibraryManagement.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/contact")
    public String showContactForm(Model model) {
        model.addAttribute("contact", new Contact());
        return "contact";
    }

    @PostMapping("/contact/report")
    public String submitContactForm(@ModelAttribute Contact contact, Model model) {
        contactRepository.save(contact); // Lưu dữ liệu vào database
        model.addAttribute("contact", contact);
        return "redirect:/contact/success"; // Chuyển hướng đến trang thành công
    }

    @GetMapping("/contact/success")
    public String showSuccessPage(Model model) {
        model.addAttribute("title", "Contact Submission Successful");
        return "success"; // Trả về tệp success.html
    }
}
