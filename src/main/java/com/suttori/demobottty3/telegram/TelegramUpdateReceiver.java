package com.suttori.demobottty3.telegram;

import com.suttori.demobottty3.config.BotConfig;
import com.suttori.demobottty3.processor.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageId;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

/*
Наследование идёт от LongPollingBot вместо TelegramLongPollingBot, чтобы отдельно имплементировать DefaultAbsSender
и не попадать в цикличную зависимость, когда надо отправить несколько сообщений
 */
@Component
public class TelegramUpdateReceiver implements LongPollingBot {

    private final BotConfig config;
    private final DefaultBotOptions botOptions;
    private Processor processor;

    TelegramSender telegramSender;

    public TelegramUpdateReceiver(
            BotConfig config,
            DefaultBotOptions botOptions, TelegramSender telegramSender) {
        this.config = config;
        this.botOptions = botOptions;
        this.telegramSender = telegramSender;
    }

    @Autowired
    public void setProcessor(Processor processor) {
        this.processor = processor;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }


    @Override
    public void onUpdateReceived(Update update) {
//
//        String text = "FIFA - will never  regret it";
//
//        //String[] words = text.split("\\|(|\\s*|,| ! |\\.)");
//        String[] words2 = text.split("\\s-\\s");
//        //String[] words = text.split("\\n");
//        for(String word : words2){
//            System.out.println(word);
//        }

//        if (update.hasMessage()) {
//            if (update.getMessage().getFrom().getUserName().equals("Vladyss10")) {
//                SendMessage sendMessage = new SendMessage();
//                sendMessage.setChatId(update.getMessage().getChatId());
//                sendMessage.setText("Бан нахуй, иди в пизду, долбаеб дырявый");
//                telegramSender.send(sendMessage);
//                return;
//            } else if (update.getMessage().getFrom().getUserName().equals("EUG_TG")) {
//                SendMessage sendMessage1 = new SendMessage();
//                sendMessage1.setChatId(update.getMessage().getChatId());
//                sendMessage1.setText("Думаешь самый умный? пошел нахуй долбаеб");
//                telegramSender.send(sendMessage1);
//                return;
//            }
//        }

        processor.process(update);
    }


    @Override
    public BotOptions getOptions() {
        return botOptions;
    }

    @Override
    public void clearWebhook() throws TelegramApiRequestException {
        /*
         Данный метод обязателен в интерфейсе, чтобы удалить вебхук при регистрации. Но если вебхука для бота никогда
         не создавалось, то метод можно оставить пустым
        */
    }
}
