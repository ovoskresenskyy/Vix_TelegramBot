package com.vix.circustelegramchat.bot;

import com.vix.circustelegramchat.bot.handler.CallBackDataHandler;
import com.vix.circustelegramchat.bot.handler.TicketSender;
import com.vix.circustelegramchat.bot.handler.TextHandler;
import com.vix.circustelegramchat.bot.util.BotUtil;
import com.vix.circustelegramchat.model.Visitor;
import com.vix.circustelegramchat.service.VisitorService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

/**
 * This is the main class which is handle all received commands and actions.
 * After handling, it can send one or a few new messages,
 * or edit exist one, or send the document (ticket).
 */
@Service
@Slf4j
public class TelegramBot extends TelegramLongPollingBot implements Constants {

    private final BotUtil botUtil;
    private final VisitorService visitorService;
    private final TextHandler textHandler;
    private final CallBackDataHandler callBackDataHandler;
    private final TicketSender ticketSender;

    @Getter
    @Value("${bot.name}")
    private String botUsername;

    public TelegramBot(@Value("${bot.token}") String botToken,
                       BotUtil botUtil,
                       VisitorService visitorService,
                       TextHandler textHandler,
                       CallBackDataHandler callBackDataHandler, TicketSender ticketSender) {
        super(botToken);
        this.botUtil = botUtil;
        this.visitorService = visitorService;
        this.textHandler = textHandler;
        this.callBackDataHandler = callBackDataHandler;
        this.ticketSender = ticketSender;

        setBotCommands();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleReceivedText(update);
        } else if (update.hasCallbackQuery()) {
            handleReceivedCallBackQuery(update);
        }
    }

    /**
     * This method is setting into the bot the list of supported commands with their descriptions.
     */
    private void setBotCommands() {
        try {
            List<BotCommand> supportedCommands = botUtil.getSupportedCommands();
            this.execute(new SetMyCommands(supportedCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bots command list: " + e.getMessage());
        }
    }

    /**
     * This method is responsible for handling all text messages, including inputted commands
     * or any other users inputs.
     *
     * @param update - Received update from Bot API
     */
    private void handleReceivedText(Update update) {
        String text = update.getMessage().getText();
        String chatId = String.valueOf(update.getMessage().getChatId());
        Visitor visitor = visitorService.findByChatId(chatId);

        sendMessage(textHandler.handle(visitor, text));
    }

    /**
     * This method is responsible for handling all Inline buttons presses.
     * <p>
     * In case, when pressed button "Get ticket", it will use document sender,
     * to create and send the ticket.
     * In other cases - use CallBackData handler.
     *
     * @param update - Received update from Bot API
     */
    private void handleReceivedCallBackQuery(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String callBackData = update.getCallbackQuery().getData();
        String chatId = String.valueOf(message.getChatId());
        Visitor visitor = visitorService.findByChatId(chatId);

        if (callBackData.contains(CBD_GET_TICKET_ID_)) {
            int ticketId = botUtil.extractId(callBackData);
            SendDocument ticket = ticketSender.getTicket(visitor, ticketId);

            sendMessage(ticket);
        } else {
            sendMessage(callBackDataHandler.handle(visitor, message, callBackData));
        }
    }

    /**
     * Method for simple sending a message.
     *
     * @param message - New message for the user.
     */
    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending new message: " + e.getMessage());
        }
    }

    /**
     * Method for simple sending a messages.
     *
     * @param messages - List of new messages for the user.
     */
    private void sendMessage(List<SendMessage> messages) {
        for (SendMessage message : messages) {
            sendMessage(message);
        }
    }

    /**
     * Method for simple sending a message.
     *
     * @param message - Edited message for the user.
     */
    private void sendMessage(EditMessageText message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending edited message: " + e.getMessage());
        }
    }

    /**
     * Method for simple sending a document.
     *
     * @param document - Created and stored ticket.
     */
    private void sendMessage(SendDocument document) {
        try {
            execute(document);
        } catch (TelegramApiException e) {
            log.error("Error sending document: " + e.getMessage());
        }
    }
}
