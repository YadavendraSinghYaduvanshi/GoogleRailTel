package com.cpm.googlerailtel.xmlGetterSetter;

/**
 * Created by neerajg on 09-01-2018.
 */

public class CaptureGetterSetter {
    private int captue;
    private int upload;
    private String visited_date;

    public int getCaptue() {
        return captue;
    }

    public void setCaptue(int captue) {
        this.captue = captue;
    }

    public int getUpload() {
        return upload;
    }

    public void setUpload(int upload) {
        this.upload = upload;
    }

    public String getVisited_date() {
        return visited_date;
    }

    public void setVisited_date(String visited_date) {
        this.visited_date = visited_date;
    }

    @Override
    public String toString() {
        return "CaptureGetterSetter{" +
                "captue='" + captue + '\'' +
                ", upload='" + upload + '\'' +
                ", visited_date='" + visited_date + '\'' +
                '}';
    }
}
