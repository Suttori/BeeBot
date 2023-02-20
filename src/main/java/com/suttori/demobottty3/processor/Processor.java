package com.suttori.demobottty3.processor;


import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;


public interface Processor {

    void executeMessage(Update update);

    void executeCallBackQuery(CallbackQuery callbackQuery);

    void executePost(Update update);

    default SendMessage process(Update update) {
        if (update.hasMessage()) {
            executeMessage(update);
        } else if (update.hasCallbackQuery()) {
            executeCallBackQuery(update.getCallbackQuery());
        }

        return null;
    }
}
