package com.suttori.demobottty3.handler;

import com.suttori.demobottty3.services.ChannelService;
import com.suttori.demobottty3.services.MessageService;
import com.suttori.demobottty3.telegram.TelegramSender;
import com.suttori.demobottty3.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageHandler implements Handler<Update> {

    private boolean takeChannelFlag = false;

    ChannelService channelService;
    TelegramSender telegramSender;
    MessageService messageService;

    @Autowired
    public MessageHandler(ChannelService channelService, TelegramSender telegramSender) {
        this.channelService = channelService;
        this.telegramSender = telegramSender;
    }

    @Override
    public void choose(Update update) {
        Message message = update.getMessage();

        if (message.getForwardFromChat() != null && takeChannelFlag) {
            takeChannelFlag = false;
            channelService.addChannel(update);
        }


        if (message.hasText()) {
            switch (message.getText()) {
                case "/start":
                    telegramSender.send(MessageService.createSendMessageWithMaxLength(Constants.START_MESSAGE, String.valueOf(message.getChatId())));
                    break;
                case "/add":
                    takeChannelFlag = true;
                    telegramSender.send(MessageService.createSendMessageWithMaxLength(Constants.ADD_CHANNEL, String.valueOf(message.getChatId())));
                    break;
                case "/help":
                    messageService.help(message);
                    break;
                case "Создать пост":
                    messageService.messageCreatePost(message);
                    break;
                case "Отложенные":
                    break;
                case "Помощь":
                    messageService.messageHelp(message);
                    break;
                case "Настройки":
                    messageService.setSettings(message);
                    break;
            }
        }
    }
}
