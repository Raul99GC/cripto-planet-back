package com.rcgraul.cripto_planet.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Configuration freemarkerConfig;

    public void sendPasswordResetEmail(String to, String resetUrl) throws MessagingException, IOException, TemplateException {
        // Crear modelo de datos para la plantilla
        Map<String, Object> model = new HashMap<>();
        model.put("resetUrl", resetUrl);

        // Procesar la plantilla Freemarker
        Template template = freemarkerConfig.getTemplate("password-reset-email.ftl");
        StringWriter writer = new StringWriter();
        template.process(model, writer);
        String emailContent = writer.toString();

        // Crear el mensaje MIME
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject("Password Reset Request");
        helper.setText(emailContent, true);

        // Adjuntar imagen como recurso embebido
        ClassPathResource imageResource = new ClassPathResource("static/img/logo.png");
        helper.addInline("logo", imageResource);

        // Enviar el correo
        mailSender.send(message);
    }
}
