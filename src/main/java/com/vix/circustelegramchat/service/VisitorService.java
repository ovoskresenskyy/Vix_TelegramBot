package com.vix.circustelegramchat.service;

import com.vix.circustelegramchat.bot.Constants;
import com.vix.circustelegramchat.model.Visitor;
import com.vix.circustelegramchat.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VisitorService implements Constants {

    private final VisitorRepository visitorRepository;

    @Autowired
    public VisitorService(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    public Visitor findByChatId(String chatId) {
        return visitorRepository.findByChatId(chatId).orElseGet(() -> Visitor.builder()
                .chatId(chatId)
                .state(STATE_EMPTY)
                .build());
    }

    public void save(Visitor visitor) {
        visitorRepository.save(visitor);
    }

    public void changeState(Visitor visitor, String state, String text) {
        switch (state) {
            case STATE_PHONE_NUMBER_ENTERED -> visitor.setPhoneNumber(text);
            case STATE_FIRST_NAME_ENTERED -> visitor.setFirstName(text);
            case STATE_REGISTERED -> visitor.setLastName(text);
        }

        visitor.setState(state);
        save(visitor);
    }

    public void changeState(Visitor visitor, String state) {
        changeState(visitor, state, "");
    }
}
