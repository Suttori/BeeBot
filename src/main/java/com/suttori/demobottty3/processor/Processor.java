package com.suttori.demobottty3.processor;


import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;


public interface Processor {

    void executeMessage(Update update);

    void executeCallBackQuery(CallbackQuery callbackQuery);

    void executePost(Update update);

    default void process(Update update) {
        if (update.hasMessage()) {
            executeMessage(update);
        } else if (update.hasCallbackQuery()) {
            executeCallBackQuery(update.getCallbackQuery());
        }

    }
}
