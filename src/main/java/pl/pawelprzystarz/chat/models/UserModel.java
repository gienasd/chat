package pl.pawelprzystarz.chat.models;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import pl.pawelprzystarz.chat.models.sockets.ChatSocket;

import java.io.IOException;

public class UserModel {
    private String nickname;
    private WebSocketSession session;
    private int warnings;
    private int privateMessages;
    private int messages;

    public UserModel(WebSocketSession session){
        this.session = session;
        this.nickname = null;
        this.warnings = 0;
        this.privateMessages = 0;
        this.messages = 0;
    }

    public int getPrivateMessages() {
        return privateMessages;
    }

    public void setPrivateMessages(int privateMessages) {
        this.privateMessages = privateMessages;
    }

    public int getMessages() {
        return messages;
    }

    public void setMessages(int messages) {
        this.messages = messages;
    }

    public int getWarnings() {
        return warnings;
    }

    public void setWarnings(int warnings) {
        this.warnings = warnings;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserModel userModel = (UserModel) o;

        if (nickname != null ? !nickname.equals(userModel.nickname) : userModel.nickname != null) return false;
        return session != null ? session.equals(userModel.session) : userModel.session == null;
    }

    @Override
    public int hashCode() {
        int result = nickname != null ? nickname.hashCode() : 0;
        result = 31 * result + (session != null ? session.hashCode() : 0);
        return result;
    }

    public void sendMessagePacket(String message) {
            MessageModel messageModel = new MessageModel();
            messageModel.setMessageType(MessageModel.MessageType.MESSAGE);
            messageModel.setContext(message + "\n");

            try {
                session.sendMessage(new TextMessage(ChatSocket.GSON.toJson(messageModel)));
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void sendDialogPacket(String message) {
        MessageModel messageModel = new MessageModel();
        messageModel.setMessageType(MessageModel.MessageType.OPEN_DIALOG);
        messageModel.setContext(message + "\n");

        try {
            session.sendMessage(new TextMessage(ChatSocket.GSON.toJson(messageModel)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCloseWindowPacket() {
        MessageModel messageModel = new MessageModel();
        messageModel.setMessageType(MessageModel.MessageType.CLOSE_WINDOW);

        try {
            session.sendMessage(new TextMessage(ChatSocket.GSON.toJson(messageModel)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
