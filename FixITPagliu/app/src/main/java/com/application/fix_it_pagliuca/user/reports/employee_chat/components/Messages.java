package com.application.fix_it_pagliuca.user.reports.employee_chat.components;

@SuppressWarnings("ALL")
public class Messages {
    String msg;

    public Messages() {
    }

    @Override
    public String toString() {
        return "Messages{" +
                "msg='" + msg + '\'' +
                '}';
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
