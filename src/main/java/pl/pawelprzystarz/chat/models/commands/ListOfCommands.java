package pl.pawelprzystarz.chat.models.commands;

import pl.pawelprzystarz.chat.models.UserModel;

import java.util.List;
import java.util.stream.Collectors;

public class ListOfCommands implements Command {
    @Override
    public void parseCommand(UserModel model, List<UserModel> userList, String... args) {

        for (UserModel userModel : userList) {
            userModel.sendMessagePacket("Lista dostępnych komend to: " + CommandFactory.getCommandList().stream().collect(Collectors.joining(", ")));
        }
    }

    @Override
    public int argsCount() {
        return 0;
    }

    @Override
    public String error() {
        return "Użycie to: /commands";
    }
}
