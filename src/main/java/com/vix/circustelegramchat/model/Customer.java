package com.vix.circustelegramchat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

//    @NotBlank
    @Column(name = "first_name")
    private String firstName;

//    @NotBlank
    @Column(name = "last_name")
    private String lastName;

//    @NotBlank
    @Column(name = "phone_number")
    private String phoneNumber;

//    @NotNull
    @Column(name = "chat_id", unique = true)
    private String chatId;

//    @NotBlank
    @Column(name = "state")
    private String state;

    @Override
    public String toString() {
        return "First name: " + firstName
                + "\nLast name: " + lastName
                + "\nPhone number: " + phoneNumber;
    }
}
