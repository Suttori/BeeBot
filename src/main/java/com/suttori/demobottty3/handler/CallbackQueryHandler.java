package com.suttori.demobottty3.handler;

import com.suttori.demobottty3.entity.FlagController;
import com.suttori.demobottty3.services.MessageService;
import com.suttori.demobottty3.services.PostService;
import com.suttori.demobottty3.telegram.TelegramSender;
import com.suttori.demobottty3.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.suttori.demobottty3.handler.MessageHandler.flagController;

@Component
public class CallbackQueryHandler implements Handler<CallbackQuery> {

    private PostService postService;
    TelegramSender telegramSender;

    @Autowired
    public CallbackQueryHandler(PostService postService, TelegramSender telegramSender) {
        this.postService = postService;
        this.telegramSender = telegramSender;
    }

    @Override
    public void choose(CallbackQuery callbackQuery) {

        switch (callbackQuery.getData()) {
            case "add_media":
                telegramSender.send(MessageService.createSendMessageWithMaxLength("Отправьте файл, который нужно добавить к тексту", String.valueOf(callbackQuery.getMessage().getChatId())));
                flagController.setAddMedia(true);
                break;
            case "add_text":
                telegramSender.send(MessageService.createSendMessageWithMaxLength("Отправьте текст, на который хотите заменить описание/пост", String.valueOf(callbackQuery.getMessage().getChatId())));
                flagController.setAddText(true);
                break;
            case "add_button":
                postService.addCustomButton(callbackQuery);
                break;
            case "notification":
                postService.notification(callbackQuery);
                break;
            case "preview":
                //TODO
                break;
            case "auto_caption":
                //TODO
                break;
            case "comment":
                //TODO
                break;
            case "copy":
                //TODO
                break;
            case "cancel_create_post":
                flagController.setNewPost(false);
                postService.cancelCreatePost(callbackQuery);
                telegramSender.send(MessageService.createSendMessageWithMaxLength("Создание поста отменено", String.valueOf(callbackQuery.getMessage().getChatId())));
                break;
            case "next":
                flagController.setNewPost(false);
                    postService.nextButton(callbackQuery);
                break;
            case "scheduled":
                //TODO
                break;
            case "plan":
                //TODO
                break;
            case "auto_delete":
                //TODO
                break;
            case "cancel_next_button":
                //TODO
                break;
            case "publish":
                postService.publish(callbackQuery);
                break;
        }


    }
}
