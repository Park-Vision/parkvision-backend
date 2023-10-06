package net.parkvision.parkvisionbackend;

import jakarta.annotation.PostConstruct;
import net.parkvision.parkvisionbackend.service.DataSeeder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import net.parkvision.parkvisionbackend.service.EmailSenderService;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ParkVisionBackendApplication {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private DataSeeder dataSeeder;

    public static void main(String[] args) {
        SpringApplication.run(ParkVisionBackendApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void triggerWhenStarts() throws Exception {
        System.out.println("Application started ... sending email");

        // emailSenderService.sendEmail("@gmail.com", "TEST body
        // <ul><li>jeden</li></ul>", "Test topic 2");

        // List<String> emails = new
        // ArrayList<>(List.of("maciejmakarakontakt@gmail.com"));

        // for (String email : emails) {
        // emailSenderService.sendMessageWithAttachment(email, "Powiadomienie z systemu
        // ParkVision", "Twój pojazd " +
        // "zostaje wyeskortowany z parkingu przez grupę wojskowych dronów.",
        // "C:\\Users\\filip\\Documents\\Code\\parkvision-backend\\img.png");
        // }
    }

//    @PostConstruct
//    public void seedData() {
//        dataSeeder.seedData();
//    }

}
