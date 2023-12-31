package com.vix.circustelegramchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This class is an entry point of Telegram Bot application.
 * This bot is responsible for storing info about visitors, showing available performances,
 * carrying user through ticket ordering, saving and sending PDF tickets to user.
 */
@SpringBootApplication
public class VixChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(VixChatApplication.class, args);
    }

}
