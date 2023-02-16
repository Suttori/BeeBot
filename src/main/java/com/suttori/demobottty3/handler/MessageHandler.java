package com.suttori.demobottty3.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class MessageHandler implements Handler<Message>{
    @Override
    public void choose(Message message) {

    }
}
