package com.suttori.demobottty3.services;

import com.suttori.demobottty3.dao.ChannelRepository;
import com.suttori.demobottty3.entity.Channel;
import com.suttori.demobottty3.entity.Post;
import com.suttori.demobottty3.telegram.TelegramSender;
import com.suttori.demobottty3.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Service
public class PostService {

    TelegramSender telegramSender;
    List<Channel> channels;

    Map<String, SendMediaBotMethod<Message>> mapMedia = new HashMap<>();

    @Autowired
    ChannelRepository channelRepository;

    Post post;

    String errorMessage;
    String successMessage;

    @Autowired
    public PostService(TelegramSender telegramSender, Post post) {
        this.telegramSender = telegramSender;
        this.post = post;
    }

    public void chooseChannel(Message message) {
        channels = channelRepository.findChannelByUserId(message.getChatId());

        if (channels.isEmpty()) {
            errorMessage = "Вы не добавили ни одного канала в бота. Чтобы Добавить канал нажмите /add и следуйте дальнейшим инструкциям.";
            SendMessage sendMessage = SendMessage.builder()
                    .text(errorMessage)
                    .chatId(message.getChatId())
                    .build();
            telegramSender.send(sendMessage);
        }

        if (channels.size() > 1) {
            successMessage = "Выберите канал, в который хотите опубликовать пост";
            SendMessage sendMessage = SendMessage.builder()
                    .text(successMessage)
                    .chatId(message.getChatId())
                    .build();
            telegramSender.send(sendMessage);
            //TODO
        }

        if (channels.size() == 1) {
            successMessage = Constants.CREATE_POST;
            SendMessage sendMessage = SendMessage.builder()
                    .text(successMessage)
                    .chatId(message.getChatId())
                    .build();
            telegramSender.send(sendMessage);
        }
    }


    public void publish(CallbackQuery callbackQuery) {
        if (post.getSendMessage() != null) {
            telegramSender.send(post.getSendMessage());
            post.setSendMessage(null);
        }
        if (post.getSendPhoto() != null) {
            telegramSender.sendPhoto(post.getSendPhoto());
            post.setSendPhoto(null);
        }

        Integer messageId = callbackQuery.getMessage().getMessageId();
        var editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
        editMessageText.setMessageId(messageId);
        editMessageText.setText("Пост успешно был опубликован");

        telegramSender.sendEditMessage(editMessageText);
    }


    public void cancelCreatePost(CallbackQuery callbackQuery) {
        deleteMessageCallbackQuery(callbackQuery);
        post.setSendMessage(null);
        post.setSendPhoto(null);
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


    Message messagePost;

    public void createPost(Message message) {
        if (message.hasText()) {



            post.setSendMessage(new SendMessage());
            post.getSendMessage().setChatId(message.getChatId());
            post.getSendMessage().setText(message.getText());
            post.getSendMessage().setReplyMarkup(createButtonPost(message));
            post.getSendMessage().enableNotification();
            telegramSender.send(post.getSendMessage());
        }

        if (message.hasPhoto()) {
            messagePost = new Message();
            messagePost.setChat(message.getChat());
            messagePost.setPhoto(message.getPhoto());
            messagePost.setCaption(message.getCaption());
            messagePost.setReplyMarkup(createButtonPost(message));
            CopyMessage copyMessage = new CopyMessage();
            copyMessage.setMessageId(messagePost.getMessageId());
            telegramSender.sendCopyMessage(copyMessage);

//            post.setSendPhoto(new SendPhoto());
//            post.getSendPhoto().setChatId(message.getChatId());
//            post.getSendPhoto().setPhoto(new InputFile(getPhotoFieldId(message)));
//            post.getSendPhoto().setCaption(message.getCaption());
//            post.getSendPhoto().setReplyMarkup(createButtonPost(message));
//            telegramSender.sendPhoto(post.getSendPhoto());
        }

        if (message.hasAudio()) {

        }

        if (message.hasVideo()) {

        }

        if (message.hasVoice()) {

        }

        if (message.hasVideoNote()) {

        }

        if (message.hasDocument()) {

        }

        if (message.hasAnimation()) {

        }


        if (message.hasPoll()) {

        }

        if (message.hasSticker()) {

        }

    }


    public String getPhotoFieldId(Message message) {
        List<PhotoSize> photos = message.getPhoto();
        return photos.stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElseThrow().getFileId();
    }

//    public CopyMessage copyMessage(Message message) {
//        return new CopyMessage(String.valueOf(message.getChatId()),
//                String.valueOf(message.getFrom().getId()), message.getMessageId());
//    }


    public void addText(Message message) {
        if (message.hasText()) {
            post.getSendPhoto().setChatId(message.getChatId());
            post.getSendPhoto().setCaption(message.getText());
            post.getSendPhoto().setReplyMarkup(createButtonPost(message));
            telegramSender.sendPhoto(post.getSendPhoto());
            deleteMessage(message);


        }
    }

    public void addMedia(Message message) {
        if (message.hasPhoto()) {
            post.setSendPhoto(new SendPhoto());
            post.getSendPhoto().setChatId(message.getChatId());
            post.getSendPhoto().setPhoto(new InputFile(getPhotoFieldId(message)));
            post.getSendPhoto().setCaption(post.getSendMessage().getText());
            post.getSendPhoto().setReplyMarkup(createButtonPost(message));
            telegramSender.sendPhoto(post.getSendPhoto());

            deleteMessage(message);
            post.setSendMessage(null);
        }
    }

    public void prepareForPost() {
        InlineKeyboardMarkup inlineKeyboardMarkupPlug = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardPlug = new ArrayList<>();
        inlineKeyboardMarkupPlug.setKeyboard(keyboardPlug);

        if (post.getSendMessage() != null) {
            post.getSendMessage().setReplyMarkup(inlineKeyboardMarkupPlug);
            telegramSender.send(post.getSendMessage());
            post.getSendMessage().setChatId(String.valueOf(channels.get(0).getChannelId()));
        } else if (post.getSendPhoto() != null) {
            post.getSendPhoto().setReplyMarkup(inlineKeyboardMarkupPlug);
            telegramSender.sendPhoto(post.getSendPhoto());
            post.getSendPhoto().setChatId(String.valueOf(channels.get(0).getChannelId()));
        }
    }

    public void nextButton(CallbackQuery callbackQuery) {
        prepareForPost();
        deleteMessageCallbackQuery(callbackQuery);

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
        if (post.getSendMessage() != null) {

        }
        if (post.getSendPhoto() != null) {

        }


        List<SendMediaBotMethod<Message> > messageList = new ArrayList<>();
        messageList.add(post.getSendVoice());
        Message message;




    }

    public void notification(CallbackQuery callbackQuery) {
        if (post.getSendMessage() != null) {
            if (post.getSendMessage().getDisableNotification() == null) {
                post.getSendMessage().disableNotification();

                Integer messageId = callbackQuery.getMessage().getMessageId();
                var editMessageText = new EditMessageText();
                editMessageText.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
                editMessageText.setMessageId(messageId);
                editMessageText.setReplyMarkup(createButtonPost(callbackQuery.getMessage()));
                editMessageText.setText(callbackQuery.getMessage().getText());


                telegramSender.sendEditMessage(editMessageText);
//                post.getSendMessage().setReplyMarkup(createButtonPost(callbackQuery.getMessage()));
//                telegramSender.send(post.getSendMessage());
//                deleteMessageCallbackQuery(callbackQuery);
                return;
            }
            if (post.getSendMessage().getDisableNotification()) {
                post.getSendMessage().enableNotification();
                Integer messageId = callbackQuery.getMessage().getMessageId();
                var editMessageText = new EditMessageText();
                editMessageText.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
                editMessageText.setMessageId(messageId);
                editMessageText.setReplyMarkup(createButtonPost(callbackQuery.getMessage()));
                editMessageText.setText(callbackQuery.getMessage().getText());



                telegramSender.sendEditMessage(editMessageText);
//                post.getSendMessage().setReplyMarkup(createButtonPost(callbackQuery.getMessage()));
//                telegramSender.send(post.getSendMessage());
//                deleteMessageCallbackQuery(callbackQuery);
                return;
            }
        }




    }


    public InlineKeyboardMarkup createButtonPost(Message message) {
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
        var preview = new InlineKeyboardButton();
        var autoCaption = new InlineKeyboardButton();
        var comment = new InlineKeyboardButton();
        var copy = new InlineKeyboardButton();
        var cancel = new InlineKeyboardButton();
        var next = new InlineKeyboardButton();

        if (post.getSendPhoto() != null || post.getSendAudio() != null || post.getSendAnimation() != null ||
                post.getSendVideo() != null) {
            if (message.getCaption() != null) {
                addMediaOrText.setText("Изменить текст");
                addMediaOrText.setCallbackData("add_text");
            } else {
                addMediaOrText.setText("Добавить текст");
                addMediaOrText.setCallbackData("add_text");
            }
        } else {
            addMediaOrText.setText("Добавить медиа");
            addMediaOrText.setCallbackData("add_media");
        }


        if (post.getSendMediaGroup() == null) {
            addButton.setText("Добавить кнопки");
            addButton.setCallbackData("add_button");
        }

//        if (post.getSendMessage().getDisableNotification() == null) {
//            notification.setText("Уведомление: вкл");
//        } else {
//            notification.setText("Уведомление: выкл");
//        }

        preview.setText("Превью");
        autoCaption.setText("Автоподпись");
        comment.setText("Комментарии");
        copy.setText("Копировать");
        cancel.setText("Отмена");
        next.setText("Далее");

//        notification.setCallbackData("notification");
        preview.setCallbackData("preview");
        autoCaption.setCallbackData("auto_caption");
        comment.setCallbackData("comment");
        copy.setCallbackData("copy");
        cancel.setCallbackData("cancel_create_post");
        next.setCallbackData("next");

        rowInLineOne.add(addMediaOrText);
        rowInLineOne.add(addButton);
        rowInLineTwo.add(notification);
        rowInLineTwo.add(preview);
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


}
