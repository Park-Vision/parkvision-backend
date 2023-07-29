package net.parkvision.parkvisionbackend.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailSenderService {
    
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String body, String topic) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("${spring.mail.username}");
        message.setTo(to);
        message.setText(body);
        message.setSubject(topic);

        javaMailSender.send(message);
        System.out.println("Email sent to " + to);
    }

    public void sendMessageWithAttachment(
    String to, String subject, String text, String pathToAttachment) throws Exception {

        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("${spring.mail.username}");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        FileSystemResource file 
        = new FileSystemResource(new File(pathToAttachment));
        helper.addAttachment("parking.png", file);

        javaMailSender.send(message);
        System.out.println("Email sent to " + to);
    }
}
