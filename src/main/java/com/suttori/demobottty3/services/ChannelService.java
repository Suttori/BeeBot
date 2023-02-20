package com.suttori.demobottty3.services;

import com.suttori.demobottty3.dao.ChannelRepository;
import com.suttori.demobottty3.entity.Channel;
import com.suttori.demobottty3.telegram.TelegramSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Service
public class ChannelService {

    TelegramSender telegramSender;

    @Autowired
    public ChannelService(TelegramSender telegramSender) {
        this.telegramSender = telegramSender;
    }

    @Autowired
    private ChannelRepository channelRepository;

    public void addChannel(Update update) {
        String errorMessage;
        String SuccessMessage = "Канал успешно добавлен";
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
        channelRepository.save(channel);
        SendMessage sendMessage = SendMessage.builder()
                .text(SuccessMessage)
                .chatId(message.getChatId())
                .build();
        telegramSender.send(sendMessage);
    }
}
