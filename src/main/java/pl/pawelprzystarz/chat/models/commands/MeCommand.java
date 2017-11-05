package pl.pawelprzystarz.chat.models.commands;

import pl.pawelprzystarz.chat.models.UserModel;

import java.util.List;

public class MeCommand implements Command {
    @Override
    public void parseCommand(UserModel model, List<UserModel> userList, String... args) {
        model.sendDialogPacket("Ilość Twoich normalnych wiadomości to: " + model.getMessages());
        model.sendDialogPacket("Ilość Twoich prywatnych wiadomości to: " + model.getPrivateMessages());
    }

    @Override
    public int argsCount() {
        return 0;
    }

    @Override
    public String error() {
        return "Użycie to /me";
    }
}
