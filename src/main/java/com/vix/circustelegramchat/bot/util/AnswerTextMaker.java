package com.vix.circustelegramchat.bot.util;

import com.vix.circustelegramchat.config.Constants;
import com.vix.circustelegramchat.model.Customer;
import com.vix.circustelegramchat.model.Performance;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AnswerTextMaker implements Constants {

    public String phoneNumberEntered(String phoneNumber) {
        return "Phone number " + phoneNumber + " was saved.\n\nNow please enter yor first name.";
    }

    public String firstNameEntered(String name) {
        return "First name " + name + " was saved.\n\nNow please enter yor last name.";
    }

    public String lastNameEntered(String name) {
        return "Last name " + name + " was saved.";
    }

    public String performanceSelected(Customer customer, Performance performance) {
        return "Perfect choice! You choose " + performance.getName()
                + "\nPerformance starts " + performance.getDate().format(PERFORMANCE_DATE_FORMAT)
                + " at " + performance.getTime()
                + "\n\nVisitor data:\n"
                + customer.getFirstName() + " " + customer.getLastName()
                + "Phone number: " + customer.getPhoneNumber()
                + "\n\nPress 'Accept' to approve.";
    }

    public String navigationButtonPressed(LocalDate performanceDate) {
        return TEXT_CHOSE_PERFORMANCE + performanceDate.format(PERFORMANCE_DATE_FORMAT);
    }

    public String ticketOrdered() {
        return "Congrats, ticket was ordered.";
    }
}
