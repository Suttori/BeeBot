package com.suttori.demobottty3.services;

import com.suttori.demobottty3.telegram.TelegramSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;

@Component
public class ButtonService {

    TelegramSender telegramSender;

    @Autowired
    public ButtonService(TelegramSender telegramSender) {
        this.telegramSender = telegramSender;
    }

    public void generateButton(Update update) {

        var markup = new ReplyKeyboardMarkup();
        var keyboardRows = new ArrayList<KeyboardRow>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add("Создать пост");
        row1.add("Отложенные");
        row2.add("Обратная связь");
        row2.add("Настройки");
        keyboardRows.add(row1);
        keyboardRows.add(row2);
        markup.setKeyboard(keyboardRows);
        markup.setResizeKeyboard(true);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(" ");
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setReplyMarkup(markup);
        telegramSender.sendReplyKeyboardMarkup(sendMessage);
    }
}
