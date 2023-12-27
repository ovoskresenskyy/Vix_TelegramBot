package com.vix.circustelegramchat.bot.handler;

import com.vix.circustelegramchat.config.Constants;
import com.vix.circustelegramchat.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class DocumentSender implements Constants {

    public SendDocument handle(Customer customer, Message message, String callBackData) {
        return new SendDocument();
    }
}
