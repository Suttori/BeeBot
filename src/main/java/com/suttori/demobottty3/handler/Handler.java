package com.suttori.demobottty3.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface Handler<T> {
    void choose(T t);
}
