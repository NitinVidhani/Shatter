package application.example.shatter.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Chat {

    private String message;
    private String sender;
    private String reciever;
    private String recieverName;
    private String timeStamp;

    public Chat() {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        timeStamp = dateFormat.format(new Date()).toString();
    }

    public Chat(String message, String sender, String reciever, String recieverName) {
        this.message = message;
        this.sender = sender;
        this.reciever = reciever;
        this.recieverName = recieverName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getRecieverName() {
        return recieverName;
    }

    public void setRecieverName(String recieverName) {
        this.recieverName = recieverName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}
