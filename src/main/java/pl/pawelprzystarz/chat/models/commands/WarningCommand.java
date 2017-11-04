package pl.pawelprzystarz.chat.models.commands;

import pl.pawelprzystarz.chat.models.UserModel;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class WarningCommand implements Command {
    @Override
    public void parseCommand(UserModel model, List<UserModel> userList, String... args) {
        String nickToWarning = args[0];
        Optional<UserModel> userModel = userList.stream().filter(s -> s.getNickname().equals(nickToWarning)).findAny();

        if(userModel.isPresent()){
            userModel.get().setWarnings(userModel.get().getWarnings()+1);
            model.sendMessage("Użytkownik " + nickToWarning + " dostał/a swoje " + userModel.get().getWarnings() + " ostrzeżenie!");
            if(userModel.get().getWarnings() == 3){
                try {
                    userModel.get().getSession().close();
                    model.sendMessage("Użytkownik " + nickToWarning + " został wyrzucony po 3 ostrzeżeniu!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else{
            model.sendMessage("Taki user nie istnieje");
        }
    }

    @Override
    public int argsCount() {
        return 1;
    }

    @Override
    public String error() {
        return "Użycie komendy to: /warning tutaj_nick";
    }
}
