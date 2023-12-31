package com.vix.circustelegramchat.bot.util;

import com.vix.circustelegramchat.bot.Constants;
import com.vix.circustelegramchat.model.Performance;
import com.vix.circustelegramchat.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This is the util class to make easier create the buttons and make this process more readable.
 * Using this class we can get preinstalled buttons or create the custom.
 */
@Component
@RequiredArgsConstructor
public class ButtonCreator implements Constants {

    private final PerformanceService performanceService;

    /**
     * This method is creates and fills the new InlineKeyboardButton.
     *
     * @param text         - Text to be placed on the button
     * @param callBackData - Call back data to be returned after pressing the button
     * @return - New InlineKeyboardButton
     */
    public InlineKeyboardButton getButton(String text, String callBackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callBackData);
        return button;
    }

    /**
     * This method is creates and returns the new InlineKeyboardButton
     * for "Change my data" action
     *
     * @return - New InlineKeyboardButton "Change my data"
     */
    public InlineKeyboardButton getChangeMyDataButton() {
        return getButton(BUTTON_CHANGE_MY_DATA, CBD_CHANGE_MY_DATA);
    }

    /**
     * This method is creates and returns the new InlineKeyboardButton
     * for "Change first name" action
     *
     * @return - New InlineKeyboardButton "Change first name"
     */
    public InlineKeyboardButton getChangeFirstNameButton() {
        return getButton(BUTTON_CHANGE_FIRST_NAME, CBD_CHANGE_FIRST_NAME);
    }

    /**
     * This method is creates and returns the new InlineKeyboardButton
     * for "Change last name" action
     *
     * @return - New InlineKeyboardButton "Change last name"
     */
    public InlineKeyboardButton getChangeLastNameButton() {
        return getButton(BUTTON_CHANGE_LAST_NAME, CBD_CHANGE_LAST_NAME);
    }

    /**
     * This method is creates and returns the new InlineKeyboardButton
     * for "Change phone number" action
     *
     * @return - New InlineKeyboardButton "Change phone number"
     */
    public InlineKeyboardButton getChangePhoneNumberButton() {
        return getButton(BUTTON_CHANGE_PHONE_NUMBER, CBD_CHANGE_PHONE_NUMBER);
    }

    /**
     * This method is creates and returns the new InlineKeyboardButton
     * for "Back to performances" action
     *
     * @return - New InlineKeyboardButton "Back to performances"
     */
    public InlineKeyboardButton getBackToPerformancesButton() {
        return getButton(BUTTON_BACK_TO_PERFORMANCES, CBD_BACK_TO_PERFORMANCES);
    }

    /**
     * This method is creates and returns the new InlineKeyboardButton
     * for "Accept selected performance" action
     * ID of the selected performance will be the part of returned CBD
     *
     * @param id - ID of the selected performance
     * @return - New InlineKeyboardButton "Accepted performance" with it ID
     */
    public InlineKeyboardButton getPerformanceAcceptButton(int id) {
        return getButton(BUTTON_ACCEPT, CBD_ACCEPTED_PERFORMANCE_ID_ + id);
    }

    /**
     * This method is creates and returns the new InlineKeyboardButton
     * for "Get ticket" action
     * ID of the ticket will be the part of returned CBD
     *
     * @param id - ID of the ticket
     * @return - New InlineKeyboardButton "Get ticket" with it ID
     */
    public InlineKeyboardButton getTicketButton(int id) {
        return getButton(BUTTON_GET_TICKET, CBD_GET_TICKET_ID_ + id);
    }

    /**
     * This method is creates the list of the buttons with performances on the received date
     *
     * @param performanceDate - The date of the performances
     * @return - List of InlineKeyboardButton with the performances
     */
    public List<InlineKeyboardButton> getPerformanceButtons(LocalDate performanceDate) {
        List<InlineKeyboardButton> performancesButtons = new ArrayList<>();
        List<Performance> performances = performanceService.getPerformancesByDate(performanceDate);

        for (Performance performance : performances) {
            String buttonName = "'" + performance.getName() + "'" + " - " + performance.getTime();
            String buttonCBD = CBD_SELECTED_PERFORMANCE_ID_ + performance.getId();
            performancesButtons.add(getButton(buttonName, buttonCBD));
        }

        return performancesButtons;
    }

    /**
     * This method is creates the navigation buttons "<" and ">"
     * to get the user opportunity "move" through all possible dates of the performances
     *
     * @param currentDate - Current date which are the "start point". According to that
     *                    date we will create "< Go back" or "Go next >" buttons
     * @return - List of InlineKeyboardButton with the navigation buttons
     */
    public List<InlineKeyboardButton> getNavigationButtons(LocalDate currentDate) {
        List<InlineKeyboardButton> navigationButtons = new ArrayList<>();

        /* Add "< Go back" button if only previous performances are present */
        Optional<LocalDate> optionalPreviousDate = performanceService.getPreviousPerformanceDate(currentDate);
        if (optionalPreviousDate.isPresent()) {
            String previousDate = CBD_SHOW_PERFORMANCES_DATE_ + optionalPreviousDate.get();
            navigationButtons.add(getButton(BUTTON_SHOW_PREVIOUS_DATE, previousDate));
        }

        /* Add "Go next >" button if only upcoming performances are present */
        Optional<LocalDate> optionalNextDate = performanceService.getNextPerformanceDate(currentDate);
        if (optionalNextDate.isPresent()) {
            String nextDate = CBD_SHOW_PERFORMANCES_DATE_ + optionalNextDate.get();
            navigationButtons.add(getButton(BUTTON_SHOW_NEXT_DATE, nextDate));
        }

        return navigationButtons;
    }
}
