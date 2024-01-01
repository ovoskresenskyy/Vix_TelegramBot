package com.vix.circustelegramchat.bot.util;

import com.vix.circustelegramchat.bot.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.util.List;

/**
 * This is util class works with keyboards creating.
 * Here we can get exist keyboard ar create the new one-button keyboard
 */
@Component
@RequiredArgsConstructor
public class KeyboardCreator implements Constants {

    private final ButtonCreator buttonCreator;

    /**
     * This method creates keyboard to navigate exist performances.
     * Used buttons:
     * - button for each performance for received date
     * - navigation buttons
     *
     * @param performanceDate - The date of the performance to be added on the buttons
     * @return - The list of lists of InlineKeyboardButtons
     */
    public List<List<InlineKeyboardButton>> getPerformanceKeyboard(LocalDate performanceDate) {
        List<List<InlineKeyboardButton>> keyboard = buttonCreator.getPerformanceButtons(performanceDate);
        keyboard.add(buttonCreator.getNavigationButtons(performanceDate));
        return keyboard;
    }

    /**
     * This method creates keyboard of acceptation of the performance.
     * Used buttons:
     * - accept the performance
     * - change data
     * - back to performance to choose another
     *
     * @param performanceId - The ID of performance to be accepted
     * @return - The list of lists of InlineKeyboardButtons
     */
    public List<List<InlineKeyboardButton>> getPerformanceAcceptationKeyboard(int performanceId) {
        InlineKeyboardButton performanceAcceptButton = buttonCreator.getPerformanceAcceptButton(performanceId);
        InlineKeyboardButton changeMyDataButton = buttonCreator.getChangeMyDataButton();
        InlineKeyboardButton backToPerformancesButton = buttonCreator.getBackToPerformancesButton();

        List<InlineKeyboardButton> firstRow = List.of(performanceAcceptButton, changeMyDataButton);
        List<InlineKeyboardButton> secondRow = List.of(backToPerformancesButton);

        return List.of(firstRow, secondRow);
    }

    /**
     * This method is responsible for creating the keyboard with buttons for changing the users data
     * Used buttons:
     * - change first name
     * - change last name
     * - change phone number
     *
     * @return - The list of lists of InlineKeyboardButtons
     */
    public List<List<InlineKeyboardButton>> getChangeDataKeyboard() {
        InlineKeyboardButton changeFirstNameButton = buttonCreator.getChangeFirstNameButton();
        InlineKeyboardButton changeLastNameButton = buttonCreator.getChangeLastNameButton();
        InlineKeyboardButton changePhoneNumberButton = buttonCreator.getChangePhoneNumberButton();

        List<InlineKeyboardButton> firstRow = List.of(changeFirstNameButton, changeLastNameButton);
        List<InlineKeyboardButton> secondRow = List.of(changePhoneNumberButton);

        return List.of(firstRow, secondRow);
    }

    /**
     * This method is responsible for creating simple 1-button keyboard
     *
     * @param button - The button to be set in a keyboard
     * @return - The One-Button keybaord
     */
    public List<List<InlineKeyboardButton>> getOneButtonKeyBoard(InlineKeyboardButton button) {
        return List.of(List.of(button));
    }
}
