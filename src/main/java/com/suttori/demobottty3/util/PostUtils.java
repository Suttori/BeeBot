package com.suttori.demobottty3.util;

import com.suttori.demobottty3.telegram.TelegramSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PostUtils {


    TelegramSender telegramSender;

    @Autowired
    public PostUtils(TelegramSender telegramSender) {
        this.telegramSender = telegramSender;
    }


    public void deleteMessage(Message message) {
        String chatId = String.valueOf(message.getChatId());
        Integer messageId = message.getMessageId();
        DeleteMessage deleteMessage = new DeleteMessage(chatId, messageId);
        try {
            telegramSender.execute(deleteMessage);
        } catch (TelegramApiException tae) {
            throw new RuntimeException(tae);
        }
    }


    public void deleteMessageById(String chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage(chatId, messageId);
        try {
            telegramSender.execute(deleteMessage);
        } catch (TelegramApiException tae) {
            throw new RuntimeException(tae);
        }
    }

    public void deleteMessageCallbackQuery(CallbackQuery callbackQuery) {
        String chatId = String.valueOf(callbackQuery.getMessage().getChatId());
        Integer messageId = callbackQuery.getMessage().getMessageId();
        DeleteMessage deleteMessage = new DeleteMessage(chatId, messageId);
        try {
            telegramSender.execute(deleteMessage);
        } catch (TelegramApiException tae) {
            throw new RuntimeException(tae);
        }
    }

    public String getPhotoFieldId(Message message) {
        List<PhotoSize> photos = message.getPhoto();
        return photos.stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElseThrow().getFileId();
    }

    public List<List<InlineKeyboardButton>> createCustomButton(Message message, List<List<InlineKeyboardButton>> keyboard) {
        String buttons = message.getText();

        String[] buttonN = buttons.split("\\n");
        InlineKeyboardButton customButton;
        List<InlineKeyboardButton> rowInLine;
        String[] buttonDelimiter;
        String[] buttonDelimiter1;

        for (String s : buttonN) {
            rowInLine = new ArrayList<>();
            buttonDelimiter = s.split("\\s\\|\\s");
            for (String value : buttonDelimiter) {
                buttonDelimiter1 = value.split("\\s-\\s");
                for (int k = 0; k < buttonDelimiter1.length; k++) {
                    customButton = new InlineKeyboardButton();
                    customButton.setText(buttonDelimiter1[k]);
                    customButton.setUrl(buttonDelimiter1[k + 1]);
                    rowInLine.add(customButton);
                    k++;
                }
            }
            keyboard.add(rowInLine);
        }
        return keyboard;
    }
}
