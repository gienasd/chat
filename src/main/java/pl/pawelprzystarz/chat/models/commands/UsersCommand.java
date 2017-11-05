package pl.pawelprzystarz.chat.models.commands;

import pl.pawelprzystarz.chat.models.UserModel;

import java.util.List;
import java.util.stream.Collectors;

public class UsersCommand implements Command {
    @Override
    public void parseCommand(UserModel model, List<UserModel> userList, String... args) {
        model.sendMessagePacket("Lista użytkowników online: " + userList.stream().map(s -> s.getNickname()).collect(Collectors.joining(", ")));
    }

    @Override
    public int argsCount() {
        return 0;
    }

    @Override
    public String error() {
        return "Użycie to: /users";
    }
}
