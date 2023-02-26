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
    Message messagePost;
    CopyMessage copyMessage;

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
        telegramSender.sendCopyMessage(copyMessage);

//        if (post.getSendMessage() != null) {
//            telegramSender.send(post.getSendMessage());
//            post.setSendMessage(null);
//        }
//        if (post.getSendPhoto() != null) {
//            telegramSender.sendPhoto(post.getSendPhoto());
//            post.setSendPhoto(null);
//        }

        Integer messageId = callbackQuery.getMessage().getMessageId();
        var editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
        editMessageText.setMessageId(messageId);
        editMessageText.setText("Пост успешно был опубликован");

        telegramSender.sendEditMessage(editMessageText);
    }


    public void cancelCreatePost(CallbackQuery callbackQuery) {
        deleteMessageCallbackQuery(callbackQuery);
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

    public void deleteCopyMessage(CopyMessage copyMessage) {
        String chatId = copyMessage.getChatId();
        Integer messageId = copyMessage.getMessageId();
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
//        if (message.hasText()) {
//            post.setSendMessage(new SendMessage());
//            post.getSendMessage().setChatId(message.getChatId());
//            post.getSendMessage().setText(message.getText());
//            post.getSendMessage().setReplyMarkup(createButtonPost(message));
//            post.getSendMessage().enableNotification();
//            telegramSender.send(post.getSendMessage());
//        }
        createMessagePost(message);
        copyMessage = copyMessage(message);
        copyMessage.setReplyMarkup(createButtonPost());
        telegramSender.sendCopyMessage(copyMessage);
//            post.setSendPhoto(new SendPhoto());
//            post.getSendPhoto().setChatId(message.getChatId());
//            post.getSendPhoto().setPhoto(new InputFile(getPhotoFieldId(message)));
//            post.getSendPhoto().setCaption(message.getCaption());
//            post.getSendPhoto().setReplyMarkup(createButtonPost(message));
//            telegramSender.sendPhoto(post.getSendPhoto());

    }

    public void addTextButton(CallbackQuery callbackQuery) {
        deleteMessageCallbackQuery(callbackQuery);
    }

    public void addText(Message message) {
        if (message.hasText()) {
            messagePost.setCaption(message.getText());
            copyMessage = copyMessage(messagePost);
            copyMessage.setReplyMarkup(createButtonPost());
            telegramSender.sendCopyMessage(copyMessage);
            deleteMessage(message);
        }
    }

//    public void addMedia(Message message) {
//        if (!message.hasText()) {
//            EditMessageMedia editMessageMedia = new EditMessageMedia();
//            editMessageMedia.setChatId(messagePost.getChatId());
//            editMessageMedia.setMedia(new InputMediaPhoto(getPhotoFieldId(message)));
//
//            try {
//                telegramSender.execute(editMessageMedia);
//            } catch (TelegramApiException e) {
//                throw new RuntimeException(e);
//            }
////
////            copyMessage = copyMessage(messagePost);
////            copyMessage.setReplyMarkup(createButtonPost(message));
////            telegramSender.sendCopyMessage(copyMessage);
//
////            post.setSendPhoto(new SendPhoto());
////            post.getSendPhoto().setChatId(message.getChatId());
////            post.getSendPhoto().setPhoto(new InputFile(getPhotoFieldId(message)));
////            post.getSendPhoto().setCaption(post.getSendMessage().getText());
////            post.getSendPhoto().setReplyMarkup(createButtonPost(message));
////            telegramSender.sendPhoto(post.getSendPhoto());
////
////            deleteMessage(message);
////            post.setSendMessage(null);
//        }
//    }

    public void prepareForPost() {
        InlineKeyboardMarkup inlineKeyboardMarkupPlug = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardPlug = new ArrayList<>();
        inlineKeyboardMarkupPlug.setKeyboard(keyboardPlug);
        copyMessage.setReplyMarkup(inlineKeyboardMarkupPlug);
        //copyMessage = copyMessage(messagePost);
        telegramSender.sendCopyMessage(copyMessage);
        copyMessage.setChatId(String.valueOf(channels.get(0).getChannelId()));
//        if (post.getSendMessage() != null) {
//            post.getSendMessage().setReplyMarkup(inlineKeyboardMarkupPlug);
//            telegramSender.send(post.getSendMessage());
//            post.getSendMessage().setChatId(String.valueOf(channels.get(0).getChannelId()));
//        } else if (post.getSendPhoto() != null) {
//            post.getSendPhoto().setReplyMarkup(inlineKeyboardMarkupPlug);
//            telegramSender.sendPhoto(post.getSendPhoto());
//            post.getSendPhoto().setChatId(String.valueOf(channels.get(0).getChannelId()));
//        }
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


    SendMessage sendMessage = new SendMessage();

    public void preview(CallbackQuery callbackQuery) {


        if (copyMessage != null){
            sendMessage.setText(callbackQuery.getMessage().getText());
            sendMessage.setChatId(callbackQuery.getMessage().getChatId());
            sendMessage.setDisableWebPagePreview(true);
            sendMessage.setReplyMarkup(createButtonPost());
            deleteMessageCallbackQuery(callbackQuery);
            copyMessage = null;
            telegramSender.send(sendMessage);
        }


        if (sendMessage.getDisableWebPagePreview() == null) {
            sendMessage.setDisableWebPagePreview(true);
        } else {
            sendMessage.enableWebPagePreview();
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
        var preview = new InlineKeyboardButton();
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



        if (messagePost.hasText()) {

            if (sendMessage.getDisableWebPagePreview() == null) {
                preview.setText("Превью: вкл");
            } else {
                preview.setText("Превью: выкл");
            }
            preview.setCallbackData("preview");
            rowInLineTwo.add(preview);
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


    public String getPhotoFieldId(Message message) {
        List<PhotoSize> photos = message.getPhoto();
        return photos.stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElseThrow().getFileId();
    }

    public CopyMessage copyMessage(Message message) {
//        return new CopyMessage(String.valueOf(message.getChatId()),
//                String.valueOf(message.getFrom().getId()), message.getMessageId());
        return CopyMessage.builder()
                .chatId(message.getChatId())
                .fromChatId(message.getFrom().getId())
                .messageId(message.getMessageId())
                .messageThreadId(message.getMessageThreadId())
                .caption(message.getCaption())
                .parseMode("html")
                .captionEntities(message.getCaptionEntities())
//                .disableNotification(true)
//                .replyToMessageId(message.getReplyToMessage().getMessageId())
                .allowSendingWithoutReply(true)
                .replyMarkup(message.getReplyMarkup())
                .protectContent(message.getHasProtectedContent()).build();
    }
}
