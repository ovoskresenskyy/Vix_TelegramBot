package com.vix.circustelegramchat.bot;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * This interface is a holder of different constants for make code more readable and flexible.
 */
public interface Constants {

    /**
     * Visitor states.
     * Used to separate users imputes.
     */

    /* Just created visitor */
    String STATE_EMPTY = "EMPTY";

    /* States of the first data entering */
    String STATE_PHONE_NUMBER_ENTERING = "PHONE_NUMBER_ENTERING";
    String STATE_FIRST_NAME_ENTERING = "FIRST_NAME_ENTERING";
    String STATE_LAST_NAME_ENTERING = "LAST_NAME_ENTERING";

    /* States of data changing */
    String STATE_PHONE_NUMBER_CHANGING = "PHONE_NUMBER_CHANGING";
    String STATE_FIRST_NAME_CHANGING = "FIRST_NAME_CHANGING";
    String STATE_LAST_NAME_CHANGING = "LAST_NAME_CHANGING";

    /* Full registered user*/
    String STATE_REGISTERED = "REGISTERED";

    /**
     * Commands.
     * Used at the bot menu, to get possibility user operate with the bot.
     */
    String COMMAND_START = "/start";
    String COMMAND_SHOW_MY_DATA = "/my_data";
    String COMMAND_ORDER_TICKET = "/order_ticket";
    String COMMAND_SHOW_MY_TICKETS = "/my_tickets";
    String COMMAND_OPERATOR = "/operator";

    /**
     * Commands description.
     * Used to make it more useful for user.
     */
    String COMMAND_DESCRIPTION_START = "Get a welcome message";
    String COMMAND_DESCRIPTION_SHOW_MY_DATA = "Show my data";
    String COMMAND_DESCRIPTION_ORDER_TICKET = "Order new ticket";
    String COMMAND_DESCRIPTION_SHOW_MY_TICKETS = "Show my tickets";
    String COMMAND_DESCRIPTION_OPERATOR = "Chat with operator";

    /**
     * Buttons.
     * These texts will be printed on out buttons
     */
    String BUTTON_CHANGE_MY_DATA = "Change my data";
    String BUTTON_CHANGE_FIRST_NAME = "Change first name";
    String BUTTON_CHANGE_LAST_NAME = "Change last name";
    String BUTTON_CHANGE_PHONE_NUMBER = "Change phone number";

    String BUTTON_CHAT_WITH_OPERATOR = "Chat with operator";

    String BUTTON_ACCEPT = "Accept";
    String BUTTON_GET_TICKET = "Get ticket";
    String BUTTON_BACK_TO_PERFORMANCES = "Back to performances";

    String BUTTON_SHOW_PREVIOUS_DATE = "< Show previous date";
    String BUTTON_SHOW_NEXT_DATE = "Show next date >";

    /**
     * Call back data.
     * This is what button clicks return.
     */
    String CBD_CHANGE_MY_DATA = "CBD_CHANGE_MY_DATA";
    String CBD_CHANGE_FIRST_NAME = "CBD_CHANGE_FIRST_NAME";
    String CBD_CHANGE_LAST_NAME = "CBD_CHANGE_LAST_NAME";
    String CBD_CHANGE_PHONE_NUMBER = "CBD_CHANGE_PHONE_NUMBER";

    String CBD_CHAT_WITH_OPERATOR = "CBD_CHAT_WITH_OPERATOR";

    String CBD_SHOW_PERFORMANCES_DATE_ = "CBD_SHOW_PERFORMANCES_";
    String CBD_BACK_TO_PERFORMANCES = "CBD_BACK_TO_PERFORMANCES";

    String CBD_SELECTED_PERFORMANCE_ID_ = "CBD_SELECTED_PERFORMANCE_ID_";
    String CBD_ACCEPTED_PERFORMANCE_ID_ = "CBD_ACCEPTED_PERFORMANCE_ID_";
    String CBD_GET_TICKET_ID_ = "CBD_GET_TICKET_ID_";

    /**
     * Used to show date of performance in readable format
     */
    DateTimeFormatter PERFORMANCE_DATE_FORMAT = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);

    /**
     * Patterns to check if the users input is correct
     */
    String PHONE_NUMBER_FORMAT = "\\d{10}";
    String NAME_FORMAT = "[A-Z](?=.{1,29}$)[A-Za-z]+( [A-Z][A-Za-z]+)*";
}
