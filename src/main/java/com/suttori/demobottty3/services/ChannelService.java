package com.suttori.demobottty3.services;

import com.suttori.demobottty3.dao.ChannelRepository;
import com.suttori.demobottty3.entity.Channel;
import com.suttori.demobottty3.telegram.TelegramSender;
import com.suttori.demobottty3.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
public class ChannelService {

    TelegramSender telegramSender;


    String errorMessage;
    String successMessage;

    @Autowired
    public ChannelService(TelegramSender telegramSender) {
        this.telegramSender = telegramSender;
    }

    @Autowired
    private ChannelRepository channelRepository;

    public boolean isUserHaveChannel(Message message) {
        return !channelRepository.findChannelByUserId(message.getChatId()).isEmpty();
    }

    public void addChannel(Update update) {
        String errorMessage;
        String successMessage = "Канал успешно добавлен";
        Message message = update.getMessage();

        if (!telegramSender.isBotAdmin(message)) {
            errorMessage = "Бот не является администратором канала";
            SendMessage sendMessage = SendMessage.builder()
                    .text(errorMessage)
                    .chatId(message.getChatId())
                    .build();
            telegramSender.send(sendMessage);
            return;
        }

        if (!telegramSender.isAdmin(message)) {
            errorMessage = "Вы не являетесь администратором канала";
            SendMessage sendMessage = SendMessage.builder()
                    .text(errorMessage)
                    .chatId(message.getChatId())
                    .build();
            telegramSender.send(sendMessage);
            return;
        }

        Channel channel = new Channel();
        channel.setChannelId(message.getForwardFromChat().getId());
        channel.setChannelName(message.getForwardFromChat().getTitle());
        channel.setChannelUsername(message.getForwardFromChat().getUserName());
        channel.setUserId(message.getChatId());
        channel.setUsername(message.getChat().getUserName());
        channelRepository.save(channel);
        SendMessage sendMessage = SendMessage.builder()
                .text(successMessage)
                .chatId(message.getChatId())
                .build();
        telegramSender.send(sendMessage);
    }




    public Channel chooseChannel(Message message) {
        List<Channel> channels = channelRepository.findChannelByUserId(message.getChatId());

        if (channels.isEmpty()) {
            errorMessage = "Вы не добавили ни одного канала в бота. Чтобы Добавить канал нажмите /add и следуйте дальнейшим инструкциям.";
            SendMessage sendMessage = SendMessage.builder()
                    .text(errorMessage)
                    .chatId(message.getChatId())
                    .build();
            telegramSender.send(sendMessage);
        }

        if (channels.size() > 1) {
            successMessage = "Выберите канал, в который хотите опубликовать пост";
            SendMessage sendMessage = SendMessage.builder()
                    .text(successMessage)
                    .chatId(message.getChatId())
                    .build();
            telegramSender.send(sendMessage);
            //TODO
        }

        if (channels.size() == 1) {
            successMessage = Constants.CREATE_POST + channels.get(0).getChannelName();
            SendMessage sendMessage = SendMessage.builder()
                    .text(successMessage)
                    .chatId(message.getChatId())
                    .build();
            telegramSender.send(sendMessage);
            return channels.get(0);
        }
        return null;
    }



}
