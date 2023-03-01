package com.suttori.demobottty3.services;

import com.suttori.demobottty3.dao.ChannelRepository;
import com.suttori.demobottty3.entity.Channel;
import com.suttori.demobottty3.telegram.TelegramSender;
import com.suttori.demobottty3.util.PostUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;


import java.util.*;

@Service
public class PostService {

    TelegramSender telegramSender;
    Channel channel;
    PostUtils postUtils;
    Message messagePost;
    CopyMessage copyMessage;
    ChannelService channelService;
    private List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    SendMessage sendMessage;
    List<MessageId> messageIds = new ArrayList<>();

    //TODO остаются старые кнопки, текстовое сообщение дублируется

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    public PostService(TelegramSender telegramSender, PostUtils postUtils, ChannelService channelService) {
        this.telegramSender = telegramSender;
        this.postUtils = postUtils;
        this.channelService = channelService;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void publish(CallbackQuery callbackQuery) {
        messageIds = new ArrayList<>();
        messagePost = null;
        copyMessage.setChatId(String.valueOf(channel.getChannelId()));
        telegramSender.sendCopyMessage(copyMessage);

        Integer messageId = callbackQuery.getMessage().getMessageId();
        var editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
        editMessageText.setMessageId(messageId);
        editMessageText.setText("Пост успешно был опубликован");

        telegramSender.sendEditMessage(editMessageText);
        //TODO new post
    }

    public void createMessagePost(Message message) {
        messagePost = new Message();
        messagePost.setChat(message.getChat());
        messagePost.setMessageId(message.getMessageId());
        messagePost.setFrom(message.getFrom());
        messagePost.setCaption(message.getCaption());
        messagePost.setText(message.getText());
        messagePost.setPhoto(message.getPhoto());
        messagePost.setAudio(message.getAudio());
        messagePost.setVideo(message.getVideo());
        messagePost.setVoice(message.getVoice());
        messagePost.setVideoNote(message.getVideoNote());
        messagePost.setDocument(message.getDocument());
        messagePost.setAnimation(message.getAnimation());
        messagePost.setSticker(message.getSticker());
        messagePost.setMediaGroupId(message.getMediaGroupId());
    }

    public void createPost(Message message) {

        if (message.hasText()) {
            sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setText(message.getText());
            return;
        }

        createMessagePost(message);
        copyMessage = copyMessage(message);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(createButtonPost(keyboard));
        copyMessage.setReplyMarkup(inlineKeyboardMarkup);
        messageIds.add(telegramSender.sendCopyMessage(copyMessage));
    }

    public void cancelCreatePost() {
        messagePost = null;
        keyboard = new ArrayList<>();
        messageIds = new ArrayList<>();
    }

    public void deletePreviousMessage(CallbackQuery callbackQuery) {
        postUtils.deleteMessageCallbackQuery(callbackQuery);
    }

    public void addText(Message message) {
        if (message.hasText()) {
            messagePost.setCaption(message.getText());
            copyMessage = copyMessage(messagePost);
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard1 = new ArrayList<>(keyboard);
            inlineKeyboardMarkup.setKeyboard(createButtonPost(keyboard1));
            copyMessage.setReplyMarkup(inlineKeyboardMarkup);
            messageIds.set(0, telegramSender.sendCopyMessage(copyMessage));
            postUtils.deleteMessage(message);
        }

        //TODO
    }


    public EditMessageReplyMarkup getPost(Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);

        var editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(String.valueOf(chatId));
        editMessageReplyMarkup.setMessageId(Math.toIntExact(messageIds.get(0).getMessageId()));
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);
        copyMessage.setReplyMarkup(inlineKeyboardMarkup);
        return editMessageReplyMarkup;
    }

    public void nextButton(CallbackQuery callbackQuery) {
        telegramSender.sendEditMessageReplyMarkup(getPost(callbackQuery.getMessage().getChatId()));

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


    public void backButton(CallbackQuery callbackQuery) {
        deletePreviousMessage(callbackQuery);
        copyMessage.setChatId(callbackQuery.getMessage().getChatId());
        telegramSender.sendEditMessageReplyMarkup(getPostWithButtonMenu(callbackQuery, messageIds.get(0).getMessageId()));
    }

    public EditMessageReplyMarkup getPostWithButtonMenu(CallbackQuery callbackQuery, Long messageId) {
        var editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
        editMessageReplyMarkup.setMessageId(Math.toIntExact(messageId));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard1 = new ArrayList<>(keyboard);
        inlineKeyboardMarkup.setKeyboard(createButtonPost(keyboard1));
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);
        return editMessageReplyMarkup;
    }

    public void notification(CallbackQuery callbackQuery) {
        if (copyMessage.getDisableNotification() == null) {
            copyMessage.disableNotification();
        } else {
            copyMessage.enableNotification();
        }
        telegramSender.sendEditMessageReplyMarkup(getPostWithButtonMenu(callbackQuery, Long.valueOf(callbackQuery.getMessage().getMessageId())));
    }

    public void addCustomButton(Message message) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        keyboard = postUtils.createCustomButton(message, new ArrayList<>());
        List<List<InlineKeyboardButton>> keyboard1 = new ArrayList<>(keyboard);
        inlineKeyboardMarkup.setKeyboard(createButtonPost(keyboard1));
        copyMessage.setReplyMarkup(inlineKeyboardMarkup);
        messageIds.set(0, telegramSender.sendCopyMessage(copyMessage));
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
        var autoCaption = new InlineKeyboardButton();
        var comment = new InlineKeyboardButton();
        var copy = new InlineKeyboardButton();
        var cancel = new InlineKeyboardButton();
        var next = new InlineKeyboardButton();


        ///TODO
        if (messagePost.getCaption() != null) {
            addMediaOrText.setText("Изменить текст");
        } else {
            addMediaOrText.setText("Добавить текст");
        }
        addMediaOrText.setCallbackData("add_text");
        rowInLineOne.add(addMediaOrText);


        //TODO "изменить кнопки"
        if (keyboard.isEmpty()) {
            addButton.setText("Добавить кнопки");
        } else {
            addButton.setText("Изменить кнопки");
        }
        addButton.setCallbackData("add_button");
        rowInLineOne.add(addButton);

//        if (messagePost.getMediaGroupId() == null) {
//            addButton.setText("Добавить кнопки");
//            addButton.setCallbackData("add_button");
//            rowInLineOne.add(addButton);
//        }

        if (copyMessage.getDisableNotification() == null) {
            notification.setText("Уведомление: вкл");
        } else {
            notification.setText("Уведомление: выкл");
        }

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

    public CopyMessage copyMessage(Message message) {
        return CopyMessage.builder()
                .chatId(message.getChatId())
                .fromChatId(message.getFrom().getId())
                .messageId(message.getMessageId())
                .messageThreadId(message.getMessageThreadId())
                .caption(message.getCaption())
                .parseMode("Markdown")
                .captionEntities(message.getCaptionEntities())
//                .disableNotification(true)
//                .replyToMessageId(message.getReplyToMessage().getMessageId())
                .allowSendingWithoutReply(true)
                .replyMarkup(message.getReplyMarkup())
                .protectContent(message.getHasProtectedContent()).build();
    }
}
