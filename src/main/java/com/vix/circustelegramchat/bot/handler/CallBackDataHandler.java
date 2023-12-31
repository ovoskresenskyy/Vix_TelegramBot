package com.vix.circustelegramchat.bot.handler;

import com.vix.circustelegramchat.bot.Constants;
import com.vix.circustelegramchat.bot.util.ReplyUtil;
import com.vix.circustelegramchat.bot.util.BotUtil;
import com.vix.circustelegramchat.bot.util.ButtonCreator;
import com.vix.circustelegramchat.bot.util.KeyboardCreator;
import com.vix.circustelegramchat.model.Performance;
import com.vix.circustelegramchat.model.Ticket;
import com.vix.circustelegramchat.model.Visitor;
import com.vix.circustelegramchat.service.PerformanceService;
import com.vix.circustelegramchat.service.TicketService;
import com.vix.circustelegramchat.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;

/**
 * This class is responsible for handling all button pressings.
 * It can handle preinstalled button or custom button pressings.
 * <p>
 * When we say about custom buttons we say about the buttons,
 * where some additional data inside the CBD, like ticket id, performance id, etc.
 * <p>
 * We will change the message instead of sending the new to make chat with user more clear.
 */
@Component
@RequiredArgsConstructor
public class CallBackDataHandler implements Constants {

    private final BotUtil botUtil;
    private final ButtonCreator buttonCreator;
    private final KeyboardCreator keyboardCreator;
    private final ReplyUtil replyUtil;
    private final PerformanceService performanceService;
    private final TicketService ticketService;
    private final VisitorService visitorService;

    /**
     * This method is responsible for handling all button pressings
     *
     * @param visitor      - The user who pressed the button
     * @param message      - The message to be updated after pressing
     * @param callBackData - The call back data received from pressing the button
     * @return Edited message after pressing the button
     */
    public EditMessageText handle(Visitor visitor, Message message, String callBackData) {
        return switch (callBackData) {
            case CBD_CHANGE_MY_DATA -> changeMyDataButtonPressed(message);
            case CBD_CHANGE_FIRST_NAME -> changeFirstNameButtonPressed(visitor, message);
            case CBD_CHANGE_LAST_NAME -> changeLastNameButtonPressed(visitor, message);
            case CBD_CHANGE_PHONE_NUMBER -> changePhoneNumberButtonPressed(visitor, message);
            default -> customButtonPressed(visitor, message, callBackData);
        };
    }

    /**
     * This method changes the existed message by adding change data keyboard
     *
     * @param message - The message to be changed
     * @return Received message with the new keyboard.
     * This keyboard contains change data buttons like change phone number, change name, etc.
     */
    private EditMessageText changeMyDataButtonPressed(Message message) {
        return botUtil.initNewEditMessageText(message,
                message.getText(),
                keyboardCreator.getChangeDataKeyboard());
    }

    /**
     * This method change visitors state to _CHANGING state and ask to enter new first name
     * Current message edited to simple asking to enter new data
     *
     * @param visitor - The visitor to be updated
     * @param message - The message to be changed
     * @return Edited message with asking to enter new data
     */
    private EditMessageText changeFirstNameButtonPressed(Visitor visitor, Message message) {
        visitorService.updateVisitor(visitor, STATE_FIRST_NAME_CHANGING);
        return botUtil.initNewEditMessageText(message, replyUtil.firstNameChanging());
    }

    /**
     * This method change visitors state to _CHANGING state and ask to enter new last name
     * Current message edited to simple asking to enter new data
     *
     * @param visitor - The visitor to be updated
     * @param message - The message to be changed
     * @return Edited message with asking to enter new data
     */
    private EditMessageText changeLastNameButtonPressed(Visitor visitor, Message message) {
        visitorService.updateVisitor(visitor, STATE_LAST_NAME_CHANGING);
        return botUtil.initNewEditMessageText(message, replyUtil.lastNameChanging());
    }

    /**
     * This method change visitors state to _CHANGING state and ask to enter new phone number
     * Current message edited to simple asking to enter new data
     *
     * @param visitor - The visitor to be updated
     * @param message - The message to be changed
     * @return Edited message with asking to enter new data
     */
    private EditMessageText changePhoneNumberButtonPressed(Visitor visitor, Message message) {
        visitorService.updateVisitor(visitor, STATE_PHONE_NUMBER_CHANGING);
        return botUtil.initNewEditMessageText(message, replyUtil.phoneNumberChanging());
    }

    /**
     * This method is responsible to handle custom button pressing.
     * Custom buttons contains additional data like id or data.
     *
     * @param visitor      - The visitor who pressed the button
     * @param message      - The message to be edited
     * @param callBackData - Call back data from custom button
     * @return Edited message according to received CBD
     */
    private EditMessageText customButtonPressed(Visitor visitor, Message message, String callBackData) {
        if (callBackData.contains(CBD_SHOW_PERFORMANCES_DATE_)) {
            return navigationButtonPressed(message, callBackData);
        } else if (callBackData.contains(CBD_SELECTED_PERFORMANCE_ID_)) {
            return performanceSelected(visitor, message, callBackData);
        } else if (callBackData.contains(CBD_ACCEPTED_PERFORMANCE_ID_)) {
            return performanceAccepted(visitor, message, callBackData);
        } else {
            return unSupportedButtonPressed(message);
        }
    }

    /**
     * This method is responsible to go through all exist performances by changing date
     *
     * @param message      - The message to be edited
     * @param callBackData - Call back data from navigation button
     * @return The edited message with list of performances buttons of the received new data
     * and new navigation buttons
     */
    private EditMessageText navigationButtonPressed(Message message, String callBackData) {
        LocalDate performanceDate = botUtil.extractDate(callBackData);

        return botUtil.initNewEditMessageText(message,
                replyUtil.chosePerformance(performanceDate),
                keyboardCreator.getPerformanceKeyboard(performanceDate));
    }

    /**
     * This method is change the existed message after selecting a specific performance
     *
     * @param visitor      - The visitor who chose the performance
     * @param message      - The message to be edited
     * @param callBackData - Call back data from performance button
     * @return Edited message with info about chosen performance and 'Acceptation' buttons
     */
    private EditMessageText performanceSelected(Visitor visitor, Message message, String callBackData) {
        int performanceId = botUtil.extractId(callBackData);
        Performance performance = performanceService.findById(performanceId);

        return botUtil.initNewEditMessageText(message,
                replyUtil.performanceSelected(visitor, performance),
                keyboardCreator.getPerformanceAcceptationKeyboard(performanceId));
    }

    /**
     * This method saves the ticket after performance acceptation.
     * Then edit existed message by adding 'Get ticket' button
     *
     * @param visitor      - The visitor who pressed the button
     * @param message      - The message to be edited
     * @param callBackData - Call back data from performance accept button
     * @return Edited message with possibility to get the file with the ticket
     */
    private EditMessageText performanceAccepted(Visitor visitor, Message message, String callBackData) {
        Ticket ticket = ticketService.save(Ticket.builder()
                .performanceId(botUtil.extractId(callBackData))
                .visitorId(visitor.getId())
                .visitorFirstName(visitor.getFirstName())
                .visitorLastName(visitor.getLastName())
                .visitorPhoneNumber(visitor.getPhoneNumber())
                .build());

        return botUtil.initNewEditMessageText(message,
                replyUtil.ticketOrdered(),
                keyboardCreator.getOneButtonKeyBoard(buttonCreator.getTicketButton(ticket.getId())));
    }

    /**
     * This method edits existed message with 'Unsupported action' information
     *
     * @param message - The message to be edited
     * @return Edited message with 'Unsupported action' information
     */
    private EditMessageText unSupportedButtonPressed(Message message) {
        return EditMessageText.builder()
                .chatId(message.getChatId())
                .messageId(message.getMessageId())
                .text(replyUtil.unsupportedAction())
                .build();
    }
}
