package com.suttori.demobottty3.handler;

import com.suttori.demobottty3.entity.FlagController;
import com.suttori.demobottty3.services.*;
import com.suttori.demobottty3.telegram.TelegramSender;
import com.suttori.demobottty3.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageHandler implements Handler<Update> {

    ChannelService channelService;
    TelegramSender telegramSender;
    PostService postService;
    PostServiceText postServiceText;
    ButtonService buttonService;
    public static final FlagController flagController = new FlagController();


    public MessageHandler(ChannelService channelService, TelegramSender telegramSender, PostService postService, PostServiceText postServiceText, ButtonService buttonService) {
        this.channelService = channelService;
        this.telegramSender = telegramSender;
        this.postService = postService;
        this.postServiceText = postServiceText;
        this.buttonService = buttonService;
    }

    @Override
    public void choose(Update update) {

        Message message = update.getMessage();

        if (message.hasText()) {
            switch (message.getText()) {
                case "/start":
                    buttonService.generateButton(update);
                    telegramSender.send(MessageService.createSendMessageWithMaxLength(Constants.START_MESSAGE, String.valueOf(message.getChatId())));
                    return;
                case "/add":
                    flagController.setTakeChannelFlag(true);
                    telegramSender.send(MessageService.createSendMessageWithMaxLength(Constants.ADD_CHANNEL, String.valueOf(message.getChatId())));
                    return;
                case "/help":
                    telegramSender.send(MessageService.createSendMessageWithMaxLength(Constants.HELP, String.valueOf(message.getChatId())));
                    return;
                case "Создать пост":
                    flagController.setNewPost(true);

                    postService.chooseChannel(message);
                    postServiceText.chooseChannel(message);
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

        if (message.getForwardFromChat() != null && flagController.isTakeChannelFlag()) {
            flagController.setTakeChannelFlag(false);
            channelService.addChannel(update);
            return;
        }

//        if (flagController.isAddMedia()) {
//            flagController.setAddMedia(false);
//            postService.addMedia(message);
//            return;
//        }

        if (flagController.isAddText()) {
            flagController.setAddText(false);

            if (postServiceText.getSendMessage() != null) {
                postServiceText.addText(message);
                return;
            }

            postService.addText(message);
            return;
        }

        if (flagController.isNewPost()) {
            if (message.hasText()) {
                postServiceText.createPost(message);
                return;
            }
            postService.createPost(message);
        }
    }
}
