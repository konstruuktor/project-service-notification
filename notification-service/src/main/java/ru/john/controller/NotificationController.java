package ru.john.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.john.service.EmailService;

@RestController
@RequestMapping("/api/notify")
@RequiredArgsConstructor
public class NotificationController {
    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<Void> sendMail(@RequestParam String email,
                                         @RequestParam String subject,
                                         @RequestParam String message) {
        emailService.send(email, subject, message);
        return ResponseEntity.ok().build();
    }
}