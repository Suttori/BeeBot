//package com.suttori.demobottty3;
//
//import com.suttori.demobottty3.config.BotConfig;
//import com.suttori.demobottty3.processor.Processor;
//import com.suttori.demobottty3.services.ButtonService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Message;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//import java.util.List;
//
//@Component
//public class TelegramBot extends TelegramLongPollingBot {
//
//    @Autowired
//    final BotConfig config;
//
//    public TelegramBot(BotConfig config) {
//        super(config.getToken());
//        this.config = config;
//    }
//
//    @Override
//    public String getBotUsername() {
//        return config.getBotName();
//    }
//
//    private ButtonService buttonService;
//    private Processor processor;
//
//    @Autowired
//    public void setSendMessageService(ButtonService buttonService) {
//        this.buttonService = buttonService;
//    }
//
//    @Autowired
//    public void setProcessor(Processor processor) {
//        this.processor = processor;
//    }
//
//    @Override
//    public void onUpdateReceived(Update update) {
//
//
//        buttonService.generateButton(update.getMessage());
//
//
////        try {
////            List<ChatMember> chatMemberAdministrators = execute(new GetChatAdministrators(String.valueOf(update.getMessage().getForwardFromChat().getId())));
////            System.out.println(chatMemberAdministrators);
////
////        } catch (TelegramApiException e) {
////            throw new RuntimeException(e);
////        }
//
//
//         sendMessage(processor.process(update));
//    }
//
//    public void sendMessage(SendMessage sendMessage) {
//        try {
//            execute(sendMessage);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
