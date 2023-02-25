package com.suttori.demobottty3.entity;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.*;

@Component
public class Post {
    SendMessage sendMessage;
    SendPhoto sendPhoto;
    SendAnimation sendAnimation;
    SendAudio sendAudio;
    SendDocument sendDocument;
    SendVideo sendVideo;
    SendVideoNote sendVideoNote;
    SendVoice sendVoice;
    SendSticker sendSticker;
    SendMediaGroup sendMediaGroup;



    public SendMessage getSendMessage() {
        return sendMessage;
    }

    public void setSendMessage(SendMessage sendMessage) {
        this.sendMessage = sendMessage;
    }

    public SendPhoto getSendPhoto() {
        return sendPhoto;
    }

    public void setSendPhoto(SendPhoto sendPhoto) {
        this.sendPhoto = sendPhoto;
    }

    public SendAnimation getSendAnimation() {
        return sendAnimation;
    }

    public void setSendAnimation(SendAnimation sendAnimation) {
        this.sendAnimation = sendAnimation;
    }

    public SendAudio getSendAudio() {
        return sendAudio;
    }

    public void setSendAudio(SendAudio sendAudio) {
        this.sendAudio = sendAudio;
    }

    public SendDocument getSendDocument() {
        return sendDocument;
    }

    public void setSendDocument(SendDocument sendDocument) {
        this.sendDocument = sendDocument;
    }

    public SendVideo getSendVideo() {
        return sendVideo;
    }

    public void setSendVideo(SendVideo sendVideo) {
        this.sendVideo = sendVideo;
    }

    public SendVideoNote getSendVideoNote() {
        return sendVideoNote;
    }

    public void setSendVideoNote(SendVideoNote sendVideoNote) {
        this.sendVideoNote = sendVideoNote;
    }

    public SendVoice getSendVoice() {
        return sendVoice;
    }

    public void setSendVoice(SendVoice sendVoice) {
        this.sendVoice = sendVoice;
    }

    public SendSticker getSendSticker() {
        return sendSticker;
    }

    public void setSendSticker(SendSticker sendSticker) {
        this.sendSticker = sendSticker;
    }

    public SendMediaGroup getSendMediaGroup() {
        return sendMediaGroup;
    }

    public void setSendMediaGroup(SendMediaGroup sendMediaGroup) {
        this.sendMediaGroup = sendMediaGroup;
    }
}
