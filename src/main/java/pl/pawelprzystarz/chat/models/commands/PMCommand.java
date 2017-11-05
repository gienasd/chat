package pl.pawelprzystarz.chat.models.commands;

import pl.pawelprzystarz.chat.models.UserModel;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PMCommand implements Command{

    @Override
    public void parseCommand(UserModel model, List<UserModel> userList, String... args) {
        Optional<UserModel> userModel = userList.stream().filter(s -> s.getNickname().equals(args[0])).findAny();

        args[0] = "";

        if(userModel.isPresent()){
            userModel.get().sendMessagePacket("PM from " + "<" + model.getNickname() + "> (" +
                    LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "): " + Arrays.stream(args).collect(Collectors.joining(" ")));
            model.sendMessagePacket("PM to " + "<" + model.getNickname() + "> (" +
                    LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "): " + Arrays.stream(args).collect(Collectors.joining(" ")));
            model.setPrivateMessages(model.getPrivateMessages() + 1);
        }else{
            model.sendMessagePacket("Taki user nie istnieje");
        }
    }

    @Override
    public int argsCount() {
        return -1;
    }

    @Override
    public String error() {
        return "Użycie to: /pm tutaj_nick 'wiadomość'";
    }
}
