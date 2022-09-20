package com.example.bookmemoapp.recyclerview;

public class MemoList {
    String memoLog;
    String memoDate;

    public String getMemoLog() {
        return memoLog;
    }

    public void setMemoLog(String memoLog) {
        this.memoLog = memoLog;
    }

    public String getMemoDate() {
        return memoDate;
    }

    public void setMemoDate(String memoDate) {
        this.memoDate = memoDate;
    }

    public MemoList(String memoLog, String memoDate) {
        this.memoLog = memoLog;
        this.memoDate = memoDate;
    }
}
