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

@Component
@RequiredArgsConstructor
public class KeyboardCreator implements Constants {

    private final PerformanceService performanceService;
    private final ButtonCreator buttonCreator;

    public List<List<InlineKeyboardButton>> getMainMenuButtons() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(List.of(
                buttonCreator.getOrderTicketButton(),
                buttonCreator.getShowMyTicketsButton()
        ));
        buttons.add(List.of(
                buttonCreator.getShowMyDataButton(),
                buttonCreator.getChatWithOperatorButton()
        ));
        return buttons;
    }

    public List<List<InlineKeyboardButton>> getPerformanceAcceptationButtons(int performanceId) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        buttons.add(List.of(
                buttonCreator.getPerformanceAcceptButton(performanceId),
                buttonCreator.getChangeMyDataButton()));
        buttons.add(List.of(
                buttonCreator.getBackToPerformancesButton(),
                buttonCreator.getMainMenuButton()));

        return buttons;
    }

    public List<List<InlineKeyboardButton>> getPerformanceAcceptedButtons(int ticketId) {
        InlineKeyboardButton getTicketButton = buttonCreator.getTicketButton(ticketId);
        InlineKeyboardButton backToMainMenuButton = buttonCreator.getMainMenuButton();
        return List.of(List.of(getTicketButton, backToMainMenuButton));
    }

    public List<List<InlineKeyboardButton>> getPerformanceKeyboard(LocalDate performanceDate) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<Performance> performances = performanceService.getPerformancesByDate(performanceDate);
        buttons.add(getPerformanceButtons(performances));
        buttons.add(getNavigationButtons(performanceDate));
        buttons.add(getBackToMainMenuButton());

        return buttons;
    }

    private List<InlineKeyboardButton> getPerformanceButtons(List<Performance> performances) {
        List<InlineKeyboardButton> performancesButtons = new ArrayList<>();

        for (Performance performance : performances) {
            String buttonName = "'" + performance.getName() + "'" + " - " + performance.getTime();
            String buttonCBD = CBD_SELECTED_PERFORMANCE_ID_ + performance.getId();
            performancesButtons.add(buttonCreator.getCustomButton(buttonName, buttonCBD));
        }

        return performancesButtons;
    }

    private List<InlineKeyboardButton> getNavigationButtons(LocalDate currentDate) {
        List<InlineKeyboardButton> navigationButtons = new ArrayList<>();

        Optional<LocalDate> optionalPreviousDate = performanceService.getPreviousPerformanceDate(currentDate);
        if (optionalPreviousDate.isPresent()) {
            String previousDate = CBD_SHOW_PERFORMANCES_DATE_ + optionalPreviousDate.get();
            navigationButtons.add(buttonCreator.getCustomButton(BUTTON_SHOW_PREVIOUS_DATE, previousDate));
        }

        Optional<LocalDate> optionalNextDate = performanceService.getNextPerformanceDate(currentDate);
        if (optionalNextDate.isPresent()) {
            String nextDate = CBD_SHOW_PERFORMANCES_DATE_ + optionalNextDate.get();
            navigationButtons.add(buttonCreator.getCustomButton(BUTTON_SHOW_NEXT_DATE, nextDate));
        }

        return navigationButtons;
    }

    public List<List<InlineKeyboardButton>> getRegisteredUserShowDataButtons() {
        return List.of(List.of(
                buttonCreator.getChangeMyDataButton(),
                buttonCreator.getMainMenuButton()));
    }

    public List<InlineKeyboardButton> getBackToMainMenuButton() {
        return List.of(buttonCreator.getMainMenuButton());
    }

    public List<List<InlineKeyboardButton>> getBackToMainMenuKeyboard() {
        return List.of(getBackToMainMenuButton());
    }

}
