package com.suttori.demobottty3.services;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageService {

    private final static int MAX_TELEGRAM_MESSAGE = 4000;

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
}
