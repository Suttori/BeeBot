package com.suttori.demobottty3.services;

import com.suttori.demobottty3.dao.ChannelRepository;
import com.suttori.demobottty3.dao.UserRepository;
import com.suttori.demobottty3.entity.User;
import com.suttori.demobottty3.entity.enums.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    @Transactional
    public void setPosition(Message message, String position) {
        userRepository.setPosition(position, message.getChatId());
    }

    public User getUser(Message message) {
        return userRepository.findUserByChatId(message.getChatId());
    }

    public boolean isUserRegister(Message message) {
        return userRepository.findUserByChatId(message.getChatId()) != null;
    }

    public void saveUser(Message message) {
        User user = new User();
        user.setChatId(message.getChatId());
        user.setFirstName(message.getFrom().getFirstName());
        user.setLastName(message.getFrom().getLastName());
        user.setUserName(message.getFrom().getUserName());
        user.setPosition("START_BOT");
        user.setRegisterTime(new Timestamp(System.currentTimeMillis()));
        user.setLanguageCode(message.getFrom().getLanguageCode());
        if (message.getFrom().getIsPremium() == null) {
            user.setTelegramPremium(false);
        } else {
            user.setTelegramPremium(true);
        }
        user.setPremiumBotUser(false);
        userRepository.save(user);
    }

}
