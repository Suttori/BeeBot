package com.suttori.demobottty3.handler;

import com.suttori.demobottty3.services.MessageService;
import com.suttori.demobottty3.services.PostService;
import com.suttori.demobottty3.services.PostServiceText;
import com.suttori.demobottty3.services.UserService;
import com.suttori.demobottty3.telegram.TelegramSender;
import com.suttori.demobottty3.util.PostUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class CallbackQueryHandler implements Handler<CallbackQuery> {

    private PostService postService;
    private PostServiceText postServiceText;
    private TelegramSender telegramSender;
    private UserService userService;
    private PostUtils postUtils;

    public CallbackQueryHandler(PostService postService, PostServiceText postServiceText, TelegramSender telegramSender, UserService userService, PostUtils postUtils) {
        this.postService = postService;
        this.postServiceText = postServiceText;
        this.telegramSender = telegramSender;
        this.userService = userService;
        this.postUtils = postUtils;
    }

    @Override
    public void choose(CallbackQuery callbackQuery) {

        switch (callbackQuery.getData()) {
            case "add_text":
                telegramSender.send(MessageService.createSendMessageWithMaxLength("Отправьте текст, на который хотите заменить описание/пост", String.valueOf(callbackQuery.getMessage().getChatId())));
                userService.setPosition(callbackQuery.getMessage(), "CHANGE_TEXT");
                if (postServiceText.getSendMessage() != null) {
                    postServiceText.deletePreviousMessage(callbackQuery);
                    return;
                }
                postService.deletePreviousMessage(callbackQuery);
                break;
            case "add_button":
                telegramSender.send(MessageService.createSendMessageWithMaxLength("Отправьте кнопки, которые хотите прикрепить к посту", String.valueOf(callbackQuery.getMessage().getChatId())));
                userService.setPosition(callbackQuery.getMessage(), "ADD_BUTTONS");
                if (postServiceText.getSendMessage() != null) {
                    postServiceText.deletePreviousMessage(callbackQuery);
                    return;
                }
                postService.deletePreviousMessage(callbackQuery);


                break;
            case "notification":
                //TODO
                if (postServiceText.getSendMessage() != null) {
                    postServiceText.notification(callbackQuery);
                    return;
                }
                postService.notification(callbackQuery);
                break;
            case "preview":
                postServiceText.preview(callbackQuery);
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
                postService.cancelCreatePost();
                postServiceText.cancelCreatePost();
                userService.setPosition(callbackQuery.getMessage(), "DEFAULT_POSITION");
                postUtils.deleteMessageCallbackQuery(callbackQuery);
                telegramSender.send(MessageService.createSendMessageWithMaxLength("Создание поста отменено", String.valueOf(callbackQuery.getMessage().getChatId())));
                break;
            case "next":


                if (postServiceText.getSendMessage() != null) {
                    postServiceText.nextButton(callbackQuery);
                    return;
                }
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
                postService.backButton(callbackQuery);




                break;
            case "publish":
                userService.setPosition(callbackQuery.getMessage(), "DEFAULT_POSITION");
                if (postServiceText.getSendMessage() != null) {
                    postServiceText.publish(callbackQuery);
                    return;
                }

                postService.publish(callbackQuery);
                break;
        }


    }
}
