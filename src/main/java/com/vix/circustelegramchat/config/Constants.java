package com.vix.circustelegramchat.config;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.regex.Pattern;

public interface Constants {

    Pattern PHONE_NUMBER_FORMAT = Pattern.compile("\\d{10}");
    Pattern NAME_FORMAT = Pattern.compile("[A-Z](?=.{1,29}$)[A-Za-z]+( [A-Z][A-Za-z]+)*");

    String STATE_EMPTY = "EMPTY";
    String STATE_REGISTRATION_STARTED = "REGISTRATION_STARTED";
    String STATE_PHONE_NUMBER_ENTERED = "PHONE_NUMBER_ENTERED";

    String STATE_FIRST_NAME_ENTERED = "FIRST_NAME_ENTERED";
    String STATE_LAST_NAME_ENTERED = "LAST_NAME_ENTERED";
    String STATE_REGISTERED = "REGISTERED";

    /**
     * Commands
     */
    String COMMAND_START = "/start";

    /**
     * Call back data
     */
    String CBD_MAIN_MENU = "CBD_MAIN_MENU";
    String CBD_ORDER_TICKET = "CBD_ORDER_TICKET";
    String CBD_SHOW_MY_TICKETS = "CBD_SHOW_MY_TICKETS";
    String CBD_SHOW_MY_DATA = "CBD_SHOW_MY_DATA";
    String CBD_CHANGE_MY_DATA = "CBD_CHANGE_MY_DATA";
    String CBD_CHAT_WITH_OPERATOR = "CBD_CHAT_WITH_OPERATOR";

    String CBD_SELECTED_PERFORMANCE_ID_ = "CBD_SELECTED_PERFORMANCE_ID_";
    String CBD_ACCEPTED_PERFORMANCE_ID_ = "CBD_ACCEPTED_PERFORMANCE_ID_";

    String CBD_GET_TICKET_ID_ = "CBD_GET_TICKET_ID_";
    String CBD_REGISTRATION_INPUT_ACCEPT = "CBD_REGISTRATION_INPUT_ACCEPT";

    String CBD_ORDER_OFFER_YES = "CBD_ORDER_OFFER_YES";
    String CBD_BUTTON_ORDER_OFFER_NO = "CBD_ORDER_OFFER_NO";

    String CBD_SHOW_PERFORMANCES_DATE_ = "CBD_SHOW_PERFORMANCES_";

    /**
     * Buttons text
     */
    String BUTTON_MAIN_MENU = "Back to main menu";
    String BUTTON_ORDER_TICKET = "Order new ticket";
    String BUTTON_SHOW_MY_TICKETS = "Show my tickets";
    String BUTTON_SHOW_MY_DATA = "Show my data";
    String BUTTON_CHANGE_MY_DATA = "Change my data";
    String BUTTON_CHAT_WITH_OPERATOR = "Chat with operator";

    String BUTTON_ACCEPT = "Accept";
    String BUTTON_GET_TICKET = "Get ticket";
    String BUTTON_BACK_TO_PERFORMANCES = "Back to performances";

    String BUTTON_SHOW_PREVIOUS_DATE = "< Show previous date";
    String BUTTON_SHOW_NEXT_DATE = "Show next date >";

    String BUTTON_YES = "Yes";
    String BUTTON_NO = "No";

    String BUTTON_CONFIRM = "Confirm";

    /**
     * Texts
     */
    String TEXT_WELCOME = "Welcome to our chat-bot.\nHere you can order tickets for our performances.";

    String TEXT_UNREGISTERED_USER_DATA = "We currently have no information about you."
            + "\nWe will save your data after ordering tickets.";

    String TEXT_NO_UPCOMING_PERFORMANCES = "\nSorry, there are no upcoming performances.\nCome back later.";
    String TEXT_CHOSE_PERFORMANCE = "\n\nPlease chose the performance.\nDate: ";


    String TEXT_REGISTRATION_PHONE_NUMBER = "To get started you have to register" +
            "\nEnter please your phone number in XXXYYYYYYY format.";
    String TEXT_PHONE_NUMBER_NOT_VALID = "The number does not match the format. Please try again.";
    String TEXT_NAME_NOT_VALID = "Entered name is not valid. Please use only latin characters, no special characters.";

    String TEXT_REGISTRATION_LAST_NAME = "Now please enter yor last name.";
    String TEXT_REGISTRATION_PHONE_NUM = "Please enter your phone number in XXXYYYYYYY format.";
    String TEXT_REGISTRATION_END = "You have successfully registered.\nNow you can proceed to purchasing tickets.";


    String TEXT_UNSUPPORTED_ACTION = "Sorry, can't handle it.";

    /**
     * Used to show date of performance in readable format
     */
    DateTimeFormatter PERFORMANCE_DATE_FORMAT = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);

}
