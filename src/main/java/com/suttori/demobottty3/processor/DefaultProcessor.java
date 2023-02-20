package com.suttori.demobottty3.processor;

import com.suttori.demobottty3.handler.CallbackQueryHandler;
import com.suttori.demobottty3.handler.MessageHandler;
import com.suttori.demobottty3.handler.PostHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class DefaultProcessor implements Processor{

    private final CallbackQueryHandler callbackQueryHandler;
    private final MessageHandler messageHandler;
    private final PostHandler postHandler;

    public DefaultProcessor(CallbackQueryHandler callbackQueryHandler, MessageHandler messageHandler, PostHandler postHandler) {
        this.callbackQueryHandler = callbackQueryHandler;
        this.messageHandler = messageHandler;
        this.postHandler = postHandler;
    }

    @Override
    public void executeMessage(Update update) {
        messageHandler.choose(update);
    }

    @Override
    public void executeCallBackQuery(CallbackQuery callbackQuery) {
        callbackQueryHandler.choose(callbackQuery);
    }

    @Override
    public void executePost(Update update) {
        postHandler.choose(update);
    }
}
