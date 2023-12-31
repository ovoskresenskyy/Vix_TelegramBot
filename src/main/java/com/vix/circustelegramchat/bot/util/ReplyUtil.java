package com.vix.circustelegramchat.bot.util;

import com.vix.circustelegramchat.bot.Constants;
import com.vix.circustelegramchat.model.Performance;
import com.vix.circustelegramchat.model.Ticket;
import com.vix.circustelegramchat.model.Visitor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.time.LocalDate;

/**
 * It is the util class to generating answers texts.
 * It helps to keep all these texts at one place, to make it easier to change or update.
 */
@Component
@RequiredArgsConstructor
public class ReplyUtil implements Constants {

    private final BotUtil botUtil;

    /**
     * Answer after /start command received
     *
     * @param visitor - The visitor who clicked the command
     * @return - Reply text
     */
    public String welcomeText(Visitor visitor) {
        StringBuilder reply = new StringBuilder(getGreetings(visitor));
        reply.append("\n\nSupported commands: \n");

        for (BotCommand botCommand : botUtil.getSupportedCommands()) {
            reply.append(botCommand.getCommand())
                    .append(" - ")
                    .append(botCommand.getDescription())
                    .append("\n");
        }

        return reply.toString();
    }

    /**
     * This method is making greeting reply text for registered and non-registered visitors
     *
     * @param visitor - The visitor to be greeted
     * @return - Reply text
     */
    private String getGreetings(Visitor visitor) {
        return "Welcome "
                + (visitor.isRegistered() ? visitor.getFullName() : "")
                + " to our chat-bot.\n" +
                "Here you can order tickets for our performances.";
    }

    /**
     * This method is making reply for unregistered user who pressed "Show my data" button
     *
     * @return - Reply text
     */
    public String unregisteredUserData() {
        return """
                We currently have no information about you.
                We will save your data after ordering tickets.""";
    }

    /**
     * This method is making reply when tickets not found.
     *
     * @return - Reply text
     */
    public String ticketsNotFound() {
        return "You currently have no tickets";
    }

    /**
     * This method is making reply when upcoming performances not found.
     *
     * @return - Reply text
     */
    public String upcomingPerformancesNotFound() {
        return """
                Sorry, there are no upcoming performances.
                Come back later.""";
    }

    /**
     * This method is making reply when performances are found.
     * And propose to select one of them.
     *
     * @param performanceDate - Date of the performances
     * @return - Reply text
     */
    public String chosePerformance(LocalDate performanceDate) {
        return """
                    
                Please chose the performance.
                Date:""" + performanceDate.format(PERFORMANCE_DATE_FORMAT);
    }

    /**
     * This method is making reply of starting the registration.
     *
     * @return - Reply text
     */
    public String registrationStart() {
        return """
                To get started you have to register.
                Enter please your phone number in XXXYYYYYYY format.""";
    }

    /**
     * This method is making reply of saved phone number and ask to
     * enter the first name
     *
     * @param phoneNumber - Phone number which was just entered and saved
     * @return - Reply text
     */
    public String phoneNumberEntered(String phoneNumber) {
        return "Phone number "
                + phoneNumber
                + " was saved.\n\nNow please enter yor first name.";
    }

    /**
     * This method is making reply of asking to enter the new phone number.
     *
     * @return - Reply text
     */
    public String phoneNumberChanging() {
        return "Please enter your new phone number in XXXYYYYYYY format.";
    }

    /**
     * This method is making reply of saved first name ask to
     * enter the last name
     *
     * @param name - First name which was just entered and saved
     * @return - Reply text
     */
    public String firstNameEntered(String name) {
        return "First name "
                + name
                + " was saved.\n\nNow please enter yor last name.";
    }

    /**
     * This method is making reply of asking to enter the new first name.
     *
     * @return - Reply text
     */
    public String firstNameChanging() {
        return "Please enter your new first name.";
    }

    /**
     * This method is making reply of saved last name
     *
     * @param name - Last name which was just entered and saved
     * @return - Reply text
     */
    public String lastNameEntered(String name) {
        return "Last name "
                + name
                + " was saved.";
    }

    /**
     * This method is making reply of asking to enter the new last name.
     *
     * @return - Reply text
     */
    public String lastNameChanging() {
        return "Please enter your new last name.";
    }

    /**
     * This method is making reply of entered invalid phone number.
     *
     * @return - Reply text
     */
    public String phoneNumberInvalid() {
        return """
                The number does not match the format.
                Please try again.""";
    }

    /**
     * This method is making reply of entered invalid first or last names.
     *
     * @return - Reply text
     */
    public String nameInvalid() {
        return """
                Entered name is not valid.
                Please use only latin characters, no special characters.""";
    }

    /**
     * This method is making reply of unsupported action.
     *
     * @return - Reply text
     */
    public String unsupportedAction() {
        return "Sorry, can't handle it.";
    }

    /**
     * This method is making reply of just selected performance,
     * show all data to be saved and ask user to approve it.
     *
     * @param visitor     - The visitor to be saved in the ticket
     * @param performance - The performance was selected
     * @return - Reply text
     */
    public String performanceSelected(Visitor visitor, Performance performance) {
        return "Perfect choice! You choose " + performance.getName()
                + "\nPerformance starts " + performance.getDate().format(PERFORMANCE_DATE_FORMAT)
                + " at " + performance.getTime()
                + "\n\nVisitor data:\n"
                + visitor.getFullName()
                + "\nPhone number: " + visitor.getPhoneNumber()
                + "\n\nPress 'Accept' to approve.";
    }


    /**
     * This method is making reply of ordering the ticket.
     *
     * @return - Reply text
     */
    public String ticketOrdered() {
        return "Congrats, ticket was ordered.";
    }

    /**
     * This method is making reply of the ordered ticket with all stored data.
     *
     * @param ticket      - The ordered ticket
     * @param performance - The chosen performance
     * @return - Reply text
     */
    public String getOrderedTicketDescription(Ticket ticket, Performance performance) {
        return "#" + ticket.getId()
                + "\n" + performance.toString()
                + "\n\nVisitor: "
                + ticket.getVisitorFirstName() + " " + ticket.getVisitorLastName();
    }
}
