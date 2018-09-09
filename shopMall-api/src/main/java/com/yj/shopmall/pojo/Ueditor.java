package com.yj.shopmall.pojo;

public class Ueditor {
    private  String state;
    private  String url;
    private  String title;
    private  String original;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Ueditor{");
        sb.append("state='").append(state).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", original='").append(original).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
