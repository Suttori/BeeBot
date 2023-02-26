package com.suttori.demobottty3.handler;

import com.suttori.demobottty3.entity.Channel;
import com.suttori.demobottty3.services.*;
import com.suttori.demobottty3.telegram.TelegramSender;
import com.suttori.demobottty3.util.Constants;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageHandler implements Handler<Update> {

    ChannelService channelService;
    TelegramSender telegramSender;
    PostService postService;
    PostServiceText postServiceText;
    ButtonService buttonService;
    UserService userService;


    public MessageHandler(ChannelService channelService, TelegramSender telegramSender, PostService postService,
                          PostServiceText postServiceText, ButtonService buttonService, UserService userService) {
        this.channelService = channelService;
        this.telegramSender = telegramSender;
        this.postService = postService;
        this.postServiceText = postServiceText;
        this.buttonService = buttonService;
        this.userService = userService;
    }

    @Override
    public void choose(Update update) {

        Message message = update.getMessage();

        if (message.hasText()) {
            switch (message.getText()) {
                case "/start":
                    buttonService.generateButton(update);
                    if (!userService.isUserRegister(message)) {
                        userService.saveUser(message);
                    } else if(channelService.isUserHaveChannel(message)) {
                        userService.setPosition(message, "DEFAULT_POSITION");
                    } else {
                        userService.setPosition(message, "START_BOT");
                    }
                    telegramSender.send(MessageService.createSendMessageWithMaxLength(Constants.START_MESSAGE, String.valueOf(message.getChatId())));
                    return;
                case "/add":
                    userService.setPosition(message, "ADD_CHANNEL");
                    telegramSender.send(MessageService.createSendMessageWithMaxLength(Constants.ADD_CHANNEL, String.valueOf(message.getChatId())));
                    return;
                case "/help":
                    telegramSender.send(MessageService.createSendMessageWithMaxLength(Constants.HELP, String.valueOf(message.getChatId())));
                    return;
                case "Создать пост":
                    userService.setPosition(message, "CREATE_POST");
                    Channel channel = channelService.chooseChannel(message);
                    postService.setChannel(channel);
                    postServiceText.setChannel(channel);
                    return;
                case "Отложенные":
                    return;
                case "Обратная связь":
                    telegramSender.send(MessageService.createSendMessageWithMaxLength(Constants.CONTACT, String.valueOf(message.getChatId())));
                    return;
                case "Настройки":
                    telegramSender.send(MessageService.createSendMessageWithMaxLength(Constants.SET_SETTINGS, String.valueOf(message.getChatId())));
                    return;
            }
        }

        if (message.getForwardFromChat() != null && userService.getUser(message).getPosition().equals("ADD_CHANNEL")) {
            channelService.addChannel(update);
            return;
        }

        if (userService.getUser(message).getPosition().equals("CREATE_POST")) {
            if (message.hasText()) {
                postServiceText.createPost(message);
                return;
            }
            postService.createPost(message);
        }


        if (userService.getUser(message).getPosition().equals("CHANGE_TEXT")) {
            if (postServiceText.getSendMessage() != null) {
                postServiceText.addText(message);
                return;
            }
            postService.addText(message);
            return;
        }
    }
}
