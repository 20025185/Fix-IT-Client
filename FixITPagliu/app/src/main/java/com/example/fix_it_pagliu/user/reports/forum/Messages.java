package com.example.fix_it_pagliu.user.reports.forum;

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
