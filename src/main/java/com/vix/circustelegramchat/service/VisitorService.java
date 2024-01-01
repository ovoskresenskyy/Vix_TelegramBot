package com.vix.circustelegramchat.service;

import com.vix.circustelegramchat.bot.Constants;
import com.vix.circustelegramchat.model.Visitor;
import com.vix.circustelegramchat.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * This Service is responsible for communicate with matched repository.
 * Getting data from repository, preparing and sending to user.
 */
@Service
@RequiredArgsConstructor
public class VisitorService implements Constants {

    private final VisitorRepository visitorRepository;

    /**
     * This method is responsible for a simple saving received visitor
     *
     * @param visitor - The visitor to be saved
     */
    public void save(Visitor visitor) {
        visitorRepository.save(visitor);
    }

    /**
     * This method is responsible for finding saved visitors by theirs chatId
     *
     * @param chatId - ID of the visitor we are looking for
     * @return Found visitor if present, or new created with saved chatId and state if not.
     */
    public Visitor findByChatId(String chatId) {
        return visitorRepository.findByChatId(chatId)
                .orElseGet(() -> Visitor.builder()
                        .chatId(chatId)
                        .state(STATE_EMPTY)
                        .build());
    }
}
