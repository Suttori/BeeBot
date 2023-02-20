package com.suttori.demobottty3.telegram;

import com.suttori.demobottty3.config.BotConfig;
import com.suttori.demobottty3.processor.Processor;
import com.suttori.demobottty3.services.ButtonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Update;
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
    private ButtonService buttonService;
    private Processor processor;

    public TelegramUpdateReceiver(
            BotConfig config,
        DefaultBotOptions botOptions) {
        this.config = config;
        this.botOptions = botOptions;
    }

    @Autowired
    public void setSendMessageService(ButtonService buttonService) {
        this.buttonService = buttonService;
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
        buttonService.generateButton(update.getMessage());
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
