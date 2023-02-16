package com.suttori.demobottty3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class MessageSender {

    private TelegramBot helloWorldBot;


    public void setHelloWorldBot(TelegramBot helloWorldBot) {
        this.helloWorldBot = helloWorldBot;
    }

    public void sendMessage(SendMessage sendMessage) {
        try {
            helloWorldBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
