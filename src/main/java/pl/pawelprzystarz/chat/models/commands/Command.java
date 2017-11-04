package pl.pawelprzystarz.chat.models.commands;

import pl.pawelprzystarz.chat.models.UserModel;

import java.util.List;

public interface Command {
    void parseCommand(UserModel model, List<UserModel> userList, String ... args);
    int argsCount();
    String error();
}
