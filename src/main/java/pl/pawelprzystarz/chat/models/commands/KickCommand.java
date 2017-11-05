package pl.pawelprzystarz.chat.models.commands;

import pl.pawelprzystarz.chat.models.UserModel;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class KickCommand implements Command {
    @Override
    public void parseCommand(UserModel model, List<UserModel> userList, String... args) {
        String nickToKick = args[0];
        Optional<UserModel> userModel = userList.stream().filter(s -> s.getNickname().equals(nickToKick)).findAny();

        if(userModel.isPresent()){
            userModel.get().sendCloseWindowPacket();

        }else{
            model.sendMessagePacket("Taki user nie istnieje");
        }
    }

    @Override
    public int argsCount() {
        return 1;
    }

    @Override
    public String error() {
        return "Użycie komendy to: /kick tutaj_nick";
    }
}
