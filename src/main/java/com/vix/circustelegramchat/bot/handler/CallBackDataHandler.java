package com.vix.circustelegramchat.bot.handler;

import com.vix.circustelegramchat.bot.util.AnswerTextMaker;
import com.vix.circustelegramchat.bot.util.BotUtil;
import com.vix.circustelegramchat.bot.util.KeyboardCreator;
import com.vix.circustelegramchat.config.Constants;
import com.vix.circustelegramchat.model.Customer;
import com.vix.circustelegramchat.model.Performance;
import com.vix.circustelegramchat.model.Ticket;
import com.vix.circustelegramchat.service.CustomerService;
import com.vix.circustelegramchat.service.PerformanceService;
import com.vix.circustelegramchat.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CallBackDataHandler implements Constants {

    private final BotUtil botUtil;
    private final KeyboardCreator keyboardCreator;
    private final AnswerTextMaker answerTextMaker;
    private final CustomerService customerService;
    private final PerformanceService performanceService;
    private final TicketService ticketService;

    public EditMessageText handle(Customer customer, Message message, String callBackData) {
        return switch (callBackData) {
            case CBD_MAIN_MENU -> backToMainMenuPressed(message);
            case CBD_ORDER_TICKET -> orderTicketsPressed(customer, message);
            case CBD_SHOW_MY_TICKETS -> showMyTicketsPressed(message);
            case CBD_SHOW_MY_DATA -> showMyDataPressed(customer, message);
            default -> customButtonPressed(customer, message, callBackData);
        };
    }

    private EditMessageText backToMainMenuPressed(Message message) {
        return botUtil.initNewEditMessageText(message, TEXT_WELCOME, keyboardCreator.getMainMenuButtons());
    }

    private EditMessageText customButtonPressed(Customer customer, Message message, String callBackData) {
        if (callBackData.contains(CBD_SHOW_PERFORMANCES_DATE_)) {
            return navigationButtonPressed(message, callBackData);
        } else if (callBackData.contains(CBD_SELECTED_PERFORMANCE_ID_)) {
            return performanceSelected(customer, message, callBackData);
        } else if (callBackData.contains(CBD_ACCEPTED_PERFORMANCE_ID_)) {
            return performanceAccepted(customer, message, callBackData);
        } else {
            return unSupportedButtonPressed(message);
        }
    }

    private EditMessageText performanceAccepted(Customer customer, Message message, String callBackData) {
        Ticket ticket = ticketService.save(Ticket.builder()
                .customerId(customer.getId())
                .performanceId(botUtil.extractId(callBackData))
                .build());
        String text = answerTextMaker.ticketOrdered();
        List<List<InlineKeyboardButton>> keyboard = keyboardCreator.getPerformanceAcceptedButtons(ticket.getId());

        return botUtil.initNewEditMessageText(message, text, keyboard);
    }

    private EditMessageText performanceSelected(Customer customer, Message message, String callBackData) {
        int performanceId = botUtil.extractId(callBackData);
        Performance performance = performanceService.findById(performanceId);
        String text = answerTextMaker.performanceSelected(customer, performance);
        List<List<InlineKeyboardButton>> keyboard = keyboardCreator.getPerformanceAcceptationButtons(performanceId);

        return botUtil.initNewEditMessageText(message, text, keyboard);
    }

    private EditMessageText navigationButtonPressed(Message message, String callBackData) {
        LocalDate performanceDate = botUtil.extractDate(callBackData);
        String text = answerTextMaker.navigationButtonPressed(performanceDate);
        List<List<InlineKeyboardButton>> keyboard = keyboardCreator.getPerformanceKeyboard(performanceDate);

        return botUtil.initNewEditMessageText(message, text, keyboard);
    }

    private EditMessageText unSupportedButtonPressed(Message message) {
        return EditMessageText.builder()
                .chatId(message.getChatId())
                .messageId(message.getMessageId())
                .text(TEXT_UNSUPPORTED_ACTION)
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(keyboardCreator.getMainMenuButtons())
                        .build())
                .build();
    }

    private EditMessageText orderTicketsPressed(Customer customer, Message message) {
        if (customer.getState().equals(STATE_REGISTERED)) {
            return showUpcomingPerformances(message);
        }
        customerService.changeState(customer, STATE_REGISTRATION_STARTED);
        return botUtil.initNewEditMessageText(message, TEXT_REGISTRATION_PHONE_NUMBER);
    }

    private EditMessageText showUpcomingPerformances(Message message) {
        Optional<LocalDate> optionalPerformanceDate = performanceService.getUpcomingPerformanceDate();
        if (optionalPerformanceDate.isPresent()) {
            LocalDate performanceDate = optionalPerformanceDate.get();
            String text = answerTextMaker.navigationButtonPressed(performanceDate);
            List<List<InlineKeyboardButton>> keyboard = keyboardCreator.getPerformanceKeyboard(performanceDate);

            return botUtil.initNewEditMessageText(message, text, keyboard);
        } else {
            return botUtil.initNewEditMessageText(message, TEXT_NO_UPCOMING_PERFORMANCES);
        }
    }

    private EditMessageText showMyDataPressed(Customer customer, Message message) {
        if (customer.getState().equals(STATE_EMPTY)) {
            return botUtil.initNewEditMessageText(message,
                    TEXT_UNREGISTERED_USER_DATA,
                    keyboardCreator.getBackToMainMenuKeyboard());
        } else {
            return botUtil.initNewEditMessageText(message,
                    customer.toString(),
                    keyboardCreator.getRegisteredUserShowDataButtons());
        }
    }

    private EditMessageText showMyTicketsPressed(Message message) {
        return botUtil.initNewEditMessageText(message, "show my tickets");
    }

}
