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
import java.util.regex.Pattern;

/**
 * This util class helps to operator with the TelegramBotApi, and make TelegramBot class
 * clear of util methods.
 */
@Component
@RequiredArgsConstructor
public class BotUtil implements Constants {

    private final List<BotCommand> supportedCommands = List.of(
            getCommand(COMMAND_START, COMMAND_DESCRIPTION_START),
            getCommand(COMMAND_SHOW_MY_DATA, COMMAND_DESCRIPTION_SHOW_MY_DATA),
            getCommand(COMMAND_ORDER_TICKET, COMMAND_DESCRIPTION_ORDER_TICKET),
            getCommand(COMMAND_SHOW_MY_TICKETS, COMMAND_DESCRIPTION_SHOW_MY_TICKETS),
            getCommand(COMMAND_OPERATOR, COMMAND_DESCRIPTION_OPERATOR));

    /**
     * This method is a Getter for preinstalled list of supported commands
     *
     * @return List of supported commands
     */
    public List<BotCommand> getSupportedCommands() {
        return supportedCommands;
    }

    /**
     * This method is responsible for creating new BotCommand according to received command and decription
     *
     * @param command     - Received command
     * @param description - Description of it
     * @return Created BotCommand
     */
    public BotCommand getCommand(String command, String description) {
        return BotCommand.builder()
                .command(command)
                .description(description)
                .build();
    }

    /**
     * This method is responsible for initialize new SendMessage without keyboard
     *
     * @param chatId - ChatId of new SendMessage
     * @param text   - Text of new SendMessage
     * @return The new SendMessage with only text inside
     */
    public SendMessage initNewMessage(String chatId, String text) {
        return initNewMessage(chatId, text, Collections.emptyList());
    }

    /**
     * This method is responsible for initialize new SendMessage with text and keyboard
     *
     * @param chatId   - ChatId of new SendMessage
     * @param text     - Text of new SendMessage
     * @param keyboard - Buttons of Inline keyboard
     * @return The new SendMessage with text and keyboard inside
     */
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

    /**
     * This method is responsible for initialize new EditMessageText without keyboard
     *
     * @param message - Message to be edited
     * @param text    - Text of the edited message
     * @return The new EditMessageText with only text inside
     */
    public EditMessageText initNewEditMessageText(Message message, String text) {
        return initNewEditMessageText(message, text, Collections.emptyList());
    }

    /**
     * This method is responsible for initialize new EditMessageText with text and keyboard
     *
     * @param message  - Message to be edited
     * @param text     - Text of the edited message
     * @param keyboard - Buttons of Inline keyboard
     * @return The new EditMessageText with text and keyboard inside
     */
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

    /**
     * This method is responsible for extracting the date from the Call Back Data after button pressing
     *
     * @param callBackData - Received data after button pressing
     * @return Extracted date
     */
    public LocalDate extractDate(String callBackData) {
        return LocalDate.parse(callBackData.replaceAll(CBD_SHOW_PERFORMANCES_DATE_, ""),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * This method is responsible for extracting the id from the Call Back Data after button pressing
     *
     * @param callBackData - Received id after button pressing
     * @return Extracted id
     */
    public int extractId(String callBackData) {
        return Integer.parseInt(callBackData
                .replace(CBD_SELECTED_PERFORMANCE_ID_, "")
                .replace(CBD_ACCEPTED_PERFORMANCE_ID_, "")
                .replace(CBD_GET_TICKET_ID_, ""));
    }

    /**
     * This method is checks is entered phone number is valid and matches the patter
     *
     * @param phoneNumber - Entered phone number
     * @return - True if it's NOT valid, false if it's valid
     */
    public boolean isPhoneNumberInvalid(String phoneNumber) {
        return !Pattern.compile(PHONE_NUMBER_FORMAT).matcher(phoneNumber).matches();
    }

    /**
     * This method is checks is entered name is valid and matches the patter
     *
     * @param name - Entered name
     * @return - True if it's NOT valid, false if it's valid
     */
    public boolean isNameInvalid(String name) {
        return !Pattern.compile(NAME_FORMAT).matcher(name).matches();
    }
}
