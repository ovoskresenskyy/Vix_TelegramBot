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
public class AnswerTextMaker implements Constants {

    private final BotUtil botUtil;

    public String welcomeText(Visitor visitor) {
        String greetings = visitor.getState().equals(STATE_REGISTERED)
                ? "Welcome " + visitor.getFullName()
                : TEXT_WELCOME_UNREGISTERED;

        StringBuilder supportedCommands = new StringBuilder(greetings);
        supportedCommands.append("\n\nSupported commands: \n");

        for (BotCommand botCommand : botUtil.getSupportedCommands()) {
            supportedCommands.append(botCommand.getCommand())
                    .append(" - ")
                    .append(botCommand.getDescription())
                    .append("\n");
        }

        return supportedCommands.toString();
    }

    public String phoneNumberEntered(String phoneNumber) {
        return "Phone number " + phoneNumber + " was saved.\n\nNow please enter yor first name.";
    }

    public String firstNameEntered(String name) {
        return "First name " + name + " was saved.\n\nNow please enter yor last name.";
    }

    public String lastNameEntered(String name) {
        return "Last name " + name + " was saved.";
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

    public String navigationButtonPressed(LocalDate performanceDate) {
        return TEXT_CHOSE_PERFORMANCE + performanceDate.format(PERFORMANCE_DATE_FORMAT);
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
