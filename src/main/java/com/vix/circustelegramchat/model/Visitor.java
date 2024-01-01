package com.vix.circustelegramchat.model;

import com.vix.circustelegramchat.bot.Constants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "visitor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "chat_id", unique = true)
    private String chatId;
    private String state;
    @Column(name = "operator_chat_id")
    private String operatorChatId;

    @Override
    public String toString() {
        return "First name: " + firstName
                + "\nLast name: " + lastName
                + "\nPhone number: " + phoneNumber;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isRegistered() {
        return state.equals(Constants.STATE_REGISTERED);
    }
}
