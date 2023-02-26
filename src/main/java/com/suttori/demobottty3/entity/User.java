package com.suttori.demobottty3.entity;

import com.suttori.demobottty3.entity.enums.Position;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.sql.Timestamp;

@Entity(name = "\"user\"")
//@Entity(name = "usr")
public class User {

    @Id
    private Long chatId;

    private String firstName;
    private String lastName;
    private String userName;
    private Timestamp registerTime;
    private String position;
    private boolean isTelegramPremium;
    private String languageCode;
    private boolean isPremiumBotUser;

    public boolean isTelegramPremium() {
        return isTelegramPremium;
    }

    public void setTelegramPremium(boolean telegramPremium) {
        isTelegramPremium = telegramPremium;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public boolean isPremiumBotUser() {
        return isPremiumBotUser;
    }

    public void setPremiumBotUser(boolean premiumBotUser) {
        isPremiumBotUser = premiumBotUser;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Timestamp registerAt) {
        this.registerTime = registerAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", registerAt=" + registerTime +
                '}';
    }
}
