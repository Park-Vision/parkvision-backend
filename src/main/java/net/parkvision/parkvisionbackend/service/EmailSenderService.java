package net.parkvision.parkvisionbackend.service;

import java.io.File;

import lombok.RequiredArgsConstructor;
import net.parkvision.parkvisionbackend.model.Parking;
import net.parkvision.parkvisionbackend.model.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    public static final String EMAIL_TEMPLATE = "emailtemplate";

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private final TemplateEngine templateEngine;


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

    public void sendHtmlEmail(
            String firstName,
            String lastName,
            String to,
            Parking parking,
            Reservation reservation,
            String topic) throws Exception {
        Context context = new Context();
        context.setVariable("title", "Potwierdzenie rezerwacji");
        context.setVariable("description", "Oto potwierdzenie rezerwacji jaką dokonałeś w naszym systemie:");
        context.setVariable("name", firstName + " " + lastName);
        String htmlTable = generateHTMLTable(reservation, parking);
        context.setVariable("body", htmlTable);

        String text = templateEngine.process(EMAIL_TEMPLATE, context);
        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("${spring.mail.username}");
        helper.setTo(to);
        helper.setSubject(topic);
        helper.setText(text, true);

        javaMailSender.send(message);
        System.out.println("Email sent to " + to);
    }

    public String generateHTMLTable(Reservation reservation, Parking parking) {
        StringBuilder htmlTable = new StringBuilder();
        htmlTable.append("<table style=\"width: 100%\">");

        // Create rows for Reservation fields
        htmlTable.append("<tr>");
        htmlTable.append("<th>Numer rezerwacji</th>");
        htmlTable.append("<td>").append(reservation.getId()).append("</td>");
        htmlTable.append("</tr>");

        htmlTable.append("<tr>");
        htmlTable.append("<th>Data rozpoczęcia</th>");
        htmlTable.append("<td>").append(reservation.getStartDate()).append("</td>");
        htmlTable.append("</tr>");

        htmlTable.append("<tr>");
        htmlTable.append("<th>Data zakończenia</th>");
        htmlTable.append("<td>").append(reservation.getEndDate()).append("</td>");
        htmlTable.append("</tr>");

        htmlTable.append("<tr>");
        htmlTable.append("<th>Numer rejestracyjny</th>");
        htmlTable.append("<td>").append(reservation.getRegistrationNumber()).append("</td>");
        htmlTable.append("</tr>");

        htmlTable.append("<tr>");
        htmlTable.append("<th>Parking</th>");
        htmlTable.append("<td>").append(parking.getName()).append(", ").append(parking.getStreet()).append(", ").append(parking.getCity()).append("</td>");
        htmlTable.append("</tr>");

        htmlTable.append("<tr>");
        htmlTable.append("<th>Numer miejsca postojowego</th>");
        htmlTable.append("<td>").append(reservation.getParkingSpot().getId()).append("</td>");
        htmlTable.append("</tr>");


        htmlTable.append("</table>");

        return htmlTable.toString();
    }

}
