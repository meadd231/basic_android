package com.example.bookmemoapp.custom;

import com.example.bookmemoapp.recyclerview.MemoList;

import java.util.ArrayList;

public class Book {
    String title;
    String author;
    String first_memo;
    String date;
    ArrayList<MemoList> memos;

    public Book(String title, String author, String first_memo, String date, ArrayList<MemoList> memos) {
        this.title = title;
        this.author = author;
        this.first_memo = first_memo;
        this.date = date;
        this.memos = memos;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFirst_memo() {
        return first_memo;
    }

    public void setFirst_memo(String first_memo) {
        this.first_memo = first_memo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<MemoList> getMemos() {
        return memos;
    }

    public void setMemos(ArrayList<MemoList> memos) {
        this.memos = memos;
    }
}
