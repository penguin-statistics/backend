package io.penguinstats.bean;

import org.bson.Document;

public class ScreenShot extends Documentable {

    private long timestamp;
    private String ip;
    private byte[] screenShot;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public byte[] getScreenShot() {
        return screenShot;
    }

    public void setScreenShot(byte[] screenShot) {
        this.screenShot = screenShot;
    }

    @Override
    public Document toDocument() {
        return new Document().append("ip", this.ip).append("timestamp", this.timestamp)
            .append("screenShot", this.screenShot);
    }
}
