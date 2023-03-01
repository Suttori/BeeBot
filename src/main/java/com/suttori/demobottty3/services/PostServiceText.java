package com.suttori.demobottty3.services;

import com.suttori.demobottty3.dao.ChannelRepository;
import com.suttori.demobottty3.entity.Channel;
import com.suttori.demobottty3.entity.Post;
import com.suttori.demobottty3.entity.enums.Position;
import com.suttori.demobottty3.telegram.TelegramSender;
import com.suttori.demobottty3.util.Constants;
import com.suttori.demobottty3.util.PostUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PostServiceText {

    TelegramSender telegramSender;
    SendMessage sendMessage;
    ChannelService channelService;
    PostUtils postUtils;

    Channel channel;
    private List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    public SendMessage getSendMessage() {
        return sendMessage;
    }

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    public PostServiceText(TelegramSender telegramSender, ChannelService channelService, PostUtils postUtils) {
        this.telegramSender = telegramSender;
        this.channelService = channelService;
        this.postUtils = postUtils;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void publish(CallbackQuery callbackQuery) {
        telegramSender.send(sendMessage);
        sendMessage = null;

        Integer messageId = callbackQuery.getMessage().getMessageId();
        var editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
        editMessageText.setMessageId(messageId);
        editMessageText.setText("Пост успешно был опубликован");

        telegramSender.sendEditMessage(editMessageText);
    }

    public void createMessagePost(Message message) {
        sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(message.getText());
    }

    public void createPost(Message message) {
        createMessagePost(message);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(createButtonPost(keyboard));
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        telegramSender.send(sendMessage);
    }

    public void cancelCreatePost() {
        sendMessage = null;
    }

    public void deletePreviousMessage(CallbackQuery callbackQuery) {
        postUtils.deleteMessageCallbackQuery(callbackQuery);
    }

    public void addText(Message message) {
        if (message.hasText()) {
            sendMessage.setText(message.getText());
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard1 = new ArrayList<>(keyboard);
            inlineKeyboardMarkup.setKeyboard(createButtonPost(keyboard1));
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            telegramSender.send(sendMessage);
            postUtils.deleteMessage(message);
        }
    }

    public void prepareForPost() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        telegramSender.send(sendMessage);
        sendMessage.setChatId(String.valueOf(channel.getChannelId()));
    }

    public void nextButton(CallbackQuery callbackQuery) {
        prepareForPost();
        postUtils.deleteMessageCallbackQuery(callbackQuery);

        SendMessage message = new SendMessage();
        message.setChatId(callbackQuery.getMessage().getChatId());
        message.setText("Пост готов к публикации. Опубликовать сейчас или отложить?");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> rowInLineOne = new ArrayList<>();
        List<InlineKeyboardButton> rowInLineTwo = new ArrayList<>();
        List<InlineKeyboardButton> rowInLineThree = new ArrayList<>();

        var scheduled = new InlineKeyboardButton();
        var plan = new InlineKeyboardButton();
        var autoDelete = new InlineKeyboardButton();
        var cancel = new InlineKeyboardButton();
        var publish = new InlineKeyboardButton();

        scheduled.setText("По расписанию");
        plan.setText("Отложить");
        autoDelete.setText("Таймер автоудаления");
        cancel.setText("Назад");
        publish.setText("Опубликовать");

        scheduled.setCallbackData("scheduled");
        plan.setCallbackData("plan");
        autoDelete.setCallbackData("auto_delete");
        cancel.setCallbackData("cancel_next_button");
        publish.setCallbackData("publish");

        rowInLineOne.add(scheduled);
        rowInLineOne.add(plan);
        rowInLineTwo.add(autoDelete);
        rowInLineThree.add(cancel);
        rowInLineThree.add(publish);

        keyboard.add(rowInLineOne);
        keyboard.add(rowInLineTwo);
        keyboard.add(rowInLineThree);

        inlineKeyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(inlineKeyboardMarkup);

        telegramSender.send(message);

    }


//    public void backButton(CallbackQuery callbackQuery) {
//        deletePreviousMessage(callbackQuery);
//        sendMessage.setChatId(callbackQuery.getMessage().getChatId());
//
//        var editMessageReplyMarkup = new EditMessageReplyMarkup();
//        editMessageReplyMarkup.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
//        editMessageReplyMarkup.setMessageId(sendMessage);
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> keyboard1 = new ArrayList<>(keyboard);
//        inlineKeyboardMarkup.setKeyboard(createButtonPost(keyboard1));
//        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);
//        return editMessageReplyMarkup;
//
//
//
//
//        //createButtonPost()
//        telegramSender.send(sendMessage);
//    }


    public void addCustomButton(Message message) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        keyboard = postUtils.createCustomButton(message, keyboard);
        List<List<InlineKeyboardButton>> keyboard1 = new ArrayList<>(keyboard);
        inlineKeyboardMarkup.setKeyboard(createButtonPost(keyboard1));
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        telegramSender.send(sendMessage);
    }

    public EditMessageReplyMarkup createEditReplyMarkup(CallbackQuery callbackQuery) {
        var editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
        editMessageReplyMarkup.setMessageId(callbackQuery.getMessage().getMessageId());
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard1 = new ArrayList<>(keyboard);
        inlineKeyboardMarkup.setKeyboard(createButtonPost(keyboard1));
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);
        return editMessageReplyMarkup;
    }

    public void notification(CallbackQuery callbackQuery) {
        if (sendMessage.getDisableNotification() == null) {
            sendMessage.disableNotification();
        } else {
            sendMessage.enableNotification();
        }
        telegramSender.sendEditMessageReplyMarkup(createEditReplyMarkup(callbackQuery));
    }

    public void preview(CallbackQuery callbackQuery) {
        if (sendMessage.getDisableWebPagePreview() == null) {
            sendMessage.setDisableWebPagePreview(true);
        } else {
            sendMessage.enableWebPagePreview();
        }
        telegramSender.sendEditMessageReplyMarkup(createEditReplyMarkup(callbackQuery));
    }

    public List<List<InlineKeyboardButton>> createButtonPost(List<List<InlineKeyboardButton>> keyboard) {
        List<InlineKeyboardButton> rowInLineOne = new ArrayList<>();
        List<InlineKeyboardButton> rowInLineTwo = new ArrayList<>();
        List<InlineKeyboardButton> rowInLineThree = new ArrayList<>();
        List<InlineKeyboardButton> rowInLineFour = new ArrayList<>();
        List<InlineKeyboardButton> rowInLineFive = new ArrayList<>();

        var addMediaOrText = new InlineKeyboardButton();
        var addButton = new InlineKeyboardButton();
        var notification = new InlineKeyboardButton();
        var preview = new InlineKeyboardButton();
        var autoCaption = new InlineKeyboardButton();
        var comment = new InlineKeyboardButton();
        var copy = new InlineKeyboardButton();
        var cancel = new InlineKeyboardButton();
        var next = new InlineKeyboardButton();

        addMediaOrText.setText("Изменить текст");

        addMediaOrText.setCallbackData("add_text");
        rowInLineOne.add(addMediaOrText);

        addButton.setText("Добавить кнопки");
        addButton.setCallbackData("add_button");
        rowInLineOne.add(addButton);

        if (sendMessage.getDisableNotification() == null) {
            notification.setText("Уведомление: вкл");
        } else {
            notification.setText("Уведомление: выкл");
        }

        if (sendMessage.getDisableWebPagePreview() == null) {
            preview.setText("Превью: вкл");
        } else {
            preview.setText("Превью: выкл");
        }
        preview.setCallbackData("preview");
        rowInLineTwo.add(preview);

        autoCaption.setText("Автоподпись");
        comment.setText("Комментарии");
        copy.setText("Копировать");
        cancel.setText("Отмена");
        next.setText("Далее");

        notification.setCallbackData("notification");

        autoCaption.setCallbackData("auto_caption");
        comment.setCallbackData("comment");
        copy.setCallbackData("copy");
        cancel.setCallbackData("cancel_create_post");
        next.setCallbackData("next");

        rowInLineTwo.add(notification);
        rowInLineThree.add(autoCaption);
        rowInLineThree.add(comment);
        rowInLineFour.add(copy);
        rowInLineFive.add(cancel);
        rowInLineFive.add(next);

        keyboard.add(rowInLineOne);
        keyboard.add(rowInLineTwo);
        keyboard.add(rowInLineThree);
        keyboard.add(rowInLineFour);
        keyboard.add(rowInLineFive);
        return keyboard;
    }
}
