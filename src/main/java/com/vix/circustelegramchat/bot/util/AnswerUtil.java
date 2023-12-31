package com.vix.circustelegramchat.bot.util;

import com.vix.circustelegramchat.bot.Constants;
import com.vix.circustelegramchat.model.Performance;
import com.vix.circustelegramchat.model.Ticket;
import com.vix.circustelegramchat.model.Visitor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AnswerUtil implements Constants {

    private final BotUtil botUtil;

    public String welcomeText(Visitor visitor) {
        StringBuilder answer = new StringBuilder(getGreetings(visitor));
        answer.append("\n\nSupported commands: \n");

        for (BotCommand botCommand : botUtil.getSupportedCommands()) {
            answer.append(botCommand.getCommand())
                    .append(" - ")
                    .append(botCommand.getDescription())
                    .append("\n");
        }

        return answer.toString();
    }

    private String getGreetings(Visitor visitor) {
        return "Welcome "
                + (visitor.isRegistered() ? visitor.getFullName() : "")
                + " to our chat-bot.\n" +
                "Here you can order tickets for our performances.";
    }

    public String unregisteredUserData() {
        return """
                We currently have no information about you.
                We will save your data after ordering tickets.""";
    }

    public String ticketsNotFound() {
        return "You currently have no tickets";
    }

    public String upcomingPerformancesNotFound() {
        return """
                Sorry, there are no upcoming performances.
                Come back later.""";
    }

    public String chosePerformance(LocalDate performanceDate) {
        return """
                    
                Please chose the performance.
                Date:""" + performanceDate.format(PERFORMANCE_DATE_FORMAT);
    }

    public String registrationStart() {
        return """
                To get started you have to register.
                Enter please your phone number in XXXYYYYYYY format.""";
    }

    public String phoneNumberEntered(String phoneNumber) {
        return "Phone number " + phoneNumber + " was saved.\n\nNow please enter yor first name.";
    }

    public String phoneNumberChanging() {
        return "Please enter your new phone number in XXXYYYYYYY format.";
    }

    public String firstNameEntered(String name) {
        return "First name " + name + " was saved.\n\nNow please enter yor last name.";
    }

    public String firstNameChanging() {
        return "Please enter your new first name.";
    }

    public String lastNameEntered(String name) {
        return "Last name " + name + " was saved.";
    }

    public String lastNameChanging() {
        return "Please enter your new last name.";
    }

    public String phoneNumberInvalid() {
        return """
                The number does not match the format.
                Please try again.""";
    }

    public String nameInvalid() {
        return """
                Entered name is not valid.
                Please use only latin characters, no special characters.""";
    }

    public String unsupportedAction() {
        return "Sorry, can't handle it.";
    }

    public String performanceSelected(Visitor visitor, Performance performance) {
        return "Perfect choice! You choose " + performance.getName()
                + "\nPerformance starts " + performance.getDate().format(PERFORMANCE_DATE_FORMAT)
                + " at " + performance.getTime()
                + "\n\nVisitor data:\n"
                + visitor.getFullName()
                + "\nPhone number: " + visitor.getPhoneNumber()
                + "\n\nPress 'Accept' to approve.";
    }

    public String ticketOrdered() {
        return "Congrats, ticket was ordered.";
    }

    public String getOrderedTicketDescription(Ticket ticket, Performance performance) {
        return "#" + ticket.getId()
                + "\n" + performance.toString()
                + "\n\nVisitor: "
                + ticket.getVisitorFirstName() + " " + ticket.getVisitorLastName();
    }
}
