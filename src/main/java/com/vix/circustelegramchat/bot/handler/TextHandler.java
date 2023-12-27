package com.vix.circustelegramchat.bot.handler;

import com.vix.circustelegramchat.bot.util.AnswerTextMaker;
import com.vix.circustelegramchat.bot.util.BotUtil;
import com.vix.circustelegramchat.bot.util.KeyboardCreator;
import com.vix.circustelegramchat.config.Constants;
import com.vix.circustelegramchat.model.Customer;
import com.vix.circustelegramchat.service.CustomerService;
import com.vix.circustelegramchat.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TextHandler implements Constants {

    private final BotUtil botUtil;
    private final KeyboardCreator keyboardCreator;
    private final AnswerTextMaker answerTextMaker;
    private final CustomerService customerService;
    private final PerformanceService performanceService;

    public List<SendMessage> handle(Customer customer, String text) {
        return switch (text) {
            case COMMAND_START -> commandStartReceived(customer);
            default -> handleUserInput(customer, text);
        };
    }

    private List<SendMessage> commandStartReceived(Customer customer) {
        return List.of(botUtil.initNewMessage(customer.getChatId(),
                TEXT_WELCOME,
                keyboardCreator.getMainMenuButtons()));
    }

    private List<SendMessage> handleUserInput(Customer customer, String text) {
        return switch (customer.getState()) {
            case STATE_REGISTRATION_STARTED -> handlePhoneNumberInput(customer, text);
            case STATE_PHONE_NUMBER_ENTERED -> handleFirstNameInput(customer, text);
            case STATE_FIRST_NAME_ENTERED -> handleLastNameInput(customer, text);
            default -> unSupportedCommandReceived(customer.getChatId());
        };
    }

    private List<SendMessage> unSupportedCommandReceived(String chatId) {
        //TODO: add some buttons here
        return List.of(botUtil.initNewMessage(chatId, TEXT_UNSUPPORTED_ACTION));
    }

    private List<SendMessage> handlePhoneNumberInput(Customer customer, String phoneNumber) {
        if (botUtil.isPhoneNumberInvalid(phoneNumber)) {
            return List.of(botUtil.initNewMessage(customer.getChatId(), TEXT_PHONE_NUMBER_NOT_VALID));
        }
        customerService.changeState(customer, STATE_PHONE_NUMBER_ENTERED, phoneNumber);

        String text = answerTextMaker.phoneNumberEntered(phoneNumber);
        return List.of(botUtil.initNewMessage(customer.getChatId(), text));
    }

    private List<SendMessage> handleFirstNameInput(Customer customer, String name) {
        if (botUtil.isNameInvalid(name)) {
            return List.of(botUtil.initNewMessage(customer.getChatId(), TEXT_NAME_NOT_VALID));
        }
        customerService.changeState(customer, STATE_FIRST_NAME_ENTERED, name);

        String text = answerTextMaker.firstNameEntered(name);
        return List.of(botUtil.initNewMessage(customer.getChatId(), text));
    }

    private List<SendMessage> handleLastNameInput(Customer customer, String name) {
        if (botUtil.isNameInvalid(name)) {
            return List.of(botUtil.initNewMessage(customer.getChatId(), TEXT_NAME_NOT_VALID));
        }
        customerService.changeState(customer, STATE_REGISTERED, name);

        List<SendMessage> answers = new ArrayList<>();

        String firstMessageText = answerTextMaker.lastNameEntered(name);
        SendMessage firstMessage = botUtil.initNewMessage(customer.getChatId(), firstMessageText);
        answers.add(firstMessage);

        Optional<LocalDate> optionalPerformanceDate = performanceService.getNextPerformanceDate(LocalDate.now());
        if (optionalPerformanceDate.isPresent()) {
            LocalDate performanceDate = optionalPerformanceDate.get();
            String secondMessageText = TEXT_CHOSE_PERFORMANCE + performanceDate.format(PERFORMANCE_DATE_FORMAT);
            List<List<InlineKeyboardButton>> keyboard = keyboardCreator.getPerformanceKeyboard(performanceDate);

            answers.add(botUtil.initNewMessage(customer.getChatId(), secondMessageText, keyboard));
        } else {
            firstMessage.setText(firstMessage.getText() + TEXT_NO_UPCOMING_PERFORMANCES);
        }

        return answers;
    }
}
