package com.rcgraul.cripto_planet.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine thymeleafEngine;

    public void sendPasswordResetEmail(String to, String resetUrl) throws MessagingException {
        Context context = new Context();
        context.setVariable("resetUrl", resetUrl);
        String emailContent = thymeleafEngine.process("password-reset-email", context);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject("Password reset request");
        helper.setText(emailContent, true);
        mailSender.send(message);
    }
}