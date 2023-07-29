package net.parkvision.parkvisionbackend;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import net.parkvision.parkvisionbackend.service.EmailSenderService;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class ParkVisionBackendApplication {

    @Autowired
    private EmailSenderService emailSenderService;

    public static void main(String[] args) {
        SpringApplication.run(ParkVisionBackendApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void triggerWhenStarts() throws Exception {
        System.out.println("Application started ... sending email");

        // emailSenderService.sendEmail("@gmail.com", "TEST body <ul><li>jeden</li></ul>", "Test topic 2");


        List<String> emails = new ArrayList<>(List.of("maciejmakarakontakt@gmail.com"));

        for (String email : emails) {
            emailSenderService.sendMessageWithAttachment(email, "Powiadomienie z systemu ParkVision", "Twój pojazd " +
                    "zostaje wyeskortowany z parkingu przez grupę wojskowych dronów.",
                    "C:\\Users\\makar\\Desktop\\unnamed.png");
        }
    }


}
