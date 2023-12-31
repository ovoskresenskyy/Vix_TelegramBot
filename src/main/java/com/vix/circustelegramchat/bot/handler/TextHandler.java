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

    private List<SendMessage> commandStartReceived(Visitor visitor) {
        return List.of(botUtil.initNewMessage(visitor.getChatId(), replyUtil.welcomeText(visitor)));
    }

    private List<SendMessage> commandShowMyDataReceived(Visitor visitor) {
        String chatId = visitor.getChatId();
        if (visitor.getState().equals(STATE_EMPTY)) {
            return List.of(botUtil.initNewMessage(chatId, replyUtil.unregisteredUserData()));
        }

        return List.of(botUtil.initNewMessage(chatId,
                visitor.toString(),
                keyboardCreator.getOneButtonKeyBoard(buttonCreator.getChangeMyDataButton())));
    }

    private List<SendMessage> commandOrderTicketReceived(Visitor visitor) {
        if (visitor.isRegistered()) {
            return showUpcomingPerformances(visitor);
        } else {
            visitorService.updateVisitor(visitor, STATE_PHONE_NUMBER_ENTERING);
            return List.of(botUtil.initNewMessage(visitor.getChatId(), replyUtil.registrationStart()));
        }
    }

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

    private List<SendMessage> handleUserInput(Visitor visitor, String text) {
        return switch (visitor.getState()) {
            case STATE_PHONE_NUMBER_ENTERING, STATE_PHONE_NUMBER_CHANGING -> handlePhoneNumberInput(visitor, text);
            case STATE_FIRST_NAME_ENTERING, STATE_FIRST_NAME_CHANGING -> handleFirstNameInput(visitor, text);
            case STATE_LAST_NAME_ENTERING, STATE_LAST_NAME_CHANGING -> handleLastNameInput(visitor, text);
            default -> unSupportedCommandReceived(visitor.getChatId());
        };
    }

    private List<SendMessage> unSupportedCommandReceived(String chatId) {
        return List.of(botUtil.initNewMessage(chatId, replyUtil.unsupportedAction()));
    }

    private List<SendMessage> handlePhoneNumberInput(Visitor visitor, String phoneNumber) {
        if (botUtil.isPhoneNumberInvalid(phoneNumber)) {
            return List.of(botUtil.initNewMessage(visitor.getChatId(), replyUtil.phoneNumberInvalid()));
        }

        if (isDataChanging(visitor.getState())) {
            return updateVisitorData(visitor, phoneNumber);
        }

        visitorService.updateVisitor(visitor, STATE_FIRST_NAME_ENTERING, phoneNumber);

        String text = replyUtil.phoneNumberEntered(phoneNumber);
        return List.of(botUtil.initNewMessage(visitor.getChatId(), text));
    }

    private List<SendMessage> handleFirstNameInput(Visitor visitor, String name) {
        if (botUtil.isNameInvalid(name)) {
            return List.of(botUtil.initNewMessage(visitor.getChatId(), replyUtil.nameInvalid()));
        }

        if (isDataChanging(visitor.getState())) {
            return updateVisitorData(visitor, name);
        }

        visitorService.updateVisitor(visitor, STATE_LAST_NAME_ENTERING, name);

        String text = replyUtil.firstNameEntered(name);
        return List.of(botUtil.initNewMessage(visitor.getChatId(), text));
    }

    private List<SendMessage> handleLastNameInput(Visitor visitor, String name) {
        if (botUtil.isNameInvalid(name)) {
            return List.of(botUtil.initNewMessage(visitor.getChatId(), replyUtil.nameInvalid()));
        }

        if (isDataChanging(visitor.getState())) {
            return updateVisitorData(visitor, name);
        }

        visitorService.updateVisitor(visitor, STATE_REGISTERED, name);

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

    private List<SendMessage> updateVisitorData(Visitor visitor, String update) {
        switch (visitor.getState()) {
            case STATE_PHONE_NUMBER_CHANGING -> visitor.setPhoneNumber(update);
            case STATE_FIRST_NAME_CHANGING -> visitor.setFirstName(update);
            case STATE_LAST_NAME_CHANGING -> visitor.setLastName(update);
        }
        visitorService.updateVisitor(visitor, STATE_REGISTERED);
        return commandShowMyDataReceived(visitor);
    }

    private boolean isDataChanging(String state) {
        return state.equals(STATE_PHONE_NUMBER_CHANGING)
                || state.equals(STATE_FIRST_NAME_CHANGING)
                || state.equals(STATE_LAST_NAME_CHANGING);
    }

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
