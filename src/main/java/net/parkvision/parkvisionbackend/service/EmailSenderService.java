package net.parkvision.parkvisionbackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import net.parkvision.parkvisionbackend.model.Parking;
import net.parkvision.parkvisionbackend.model.Reservation;
import net.parkvision.parkvisionbackend.model.StripeCharge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    public static final String EMAIL_TEMPLATE = "emailtemplate";

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private final TemplateEngine templateEngine;

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
        context.setVariable("description", description +
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
        context.setVariable("description", description);
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

    @Async("emailTaskExecutor")
    public void sendHtmlEmailPasswordReset(
            String firstName,
            String lastName,
            String to,
            String topic,
            String description,
            String link) throws Exception {
        Context context = new Context();
        context.setVariable("title", "Password reset link");
        context.setVariable("description", description);
        context.setVariable("body", "<b><a href=" + link + ">Reset password</a></b>");
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


        String formattedStartDateTime =
                reservation.getStartDate().withOffsetSameInstant(parking.getTimeZone()).format(formatter);
        String formattedEndDateTime =
                reservation.getEndDate().withOffsetSameInstant(parking.getTimeZone()).format(formatter);
        String formattedAmount = String.format("%.2f", reservation.getAmount());

        return "<table style=\"width: 100%\">" +

                // Create rows for Reservation fields
                "<tr>" +
                "<th>Reservation number</th>" +
                "<td>" + reservation.getId() + "</td>" +
                "</tr>" +
                "<tr>" +
                "<th>Start date</th>" +
                "<td>" + formattedStartDateTime + "</td>" +
                "</tr>" +
                "<tr>" +
                "<th>End date</th>" +
                "<td>" + formattedEndDateTime + "</td>" +
                "</tr>" +
                "<tr>" +
                "<th>Registration number</th>" +
                "<td>" + reservation.getRegistrationNumber() + "</td>" +
                "</tr>" +
                "<tr>" +
                "<th>Parking</th>" +
                "<td>" + parking.getName() + ", " + parking.getStreet() +
                ", " + parking.getCity() + "</td>" +
                "</tr>" +
                "<tr>" +
                "<th>Parking spot number</th>" +
                "<td>" + reservation.getParkingSpot().getId() + "</td>" +
                "</tr>" +
                "<tr>" +
                "<th>Amount</th>" +
                "<td>" + formattedAmount +
                " " +
                parking.getCurrency() + "</td>" +
                "</tr>" +
                "</table>";
    }

    public String generateHTMLTable(Reservation reservation, StripeCharge charge) {

        String formattedAmount = String.format("%.2f", reservation.getAmount());

        return "<table style=\"width: 100%\">" +
                "<tr>" +
                "<th>Reservation number</th>" +
                "<td>" + reservation.getId() + "</td>" +
                "</tr>" +
                "<tr>" +
                "<th>Amount</th>" +
                "<td>" + formattedAmount +
                " " +
                reservation.getParkingSpot().getParking().getCurrency() + "</td>" +
                "</tr>" +
                "<tr>" +
                "<th>Payment ID</th>" +
                "<td>" + charge.getId() +
                "</tr>" +
                "</table>";
    }
}
