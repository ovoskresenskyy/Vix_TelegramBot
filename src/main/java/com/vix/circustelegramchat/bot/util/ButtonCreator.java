package com.vix.circustelegramchat.bot.util;

import com.vix.circustelegramchat.bot.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
@RequiredArgsConstructor
public class ButtonCreator implements Constants {

    private InlineKeyboardButton getButton(String text, String callBackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callBackData);
        return button;
    }

    public InlineKeyboardButton getChangeMyDataButton(){
        return getButton(BUTTON_CHANGE_MY_DATA, CBD_CHANGE_MY_DATA);
    }

    public InlineKeyboardButton getChangeFirstNameButton(){
        return getButton(BUTTON_CHANGE_FIRST_NAME, CBD_CHANGE_FIRST_NAME);
    }

    public InlineKeyboardButton getChangeLastNameButton(){
        return getButton(BUTTON_CHANGE_LAST_NAME, CBD_CHANGE_LAST_NAME);
    }

    public InlineKeyboardButton getChangePhoneNumberButton(){
        return getButton(BUTTON_CHANGE_PHONE_NUMBER, CBD_CHANGE_PHONE_NUMBER);
    }

    public InlineKeyboardButton getBackToPerformancesButton(){
        return getButton(BUTTON_BACK_TO_PERFORMANCES, CBD_BACK_TO_PERFORMANCES);
    }

    public InlineKeyboardButton getPerformanceAcceptButton(int id){
        return getButton(BUTTON_ACCEPT, CBD_ACCEPTED_PERFORMANCE_ID_ + id);
    }

    public InlineKeyboardButton getTicketButton(int id){
        return getButton(BUTTON_GET_TICKET, CBD_GET_TICKET_ID_ + id);
    }

    public InlineKeyboardButton getCustomButton(String text, String callBackData){
        return getButton(text, callBackData);
    }
}
