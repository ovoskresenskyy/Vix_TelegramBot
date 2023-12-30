package com.vix.circustelegramchat.bot.handler;

import com.vix.circustelegramchat.bot.util.AnswerTextMaker;
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
    private final AnswerTextMaker answerTextMaker;
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
        return List.of(botUtil.initNewMessage(visitor.getChatId(), answerTextMaker.welcomeText(visitor)));
    }

    private List<SendMessage> commandShowMyDataReceived(Visitor visitor) {
        return List.of(visitor.getState().equals(STATE_EMPTY)
                ? botUtil.initNewMessage(visitor.getChatId(), TEXT_UNREGISTERED_USER_DATA)
                : botUtil.initNewMessage(visitor.getChatId(),
                visitor.toString(),
                keyboardCreator.getRegisteredUserShowDataButtons()));
    }

    private List<SendMessage> commandOrderTicketReceived(Visitor visitor) {
        if (visitor.getState().equals(STATE_REGISTERED)) {
            return botUtil.showUpcomingPerformances(visitor);
        } else {
            visitorService.changeState(visitor, STATE_REGISTRATION_STARTED);
            return List.of(botUtil.initNewMessage(visitor.getChatId(), TEXT_REGISTRATION_PHONE_NUMBER));
        }
    }

    private List<SendMessage> commandShowMyTicketsReceived(Visitor visitor) {
        List<SendMessage> answers = new ArrayList<>();
        List<Ticket> tickets = ticketService.findAllByVisitorId(visitor.getId());
        String chatId = visitor.getChatId();

        for (Ticket ticket : tickets) {
            Performance performance = performanceService.findById(ticket.getPerformanceId());
            answers.add(botUtil.initNewMessage(chatId,
                    answerTextMaker.getOrderedTicketDescription(ticket, performance),
                    keyboardCreator.getOneButtonKeyBoard(buttonCreator.getTicketButton(ticket.getId()))));
        }

        return answers;
    }

    private List<SendMessage> commandOperatorReceived(Visitor visitor) {
        return null;
    }

    private List<SendMessage> handleUserInput(Visitor visitor, String text) {
        return switch (visitor.getState()) {
            case STATE_REGISTRATION_STARTED -> handlePhoneNumberInput(visitor, text);
            case STATE_PHONE_NUMBER_ENTERED -> handleFirstNameInput(visitor, text);
            case STATE_FIRST_NAME_ENTERED -> handleLastNameInput(visitor, text);
            default -> unSupportedCommandReceived(visitor.getChatId());
        };
    }

    private List<SendMessage> unSupportedCommandReceived(String chatId) {
        return List.of(botUtil.initNewMessage(chatId, TEXT_UNSUPPORTED_ACTION));
    }

    private List<SendMessage> handlePhoneNumberInput(Visitor visitor, String phoneNumber) {
        if (botUtil.isPhoneNumberInvalid(phoneNumber)) {
            return List.of(botUtil.initNewMessage(visitor.getChatId(), TEXT_PHONE_NUMBER_NOT_VALID));
        }
        visitorService.changeState(visitor, STATE_PHONE_NUMBER_ENTERED, phoneNumber);

        String text = answerTextMaker.phoneNumberEntered(phoneNumber);
        return List.of(botUtil.initNewMessage(visitor.getChatId(), text));
    }

    private List<SendMessage> handleFirstNameInput(Visitor visitor, String name) {
        if (botUtil.isNameInvalid(name)) {
            return List.of(botUtil.initNewMessage(visitor.getChatId(), TEXT_NAME_NOT_VALID));
        }
        visitorService.changeState(visitor, STATE_FIRST_NAME_ENTERED, name);

        String text = answerTextMaker.firstNameEntered(name);
        return List.of(botUtil.initNewMessage(visitor.getChatId(), text));
    }

    private List<SendMessage> handleLastNameInput(Visitor visitor, String name) {
        if (botUtil.isNameInvalid(name)) {
            return List.of(botUtil.initNewMessage(visitor.getChatId(), TEXT_NAME_NOT_VALID));
        }
        visitorService.changeState(visitor, STATE_REGISTERED, name);

        List<SendMessage> answers = new ArrayList<>();

        String firstMessageText = answerTextMaker.lastNameEntered(name);
        SendMessage firstMessage = botUtil.initNewMessage(visitor.getChatId(), firstMessageText);
        answers.add(firstMessage);

        Optional<LocalDate> optionalPerformanceDate = performanceService.getNextPerformanceDate(LocalDate.now());
        if (optionalPerformanceDate.isPresent()) {
            LocalDate performanceDate = optionalPerformanceDate.get();
            String secondMessageText = TEXT_CHOSE_PERFORMANCE + performanceDate.format(PERFORMANCE_DATE_FORMAT);
            List<List<InlineKeyboardButton>> keyboard = keyboardCreator.getPerformanceKeyboard(performanceDate);

            answers.add(botUtil.initNewMessage(visitor.getChatId(), secondMessageText, keyboard));
        } else {
            firstMessage.setText(firstMessage.getText() + TEXT_NO_UPCOMING_PERFORMANCES);
        }

        return answers;
    }
}
