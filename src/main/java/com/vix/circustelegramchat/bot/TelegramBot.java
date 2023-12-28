package com.vix.circustelegramchat.bot;

import com.vix.circustelegramchat.bot.handler.CallBackDataHandler;
import com.vix.circustelegramchat.bot.handler.DocumentSender;
import com.vix.circustelegramchat.bot.handler.TextHandler;
import com.vix.circustelegramchat.config.Constants;
import com.vix.circustelegramchat.model.Customer;
import com.vix.circustelegramchat.service.CustomerService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Service
@Slf4j
public class TelegramBot extends TelegramLongPollingBot implements Constants {

    private final CustomerService customerService;
    private final TextHandler textHandler;
    private final CallBackDataHandler callBackDataHandler;
    private final DocumentSender documentSender;

    @Getter
    @Value("${bot.name}")
    private String botUsername;

    public TelegramBot(@Value("${bot.token}") String botToken,
                       CustomerService customerService,
                       TextHandler textHandler,
                       CallBackDataHandler callBackDataHandler, DocumentSender documentSender) {
        super(botToken);
        this.customerService = customerService;
        this.textHandler = textHandler;
        this.callBackDataHandler = callBackDataHandler;
        this.documentSender = documentSender;
    }

    private boolean isMessageWithText(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (isMessageWithText(update)) {
            String text = update.getMessage().getText();
            String chatId = String.valueOf(update.getMessage().getChatId());
            Customer customer = customerService.findByChatId(chatId);

            sendMessage(textHandler.handle(customer, text));
        } else if (update.hasCallbackQuery()) {
            Message message = update.getCallbackQuery().getMessage();
            String callBackData = update.getCallbackQuery().getData();
            String chatId = String.valueOf(message.getChatId());
            Customer customer = customerService.findByChatId(chatId);

            if (callBackData.contains(CBD_GET_TICKET_ID_)) {
                sendMessage(documentSender.handle(customer, callBackData));
            } else {
                sendMessage(callBackDataHandler.handle(customer, message, callBackData));
            }
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

//    private void setBotCommands() {
//        try {
//            this.execute(new SetMyCommands(initListOfCommands(), new BotCommandScopeDefault(), null));
//        } catch (TelegramApiException e) {
//            log.error("Error setting bots command list: " + e.getMessage());
//        }
//    }
//
//    private List<BotCommand> initListOfCommands() {
//        List<BotCommand> listOfCommands = new ArrayList<>();
//        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
//        listOfCommands.add(new BotCommand("/my_data", "get your stored data"));
//        listOfCommands.add(new BotCommand("/delete_data", "delete my data"));
//        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
//        listOfCommands.add(new BotCommand("/setting", "set your preferences"));
//        return listOfCommands;
//    }
}
