package net.parkvision.parkvisionbackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import net.parkvision.parkvisionbackend.model.Parking;
import net.parkvision.parkvisionbackend.model.Payment;
import net.parkvision.parkvisionbackend.model.Reservation;
import net.parkvision.parkvisionbackend.model.StripeCharge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.time.format.DateTimeFormatter;

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

    @Async("emailTaskExecutor")
    public void sendHtmlEmailReservation(
            String firstName,
            String lastName,
            String to,
            String title,
            String description,
            Parking parking,
            Reservation reservation,
            String topic) throws Exception {
        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("description",  description +
                "Dates and times are based on parking time zone " + parking.getTimeZone() + " compared to UTC.");
        context.setVariable("name", firstName + " " + lastName);
        String htmlTable = generateHTMLTable(reservation, parking);
        context.setVariable("body", htmlTable);

        sendContextToUser(to, topic, context);
    }

    @Async("emailTaskExecutor")
    public void sendHtmlEmailPayment(
            String firstName,
            String lastName,
            String to,
            String title,
            String description,

            StripeCharge stripeCharge,
            Reservation reservation,
            String topic) throws Exception {
        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("description",  description);
        context.setVariable("name", firstName + " " + lastName);
        String htmlTable = generateHTMLTable(reservation, stripeCharge);
        context.setVariable("body", htmlTable);

        sendContextToUser(to, topic, context);
    }

    @Async("emailTaskExecutor")
    public void sendHtmlEmailRegistrationCreated(
            String firstName,
            String lastName,
            String to,
            String topic) throws Exception {
        Context context = new Context();
        context.setVariable("title", "Registration confirmation");
        context.setVariable("description", "Here is the confirmation of the registration you made in our system:");
        context.setVariable("name", firstName + " " + lastName);

        sendContextToUser(to, topic, context);
    }

    private void sendContextToUser(String to, String topic, Context context) throws MessagingException {
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


        String formatedStartDateTime =
                reservation.getStartDate().withOffsetSameInstant(parking.getTimeZone()).format(formatter);
        String formatedEndDateTime =
                reservation.getEndDate().withOffsetSameInstant(parking.getTimeZone()).format(formatter);


        StringBuilder htmlTable = new StringBuilder();
        htmlTable.append("<table style=\"width: 100%\">");

        // Create rows for Reservation fields
        htmlTable.append("<tr>");
        htmlTable.append("<th>Reservation number</th>");
        htmlTable.append("<td>").append(reservation.getId()).append("</td>");
        htmlTable.append("</tr>");

        htmlTable.append("<tr>");
        htmlTable.append("<th>Start date</th>");
        htmlTable.append("<td>").append(formatedStartDateTime).append("</td>");
        htmlTable.append("</tr>");

        htmlTable.append("<tr>");
        htmlTable.append("<th>End date</th>");
        htmlTable.append("<td>").append(formatedEndDateTime).append("</td>");
        htmlTable.append("</tr>");

        htmlTable.append("<tr>");
        htmlTable.append("<th>Registration number</th>");
        htmlTable.append("<td>").append(reservation.getRegistrationNumber()).append("</td>");
        htmlTable.append("</tr>");

        htmlTable.append("<tr>");
        htmlTable.append("<th>Parking</th>");
        htmlTable.append("<td>").append(parking.getName()).append(", ").append(parking.getStreet())
                .append(", ").append(parking.getCity()).append("</td>");
        htmlTable.append("</tr>");

        htmlTable.append("<tr>");
        htmlTable.append("<th>Parking spot number</th>");
        htmlTable.append("<td>").append(reservation.getParkingSpot().getId()).append("</td>");
        htmlTable.append("</tr>");

        htmlTable.append("<tr>");
        htmlTable.append("<th>Amount</th>");
        htmlTable.append("<td>").append(reservation.getAmount())
                .append(" ")
                .append(parking.getCurrency()).append("</td>");
        htmlTable.append("</tr>");

        htmlTable.append("</table>");

        return htmlTable.toString();
    }

    public String generateHTMLTable(Reservation reservation, StripeCharge charge) {

        StringBuilder htmlTable = new StringBuilder();
        htmlTable.append("<table style=\"width: 100%\">");

        htmlTable.append("<tr>");
        htmlTable.append("<th>Reservation number</th>");
        htmlTable.append("<td>").append(reservation.getId()).append("</td>");
        htmlTable.append("</tr>");

        htmlTable.append("<tr>");
        htmlTable.append("<th>Amount</th>");
        htmlTable.append("<td>").append(reservation.getAmount())
                .append(" ")
                .append(charge.getCurrency()).append("</td>");
        htmlTable.append("</tr>");

        htmlTable.append("<tr>");
        htmlTable.append("<th>Payment ID</th>");
        htmlTable.append("<td>").append(charge.getId());
        htmlTable.append("</tr>");

        htmlTable.append("</table>");

        return htmlTable.toString();
    }

}
