package pl.pawelprzystarz.chat.models.sockets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.pawelprzystarz.chat.models.LogModel;
import pl.pawelprzystarz.chat.repositories.LogRepository;
import pl.pawelprzystarz.chat.models.MessageModel;
import pl.pawelprzystarz.chat.models.UserModel;
import pl.pawelprzystarz.chat.models.commands.CommandFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@EnableWebSocket
@Configuration
public class ChatSocket extends TextWebSocketHandler /* BinaryWebSocketHandler */ implements WebSocketConfigurer {

    private LogRepository logRepository;

    private List<UserModel> userList;
    private CommandFactory factory;
    public static final Gson GSON = new GsonBuilder().create();

    @Autowired
    public ChatSocket(LogRepository logRepository){
        userList = new ArrayList<>();
        factory = new CommandFactory(userList);
        this.logRepository = logRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UserModel userModel = new UserModel(session);
        userList.add(userModel);

        userModel.sendMessagePacket("Witaj!");
        userModel.sendMessagePacket("Twoja pierwsza wiadomość będzie Twoim nickiem!");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        UserModel userModel = findUser(session);

        MessageModel messageModel = GSON.fromJson(message.getPayload(), MessageModel.class);

        switch (messageModel.getMessageType()){
            case MESSAGE:{
                parseMessagePacket(userModel, messageModel);
                break;
            }
        }
    }

    private void parseMessagePacket(UserModel userModel, MessageModel messageModel) {
        if (userModel.getNickname() == null) {
            for (UserModel model : userList) {
                if(model.getNickname() != null && model.getNickname().equals(messageModel.getContext())){
                    userModel.sendDialogPacket("Nick jest już zajęty!");
                    return;
                }
            }

            if (!Pattern.matches("\\w{3,20}", messageModel.getContext())) {
                userModel.sendDialogPacket("Nick nie może zawierać znaków specjalnych i musi mieć pomiędzy 3 a 20 znakami");
                return;
            }

            userModel.setNickname(messageModel.getContext());
            userModel.sendMessagePacket("Ustawiono Twój nick na " + messageModel.getContext());
            sendMessageToAllWithoutMe(userModel, "Użytkownik " + userModel.getNickname() + " dołączył do czatu!");
            return;

        }

        if (factory.parseCommand(userModel, messageModel.getContext())){
            return;
        }

        sendMessageToAll(generatePrefix(userModel) + messageModel.getContext());

        saveLogToDatabase(userModel, messageModel.getContext());

        userModel.setMessages(userModel.getMessages() + 1);
    }

    private void saveLogToDatabase(UserModel userModel, String context) {
        LogModel logModel = new LogModel();
        logModel.setMessage(context);
        logModel.setSender(userModel.getNickname());
        logModel.setDate(LocalDateTime.now());
        userModel.getSession().getLocalAddress().getHostName();

        logRepository.save(logModel);
    }


    private void sendMessageToAllWithoutMe(UserModel userModel, String s) {
        userList.stream().filter(e -> !e.equals(userModel)).forEach(e -> e.sendMessagePacket(s));
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
        userList.forEach(s -> s.sendMessagePacket(message));
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
