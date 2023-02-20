package com.suttori.demobottty3.services;

import com.suttori.demobottty3.telegram.TelegramSender;
import com.suttori.demobottty3.util.Constants;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageService {

    private final static int MAX_TELEGRAM_MESSAGE = 4000;
    TelegramSender telegramSender;

    @Autowired
    public MessageService(TelegramSender telegramSender) {
        this.telegramSender = telegramSender;
    }

    public static List<SendMessage> createSendMessages(String text, String chatId) {
        final var messages = new ArrayList<SendMessage>();
        for (int i = 0; i < text.length(); i += MAX_TELEGRAM_MESSAGE) {
            messages.add(
                    createSendMessageWithMaxLength(
                            text.substring(i, Math.min(text.length(), i + MAX_TELEGRAM_MESSAGE)), chatId
                    )
            );
        }
        return messages;
    }

    public static SendMessage createSendMessageWithMaxLength(String text, String chatId) {
        return createSendMessageWithMaxLength(text, chatId, ParseMode.HTML);
    }

    public static SendMessage createSendMessageWithMaxLength(String text, String chatId, String parseMode) {
        return SendMessage
                .builder()
                .chatId(chatId)
                .text(substringToTelegramLength(EmojiParser.parseToUnicode(text)))
                .disableWebPagePreview(true)
                .parseMode(parseMode)
                .build();
    }



    private static String substringToTelegramLength(String s) {
        return s.substring(0, Math.min(s.length(), MAX_TELEGRAM_MESSAGE));
    }



    public void start(Message message) {
        var sm = SendMessage.builder()
                .text(Constants.START_MESSAGE)
                .chatId(message.getChatId())
                .build();
        telegramSender.send(sm);

    }

    public void add(Message message) {
        var sm = SendMessage.builder()
                .text(Constants.ADD_CHANNEL)
                .chatId(message.getChatId())
                .build();
        telegramSender.send(sm);
    }

    public SendMessage help(Message message) {
        return SendMessage.builder()
                .text("Здесь будет дохуя большая инструкция как пользоваться ботом")
                .chatId(message.getChatId())
                .build();
    }

    public SendMessage messageCreatePost(Message message) {
        return SendMessage.builder()
                .text("Отправьте боту то, что хотите опубликовать.")
                .chatId(message.getChatId())
                .build();
    }

    public SendMessage messageHelp(Message message) {
        return SendMessage.builder()
                .text("Хули у тебя не работает? Ну напиши мне сюда @blya_chort, посмотрим что ты наклацал")
                .chatId(message.getChatId())
                .build();
    }

    public SendMessage setSettings(Message message) {
        return SendMessage.builder()
                .text("Потом доделаю")
                .chatId(message.getChatId())
                .build();
    }
}
