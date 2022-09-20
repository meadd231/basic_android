package com.example.bookmemoapp.recyclerview;

import com.example.bookmemoapp.community.Comment;

import java.util.ArrayList;

public class Written {
    String writtenTitle;
    String writtenAuthor;
    String writtenPassword;
    String writtenDate;
    int viewsNum;
    ArrayList<Comment> comments;
    String contentsText;
    String contentsImage;
    ArrayList<Contents> contents;

    public Written(String writtenTitle, String writtenAuthor, String writtenPassword, String writtenDate, int viewsNum, ArrayList<Comment> comments, String contentsText, String contentsImage) {
        this.writtenTitle = writtenTitle;
        this.writtenAuthor = writtenAuthor;
        this.writtenPassword = writtenPassword;
        this.writtenDate = writtenDate;
        this.viewsNum = viewsNum;
        this.comments = comments;
        this.contentsText = contentsText;
        this.contentsImage = contentsImage;
    }

    public String getContentsText() {
        return contentsText;
    }

    public void setContentsText(String contentsText) {
        this.contentsText = contentsText;
    }

    public String getContentsImage() {
        return contentsImage;
    }

    public void setContentsImage(String contentsImage) {
        this.contentsImage = contentsImage;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public ArrayList<Contents> getContents() {
        return contents;
    }

    public void setContents(ArrayList<Contents> contents) {
        this.contents = contents;
    }

    public String getWrittenTitle() {
        return writtenTitle;
    }

    public void setWrittenTitle(String writtenTitle) {
        this.writtenTitle = writtenTitle;
    }

    public String getWrittenAuthor() {
        return writtenAuthor;
    }

    public void setWrittenAuthor(String writtenAuthor) {
        this.writtenAuthor = writtenAuthor;
    }

    public String getWrittenPassword() {
        return writtenPassword;
    }

    public void setWrittenPassword(String writtenPassword) {
        this.writtenPassword = writtenPassword;
    }

    public String getWrittenDate() {
        return writtenDate;
    }

    public void setWrittenDate(String writtenDate) {
        this.writtenDate = writtenDate;
    }

    public int getViewsNum() {
        return viewsNum;
    }

    public void setViewsNum(int viewsNum) {
        this.viewsNum = viewsNum;
    }
}