package com.vix.circustelegramchat.bot.handler;

import com.vix.circustelegramchat.bot.util.ReplyUtil;
import com.vix.circustelegramchat.bot.util.BotUtil;
import com.vix.circustelegramchat.bot.util.ButtonCreator;
import com.vix.circustelegramchat.bot.util.KeyboardCreator;
import com.vix.circustelegramchat.bot.Constants;
import com.vix.circustelegramchat.model.Visitor;
import com.vix.circustelegramchat.model.Performance;
import com.vix.circustelegramchat.model.Ticket;
import com.vix.circustelegramchat.service.VisitorService;
import com.vix.circustelegramchat.service.PerformanceService;
import com.vix.circustelegramchat.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is responsible for handling all users inputs.
 * It can handle preinstalled commands or just users inputs by keyboard
 * <p>
 * Entered commands handling by matched methods
 * Users entered input handling according to State or by isChattingWithOperator mark
 */
@Component
@RequiredArgsConstructor
public class TextHandler implements Constants {

    private final BotUtil botUtil;
    private final ButtonCreator buttonCreator;
    private final KeyboardCreator keyboardCreator;
    private final ReplyUtil replyUtil;
    private final TicketService ticketService;
    private final VisitorService visitorService;
    private final PerformanceService performanceService;

    /**
     * This method is responsible for handling everything that users entered by keyboard
     * or by command menu.
     * <p>
     * Commands are handling by matched methods, other input handling custom.
     *
     * @param visitor - The user, who makes the input
     * @param text    - Inputted text
     * @return List of one or a few reply messages
     */
    public List<SendMessage> handle(Visitor visitor, String text) {
        return switch (text) {
            case COMMAND_START -> commandStartReceived(visitor);
            case COMMAND_SHOW_MY_DATA -> commandShowMyDataReceived(visitor);
            case COMMAND_ORDER_TICKET -> commandOrderTicketReceived(visitor);
            case COMMAND_SHOW_MY_TICKETS -> commandShowMyTicketsReceived(visitor);
            case COMMAND_OPERATOR -> commandOperatorReceived(visitor);
            default -> handleUserInput(visitor, text);
        };
    }

    /**
     * This method is responsible for making reply after receiving the start command
     *
     * @param visitor - The user who send the command
     * @return New SendMessage with greetings and list of supported commands
     */
    private List<SendMessage> commandStartReceived(Visitor visitor) {
        return List.of(botUtil.initNewMessage(visitor.getChatId(), replyUtil.welcomeText(visitor)));
    }

    /**
     * This method is responsible for making reply after receiving the "show my data" command
     *
     * @param visitor - The user who send the command
     * @return New SendMessage with stored data if visitor is registered, or empty data if not
     */
    private List<SendMessage> commandShowMyDataReceived(Visitor visitor) {
        String chatId = visitor.getChatId();
        if (visitor.getState().equals(STATE_EMPTY)) {
            return List.of(botUtil.initNewMessage(chatId, replyUtil.unregisteredUserData()));
        }

        return List.of(botUtil.initNewMessage(chatId,
                visitor.toString(),
                keyboardCreator.getOneButtonKeyBoard(buttonCreator.getChangeMyDataButton())));
    }

    /**
     * This method is responsible for making reply after receiving the "Order ticket" command
     *
     * @param visitor - The user who send the command
     * @return New SendMessage with upcoming performances to choose if visitor is registered,
     * or start of registration procedure if not.
     */
    private List<SendMessage> commandOrderTicketReceived(Visitor visitor) {
        if (visitor.isRegistered()) {
            return showUpcomingPerformances(visitor);
        } else {
            visitor.setState(STATE_PHONE_NUMBER_ENTERING);
            visitorService.save(visitor);
            return List.of(botUtil.initNewMessage(visitor.getChatId(), replyUtil.registrationStart()));
        }
    }

    /**
     * This method is responsible for making reply after receiving the "Show my tickets" command
     *
     * @param visitor - The user who send the command
     * @return New SendMessage with ordered tickets if they are present, TicketNotFound message if not
     */
    private List<SendMessage> commandShowMyTicketsReceived(Visitor visitor) {
        List<Ticket> tickets = ticketService.findAllByVisitorId(visitor.getId());

        if (tickets.isEmpty()) {
            return List.of(botUtil.initNewMessage(visitor.getChatId(), replyUtil.ticketsNotFound()));
        }

        List<SendMessage> answers = new ArrayList<>();
        for (Ticket ticket : tickets) {
            Performance performance = performanceService.findById(ticket.getPerformanceId());
            answers.add(botUtil.initNewMessage(visitor.getChatId(),
                    replyUtil.getOrderedTicketDescription(ticket, performance),
                    keyboardCreator.getOneButtonKeyBoard(buttonCreator.getTicketButton(ticket.getId()))));
        }

        return answers;
    }

    private List<SendMessage> commandOperatorReceived(Visitor visitor) {
        return null;
    }

    /**
     * This method is responsible for handling the users input.
     * If visitors state matches with the registration procedure it will ask to enter the data, store it
     * and carry through the registration.
     *
     * @param visitor - The user who send the command or input a text
     * @param text    - Entered text by the user
     * @return One or a few messages according to the users input or UnsupportedCommand message
     * if can't handle input.
     */
    private List<SendMessage> handleUserInput(Visitor visitor, String text) {
        return switch (visitor.getState()) {
            case STATE_PHONE_NUMBER_ENTERING, STATE_PHONE_NUMBER_CHANGING -> handlePhoneNumberInput(visitor, text);
            case STATE_FIRST_NAME_ENTERING, STATE_FIRST_NAME_CHANGING -> handleFirstNameInput(visitor, text);
            case STATE_LAST_NAME_ENTERING, STATE_LAST_NAME_CHANGING -> handleLastNameInput(visitor, text);
            default -> unSupportedCommandReceived(visitor);
        };
    }

    /**
     * This method is a part of the registration procedure.
     * It processed the entered phone number.
     * Save it if it's valid, change the state and continuous the registration
     *
     * @param visitor     - The user who entered the phone number
     * @param phoneNumber - Inputted phone number
     * @return Reply message to continue the registration
     */
    private List<SendMessage> handlePhoneNumberInput(Visitor visitor, String phoneNumber) {
        if (botUtil.isPhoneNumberInvalid(phoneNumber)) {
            return List.of(botUtil.initNewMessage(visitor.getChatId(), replyUtil.phoneNumberInvalid()));
        }

        if (isDataChanging(visitor.getState())) {
            return updateVisitorData(visitor, phoneNumber);
        }

        visitor.setPhoneNumber(phoneNumber);
        visitor.setState(STATE_FIRST_NAME_ENTERING);
        visitorService.save(visitor);

        String text = replyUtil.phoneNumberEntered(phoneNumber);
        return List.of(botUtil.initNewMessage(visitor.getChatId(), text));
    }

    /**
     * This method is a part of the registration procedure.
     * It processed the entered first name.
     * Save it if it's valid, change the state and continuous the registration
     *
     * @param visitor - The user who entered the first name
     * @param name    - Inputted first name
     * @return Reply message to continue the registration
     */
    private List<SendMessage> handleFirstNameInput(Visitor visitor, String name) {
        if (botUtil.isNameInvalid(name)) {
            return List.of(botUtil.initNewMessage(visitor.getChatId(), replyUtil.nameInvalid()));
        }

        if (isDataChanging(visitor.getState())) {
            return updateVisitorData(visitor, name);
        }

        visitor.setFirstName(name);
        visitor.setState(STATE_LAST_NAME_ENTERING);
        visitorService.save(visitor);

        String text = replyUtil.firstNameEntered(name);
        return List.of(botUtil.initNewMessage(visitor.getChatId(), text));
    }

    /**
     * This method is a part of the registration procedure.
     * It processed the entered last name.
     * Save it if it's valid, change the state and continuous the registration
     *
     * @param visitor - The user who entered the last name
     * @param name    - Inputted last name
     * @return Reply message to show the upcoming performances if registration is completed
     */
    private List<SendMessage> handleLastNameInput(Visitor visitor, String name) {
        if (botUtil.isNameInvalid(name)) {
            return List.of(botUtil.initNewMessage(visitor.getChatId(), replyUtil.nameInvalid()));
        }

        if (isDataChanging(visitor.getState())) {
            return updateVisitorData(visitor, name);
        }

        visitor.setLastName(name);
        visitor.setState(STATE_REGISTERED);
        visitorService.save(visitor);

        List<SendMessage> answers = new ArrayList<>();

        String firstMessageText = replyUtil.lastNameEntered(name);
        SendMessage firstMessage = botUtil.initNewMessage(visitor.getChatId(), firstMessageText);
        answers.add(firstMessage);

        Optional<LocalDate> optionalPerformanceDate = performanceService.getNextPerformanceDate(LocalDate.now());
        if (optionalPerformanceDate.isPresent()) {
            LocalDate performanceDate = optionalPerformanceDate.get();
            answers.add(botUtil.initNewMessage(visitor.getChatId(),
                    replyUtil.chosePerformance(performanceDate),
                    keyboardCreator.getPerformanceKeyboard(performanceDate)));
        } else {
            firstMessage.setText(firstMessage.getText() + replyUtil.upcomingPerformancesNotFound());
        }

        return answers;
    }

    /**
     * This method is responsible for making the reply of inputted command or text is can't be handled.
     *
     * @param visitor - The user who entered the unsupported command
     * @return New message with UnsupportedAction information
     */
    private List<SendMessage> unSupportedCommandReceived(Visitor visitor) {
        return List.of(botUtil.initNewMessage(visitor.getChatId(), replyUtil.unsupportedAction()));
    }

    /**
     * This method is responsible updating visitors data if it decides to change something
     * It can change phone number, first and last names and then change the state to Registered.
     * Because it's only one field changing.
     *
     * @param visitor  - The visitor to be updated
     * @param newValue - New value to be saved
     * @return The message with all data to be showed to the user.
     * For make sure it's saved correctly
     */
    private List<SendMessage> updateVisitorData(Visitor visitor, String newValue) {
        switch (visitor.getState()) {
            case STATE_PHONE_NUMBER_CHANGING -> visitor.setPhoneNumber(newValue);
            case STATE_FIRST_NAME_CHANGING -> visitor.setFirstName(newValue);
            case STATE_LAST_NAME_CHANGING -> visitor.setLastName(newValue);
        }
        visitor.setState(STATE_REGISTERED);
        visitorService.save(visitor);
        return commandShowMyDataReceived(visitor);
    }

    /**
     * This method checks is current state means that user is not registering at the moment,
     * but changing existed data.
     *
     * @param state - Current visitor state
     * @return True if it's changing data action, false if not
     */
    private boolean isDataChanging(String state) {
        return state.equals(STATE_PHONE_NUMBER_CHANGING)
                || state.equals(STATE_FIRST_NAME_CHANGING)
                || state.equals(STATE_LAST_NAME_CHANGING);
    }

    /**
     * This method shows upcoming performances from current date
     *
     * @param visitor - The visitor to whom performances will be showed
     * @return The list of upcoming performances to choose if they are present,
     * or PerformancesNotFound message if not.
     */
    private List<SendMessage> showUpcomingPerformances(Visitor visitor) {
        Optional<LocalDate> optionalPerformanceDate = performanceService.getNextPerformanceDate(LocalDate.now());
        if (optionalPerformanceDate.isPresent()) {
            LocalDate performanceDate = optionalPerformanceDate.get();
            String text = replyUtil.chosePerformance(performanceDate);
            List<List<InlineKeyboardButton>> keyboard = keyboardCreator.getPerformanceKeyboard(performanceDate);

            return List.of(botUtil.initNewMessage(visitor.getChatId(), text, keyboard));
        } else {
            return List.of(botUtil.initNewMessage(visitor.getChatId(), replyUtil.upcomingPerformancesNotFound()));
        }
    }
}
