package com.vix.circustelegramchat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(name = "performance_id")
    private int performanceId;
    @Column(name = "visitor_id")
    private int visitorId;
    @Column(name = "visitor_first_name")
    private String visitorFirstName;
    @Column(name = "visitor_last_name")
    private String visitorLastName;
    @Column(name = "visitor_phone_number")
    private String visitorPhoneNumber;
}
