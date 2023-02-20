package com.suttori.demobottty3.telegram;

import com.suttori.demobottty3.config.BotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Component
public class TelegramSender extends DefaultAbsSender {
    private static final Logger logger = LoggerFactory.getLogger(TelegramSender.class);
    private final String token;

    protected TelegramSender(BotConfig botConfig, DefaultBotOptions options) {
        super(options);
        this.token = botConfig.getToken();
    }

    public SendMessage send(SendMessage sendMessage) {
        try {
            logger.info("Sending message to " + sendMessage.getChatId());
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error("Error during sending message", e);
        }
        return null;
    }

    public boolean isAdmin(Message message) {
        try {
            List<ChatMember> chatMembers = execute(new GetChatAdministrators(String.valueOf(message.getForwardFromChat().getId())));
            ChatMember user = execute(new GetChatMember(String.valueOf(message.getChatId()), message.getChatId()));
            for (ChatMember member: chatMembers) {
                if (member.getUser().equals(user.getUser())){
                    return true;
                }
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public boolean isBotAdmin(Message message) {
        try {
            List<ChatMember> chatMembers = execute(new GetChatAdministrators(String.valueOf(message.getForwardFromChat().getId())));
            User user = execute(new GetMe());
            System.out.println(user);
            for (ChatMember member: chatMembers) {
                System.out.println(member.getUser());
                if (member.getUser().getId().equals(user.getId())){
                    return true;
                }
            }
        } catch (TelegramApiException e) {
            System.out.println("Бот не является администратором канала");
        }
        return false;
    }

    public Optional<InputStream> downloadFileById(String fileId) {
        try {
            final var file = execute(GetFile.builder().fileId(fileId).build());
            return Optional.of(downloadFileAsStream(file));
        } catch (TelegramApiException e) {
            logger.error("Error during downloading file", e);
            return Optional.empty();
        }
    }

    @Override
    public String getBotToken() {
        return token;
    }




}
