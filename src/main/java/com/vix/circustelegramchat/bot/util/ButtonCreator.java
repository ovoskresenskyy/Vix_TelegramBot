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

    public InlineKeyboardButton getMainMenuButton(){
        return getButton(BUTTON_MAIN_MENU, CBD_MAIN_MENU);
    }

    public InlineKeyboardButton getOrderTicketButton(){
        return getButton(BUTTON_ORDER_TICKET, CBD_ORDER_TICKET);
    }

    public InlineKeyboardButton getShowMyTicketsButton(){
        return getButton(BUTTON_SHOW_MY_TICKETS, CBD_SHOW_MY_TICKETS);
    }

    public InlineKeyboardButton getShowMyDataButton(){
        return getButton(BUTTON_SHOW_MY_DATA, CBD_SHOW_MY_DATA);
    }

    public InlineKeyboardButton getChangeMyDataButton(){
        return getButton(BUTTON_CHANGE_MY_DATA, CBD_CHANGE_MY_DATA);
    }

    public InlineKeyboardButton getChatWithOperatorButton() {
        return getButton(BUTTON_CHAT_WITH_OPERATOR, CBD_CHAT_WITH_OPERATOR);
    }

    public InlineKeyboardButton getBackToPerformancesButton(){
        return getButton(BUTTON_BACK_TO_PERFORMANCES, CBD_ORDER_TICKET);
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
