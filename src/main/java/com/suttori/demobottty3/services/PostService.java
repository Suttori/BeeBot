package com.suttori.demobottty3.services;

import com.suttori.demobottty3.dao.ChannelRepository;
import com.suttori.demobottty3.entity.Channel;
import com.suttori.demobottty3.entity.Post;
import com.suttori.demobottty3.telegram.TelegramSender;
import com.suttori.demobottty3.util.Constants;
import com.suttori.demobottty3.util.PostUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Service
public class PostService {

    TelegramSender telegramSender;
    Channel channel;
    PostUtils postUtils;
    Message messagePost;
    CopyMessage copyMessage;
    ChannelService channelService;

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
        telegramSender.sendCopyMessage(copyMessage);
        messagePost = null;

        Integer messageId = callbackQuery.getMessage().getMessageId();
        var editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
        editMessageText.setMessageId(messageId);
        editMessageText.setText("Пост успешно был опубликован");

        telegramSender.sendEditMessage(editMessageText);
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
        createMessagePost(message);
        copyMessage = copyMessage(message);
        copyMessage.setReplyMarkup(createButtonPost());
        telegramSender.sendCopyMessage(copyMessage);
    }

    public void addTextButton(CallbackQuery callbackQuery) {
        postUtils.deleteMessageCallbackQuery(callbackQuery);
    }

    public void addText(Message message) {
        if (message.hasText()) {
            messagePost.setCaption(message.getText());
            copyMessage = copyMessage(messagePost);
            copyMessage.setReplyMarkup(createButtonPost());
            telegramSender.sendCopyMessage(copyMessage);
            postUtils.deleteMessage(message);
        }

        //TODO
    }

    public void prepareForPost() {
        InlineKeyboardMarkup inlineKeyboardMarkupPlug = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardPlug = new ArrayList<>();
        inlineKeyboardMarkupPlug.setKeyboard(keyboardPlug);

        copyMessage.setReplyMarkup(inlineKeyboardMarkupPlug);
        //copyMessage = copyMessage(messagePost);
        telegramSender.sendCopyMessage(copyMessage);
        copyMessage.setChatId(String.valueOf(channel.getChannelId()));

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

    public void addCustomButton(CallbackQuery callbackQuery) {

    }

    public EditMessageReplyMarkup createEditReplyMarkup(CallbackQuery callbackQuery) {
        var editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
        editMessageReplyMarkup.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageReplyMarkup.setReplyMarkup(createButtonPost());
        return editMessageReplyMarkup;
    }

    public void notification(CallbackQuery callbackQuery) {
        if (copyMessage.getDisableNotification() == null) {
            copyMessage.disableNotification();
        } else {
            copyMessage.enableNotification();
        }
        telegramSender.sendEditMessageReplyMarkup(createEditReplyMarkup(callbackQuery));
    }


    public InlineKeyboardMarkup createButtonPost() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
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

        if (messagePost.hasPhoto() || messagePost.hasAudio() || messagePost.hasAnimation() ||
                messagePost.hasVideo()) {
            ///TODO
            if (messagePost.getCaption() != null) {
                addMediaOrText.setText("Изменить текст");
            } else {
                addMediaOrText.setText("Добавить текст");
            }
            addMediaOrText.setCallbackData("add_text");
            rowInLineOne.add(addMediaOrText);
        }

        if (messagePost.getMediaGroupId() == null) {
            addButton.setText("Добавить кнопки");
            addButton.setCallbackData("add_button");
            rowInLineOne.add(addButton);
        }

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

        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
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
