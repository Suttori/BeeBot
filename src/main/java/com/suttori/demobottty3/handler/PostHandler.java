package com.suttori.demobottty3.handler;


import com.suttori.demobottty3.services.ChannelService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class PostHandler implements Handler<Update>{

    MessageHandler messageHandler;
    ChannelService channelService;

    public PostHandler(MessageHandler messageHandler, ChannelService channelService) {
        this.messageHandler = messageHandler;
        this.channelService = channelService;
    }

    @Override
    public void choose(Update update) {



    }
}
