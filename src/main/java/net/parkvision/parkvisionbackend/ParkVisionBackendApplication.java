package net.parkvision.parkvisionbackend;

import jakarta.annotation.PostConstruct;
import net.parkvision.parkvisionbackend.service.DataSeeder;
import net.parkvision.parkvisionbackend.service.EmailSenderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
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
        System.out.println("Application started ...");
    }

    @PostConstruct
    public void seedData() {
        dataSeeder.seedData();
    }

}
