package pl.pawelprzystarz.chat.models;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.pawelprzystarz.chat.models.commands.CommandFactory;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@EnableWebSocket
@Configuration
public class ChatSocket extends TextWebSocketHandler /* BinaryWebSocketHandler */ implements WebSocketConfigurer {

    private List<UserModel> userList;
    private CommandFactory factory;

    public ChatSocket(){
        userList = new ArrayList<>();
        factory = new CommandFactory(userList);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UserModel userModel = new UserModel(session);
        userList.add(userModel);

        userModel.sendMessage("Witaj!");
        userModel.sendMessage("Twoja pierwsza wiadomość będzie Twoim nickiem!");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        UserModel userModel = findUser(session);

        if(userModel.getNickname() == null){
            userModel.setNickname(message.getPayload());
            userModel.sendMessage("Ustawiono Twój nick na " + message.getPayload());
            sendMessageToAllWithoutMe(userModel, "Użytkownik " + userModel.getNickname() + " dołączył do czatu!");
            return;
        }

        if (factory.parseCommand(userModel, message.getPayload())){
            return;
        }

        sendMessageToAll(generatePrefix(userModel) + message.getPayload());
    }

    private void sendMessageToAllWithoutMe(UserModel userModel, String s) {
        userList.stream().filter(e -> !e.equals(userModel)).forEach(e -> e.sendMessage(s));
    }

    private String generatePrefix(UserModel userModel){
        return "<" + userModel.getNickname() + "> (" + getTime() + "): " ;
    }

    private String getTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    private UserModel findUser(WebSocketSession session) {
        return userList.stream().filter(s -> s.getSession().equals(session)).findAny().get();
    }

    private void sendMessageToAll(String message) {
        userList.forEach(s -> s.sendMessage(message));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        UserModel userModel = findUser(session);
        userList.remove(userModel);
        if(userModel.getNickname() != null) {
            sendMessageToAllWithoutMe(userModel, "Użytkownik " + userModel.getNickname() + " opuścił czat.");
        }
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(this, "/chat").setAllowedOrigins("*");
    }
}
