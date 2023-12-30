package com.vix.circustelegramchat.bot.util;

import com.vix.circustelegramchat.bot.Constants;
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

@Component
@RequiredArgsConstructor
public class BotUtil implements Constants {

    private final List<BotCommand> supportedCommands = List.of(
            getCommand(COMMAND_START, COMMAND_DESCRIPTION_START),
            getCommand(COMMAND_SHOW_MY_DATA, COMMAND_DESCRIPTION_SHOW_MY_DATA),
            getCommand(COMMAND_ORDER_TICKET, COMMAND_DESCRIPTION_ORDER_TICKET),
            getCommand(COMMAND_SHOW_MY_TICKETS, COMMAND_DESCRIPTION_SHOW_MY_TICKETS),
            getCommand(COMMAND_OPERATOR, COMMAND_DESCRIPTION_OPERATOR));

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

    public List<BotCommand> getSupportedCommands() {
        return supportedCommands;
    }
}
