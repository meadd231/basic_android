package com.example.bookmemoapp.timer;

public class TimerSelect {
    String selectTitle;
    String selectAuthor;
    int selectIndex;

    public TimerSelect(String selectTitle) {
        this.selectTitle = selectTitle;
    }

    public String getSelectTitle() {
        return selectTitle;
    }

    public void setSelectTitle(String selectTitle) {
        this.selectTitle = selectTitle;
    }

    public String getSelectAuthor() {
        return selectAuthor;
    }

    public void setSelectAuthor(String selectAuthor) {
        this.selectAuthor = selectAuthor;
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }
}
