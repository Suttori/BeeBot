package com.suttori.demobottty3.util;

import com.suttori.demobottty3.telegram.TelegramSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
}
