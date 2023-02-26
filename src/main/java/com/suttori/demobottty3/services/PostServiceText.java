package com.suttori.demobottty3.services;

import com.suttori.demobottty3.dao.ChannelRepository;
import com.suttori.demobottty3.entity.Channel;
import com.suttori.demobottty3.entity.Post;
import com.suttori.demobottty3.telegram.TelegramSender;
import com.suttori.demobottty3.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
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
public class PostServiceText {

    TelegramSender telegramSender;
    List<Channel> channels;
    SendMessage sendMessage;


    public SendMessage getSendMessage() {
        return sendMessage;
    }

    @Autowired
    ChannelRepository channelRepository;

    String errorMessage;
    String successMessage;

    @Autowired
    public PostServiceText(TelegramSender telegramSender) {
        this.telegramSender = telegramSender;
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
        telegramSender.send(sendMessage);
        sendMessage = null;
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
        sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(message.getText());
    }

    public void createPost(Message message) {
        createMessagePost(message);
        sendMessage.setReplyMarkup(createButtonPost());
        telegramSender.send(sendMessage);


    }

    public void addTextButton(CallbackQuery callbackQuery) {
        deleteMessageCallbackQuery(callbackQuery);
    }

    public void addText(Message message) {
        if (message.hasText()) {
            sendMessage.setText(message.getText());
            sendMessage.setReplyMarkup(createButtonPost());
            telegramSender.send(sendMessage);
            deleteMessage(message);
        }
    }

    public void prepareForPost() {
        InlineKeyboardMarkup inlineKeyboardMarkupPlug = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardPlug = new ArrayList<>();
        inlineKeyboardMarkupPlug.setKeyboard(keyboardPlug);
        sendMessage.setReplyMarkup(inlineKeyboardMarkupPlug);
        telegramSender.send(sendMessage);
        sendMessage.setChatId(String.valueOf(channels.get(0).getChannelId()));
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

    }

    public EditMessageReplyMarkup createEditReplyMarkup(CallbackQuery callbackQuery) {
        var editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
        editMessageReplyMarkup.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageReplyMarkup.setReplyMarkup(createButtonPost());
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

        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
}
