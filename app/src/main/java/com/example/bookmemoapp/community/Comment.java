package com.example.bookmemoapp.community;

public class Comment {
    String commentAuthor;
    String commentText;
    String commentDate;
    String commentPassword;

    public Comment(String commentAuthor, String commentText, String commentDate, String commentPassword) {
        this.commentAuthor = commentAuthor;
        this.commentText = commentText;
        this.commentDate = commentDate;
        this.commentPassword = commentPassword;
    }

    public String getCommentAuthor() {
        return commentAuthor;
    }

    public void setCommentAuthor(String commentAuthor) {
        this.commentAuthor = commentAuthor;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getCommentPassword() {
        return commentPassword;
    }

    public void setCommentPassword(String commentPassword) {
        this.commentPassword = commentPassword;
    }
}
