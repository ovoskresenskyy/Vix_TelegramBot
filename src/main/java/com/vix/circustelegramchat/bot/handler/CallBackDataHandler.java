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

    public EditMessageText handle(Visitor visitor, Message message, String callBackData) {
        return switch (callBackData) {
            case CBD_CHANGE_MY_DATA -> changeMyDataButtonPressed(message);
            case CBD_CHANGE_FIRST_NAME -> changeFirstNameButtonPressed(visitor, message);
            case CBD_CHANGE_LAST_NAME -> changeLastNameButtonPressed(visitor, message);
            case CBD_CHANGE_PHONE_NUMBER -> changePhoneNumberButtonPressed(visitor, message);
            default -> customButtonPressed(visitor, message, callBackData);
        };
    }

    private EditMessageText changePhoneNumberButtonPressed(Visitor visitor, Message message) {
        visitorService.updateVisitor(visitor, STATE_PHONE_NUMBER_CHANGING);
        return botUtil.initNewEditMessageText(message, replyUtil.phoneNumberChanging());
    }

    private EditMessageText changeLastNameButtonPressed(Visitor visitor, Message message) {
        visitorService.updateVisitor(visitor, STATE_LAST_NAME_CHANGING);
        return botUtil.initNewEditMessageText(message, replyUtil.lastNameChanging());
    }

    private EditMessageText changeFirstNameButtonPressed(Visitor visitor, Message message) {
        visitorService.updateVisitor(visitor, STATE_FIRST_NAME_CHANGING);
        return botUtil.initNewEditMessageText(message, replyUtil.firstNameChanging());
    }

    private EditMessageText changeMyDataButtonPressed(Message message) {
        return botUtil.initNewEditMessageText(message,
                message.getText(),
                keyboardCreator.getChangeDataKeyboard());
    }

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

    private EditMessageText performanceSelected(Visitor visitor, Message message, String callBackData) {
        int performanceId = botUtil.extractId(callBackData);
        Performance performance = performanceService.findById(performanceId);

        return botUtil.initNewEditMessageText(message,
                replyUtil.performanceSelected(visitor, performance),
                keyboardCreator.getPerformanceAcceptationKeyboard(performanceId));
    }

    private EditMessageText navigationButtonPressed(Message message, String callBackData) {
        LocalDate performanceDate = botUtil.extractDate(callBackData);

        return botUtil.initNewEditMessageText(message,
                replyUtil.chosePerformance(performanceDate),
                keyboardCreator.getPerformanceKeyboard(performanceDate));
    }

    private EditMessageText unSupportedButtonPressed(Message message) {
        return EditMessageText.builder()
                .chatId(message.getChatId())
                .messageId(message.getMessageId())
                .text(replyUtil.unsupportedAction())
                .build();
    }

}
