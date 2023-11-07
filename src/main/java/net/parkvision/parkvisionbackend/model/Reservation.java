package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String startDate;
    private String endDate;
    private String registrationNumber;

    @ManyToOne
    private User user;
    @ManyToOne
    private ParkingSpot parkingSpot;
    @OneToMany
    private List<Payment> payment;


    public OffsetDateTime getStartDate() {
        if (startDate != null) {
            return OffsetDateTime.parse(startDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        return null; // Handle null values if needed
    }

    public void setStartDate(OffsetDateTime startDate) {
        if (startDate != null) {
            this.startDate = startDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else {
            this.startDate = null; // Handle null values if needed
        }
    }

    // Getter and setter methods for endDate
    public OffsetDateTime getEndDate() {
        if (endDate != null) {
            return OffsetDateTime.parse(endDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        return null; // Handle null values if needed
    }

    public void setEndDate(OffsetDateTime endDate) {
        if (endDate != null) {
            this.endDate = endDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else {
            this.endDate = null; // Handle null values if needed
        }
    }
}
