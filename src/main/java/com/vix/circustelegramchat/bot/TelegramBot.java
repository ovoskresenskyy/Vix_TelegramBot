package com.vix.circustelegramchat.bot;

import com.vix.circustelegramchat.bot.handler.CallBackDataHandler;
import com.vix.circustelegramchat.bot.handler.DocumentSender;
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

@Service
@Slf4j
public class TelegramBot extends TelegramLongPollingBot implements Constants {

    private final BotUtil botUtil;
    private final VisitorService visitorService;
    private final TextHandler textHandler;
    private final CallBackDataHandler callBackDataHandler;
    private final DocumentSender documentSender;

    @Getter
    @Value("${bot.name}")
    private String botUsername;

    public TelegramBot(@Value("${bot.token}") String botToken,
                       BotUtil botUtil,
                       VisitorService visitorService,
                       TextHandler textHandler,
                       CallBackDataHandler callBackDataHandler, DocumentSender documentSender) {
        super(botToken);
        this.botUtil = botUtil;
        this.visitorService = visitorService;
        this.textHandler = textHandler;
        this.callBackDataHandler = callBackDataHandler;
        this.documentSender = documentSender;

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

    private void handleReceivedText(Update update){
        String text = update.getMessage().getText();
        String chatId = String.valueOf(update.getMessage().getChatId());
        Visitor visitor = visitorService.findByChatId(chatId);

        sendMessage(textHandler.handle(visitor, text));
    }

    private void handleReceivedCallBackQuery(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String callBackData = update.getCallbackQuery().getData();
        String chatId = String.valueOf(message.getChatId());
        Visitor visitor = visitorService.findByChatId(chatId);

        if (callBackData.contains(CBD_GET_TICKET_ID_)) {
            sendMessage(documentSender.handle(visitor, callBackData));
        } else {
            sendMessage(callBackDataHandler.handle(visitor, message, callBackData));
        }
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    private void sendMessage(List<SendMessage> messages) {
        for (SendMessage message : messages) {
            sendMessage(message);
        }
    }

    private void sendMessage(EditMessageText message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    private void sendMessage(SendDocument document) {
        try {
            execute(document);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    private void setBotCommands() {
        try {
            this.execute(new SetMyCommands(botUtil.getSupportedCommands(), new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bots command list: " + e.getMessage());
        }
    }

}
