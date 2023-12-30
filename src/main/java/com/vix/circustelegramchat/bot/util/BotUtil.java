package com.vix.circustelegramchat.bot.util;

import com.vix.circustelegramchat.bot.Constants;
import com.vix.circustelegramchat.model.Visitor;
import com.vix.circustelegramchat.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BotUtil implements Constants {

    private final PerformanceService performanceService;
    private final AnswerTextMaker answerTextMaker;
    private final KeyboardCreator keyboardCreator;

    public SendMessage initNewMessage(String chatId, String text) {
        return initNewMessage(chatId, text, Collections.emptyList());
    }

    public SendMessage initNewMessage(String chatId,
                                      String text,
                                      Collection<? extends List<InlineKeyboardButton>> keyboard) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(keyboard)
                        .build())
                .build();
    }

    public EditMessageText initNewEditMessageText(Message message, String text) {
        return initNewEditMessageText(message, text, Collections.emptyList());
    }

    public EditMessageText initNewEditMessageText(Message message,
                                                  String text,
                                                  Collection<? extends List<InlineKeyboardButton>> keyboard) {
        return EditMessageText.builder()
                .chatId(message.getChatId())
                .messageId(message.getMessageId())
                .text(text)
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(keyboard)
                        .build())
                .build();
    }

    public LocalDate extractDate(String callBackData) {
        return LocalDate.parse(callBackData.replaceAll(CBD_SHOW_PERFORMANCES_DATE_, ""),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public int extractId(String callBackData) {
        return Integer.parseInt(callBackData
                .replace(CBD_SELECTED_PERFORMANCE_ID_, "")
                .replace(CBD_ACCEPTED_PERFORMANCE_ID_, "")
                .replace(CBD_GET_TICKET_ID_, ""));
    }

    public boolean isPhoneNumberInvalid(String phoneNumber) {
        return !PHONE_NUMBER_FORMAT.matcher(phoneNumber).matches();
    }

    public boolean isNameInvalid(String name) {
        return !NAME_FORMAT.matcher(name).matches();
    }

    public BotCommand getCommand(String command, String description) {
        return BotCommand.builder()
                .command(command)
                .description(description)
                .build();
    }



    public List<SendMessage> showUpcomingPerformances(Visitor visitor) {
        Optional<LocalDate> optionalPerformanceDate = performanceService.getUpcomingPerformanceDate();
        if (optionalPerformanceDate.isPresent()) {
            LocalDate performanceDate = optionalPerformanceDate.get();
            String text = answerTextMaker.navigationButtonPressed(performanceDate);
            List<List<InlineKeyboardButton>> keyboard = keyboardCreator.getPerformanceKeyboard(performanceDate);

            return List.of(initNewMessage(visitor.getChatId(), text, keyboard));
        } else {
            return List.of(initNewMessage(visitor.getChatId(), TEXT_NO_UPCOMING_PERFORMANCES));
        }
    }

}
