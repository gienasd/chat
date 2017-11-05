package pl.pawelprzystarz.chat.models.commands;

import pl.pawelprzystarz.chat.models.UserModel;

import java.util.*;

public class CommandFactory {

    private static Map<String, Command> stringCommandMap;
    private static List<String> commandList;

    private List<UserModel> userList;

    public CommandFactory(List<UserModel> list){
         userList = list;
    }

    static {
        stringCommandMap = new HashMap<>();
        stringCommandMap.put("kick", new KickCommand());
        stringCommandMap.put("warning", new WarningCommand());
        stringCommandMap.put("pm", new PMCommand());
        stringCommandMap.put("commands", new ListOfCommands());
        stringCommandMap.put("users", new UsersCommand());
        stringCommandMap.put("me", new MeCommand());

        commandList = new ArrayList<>();
        for(String key : stringCommandMap.keySet()){
            commandList.add(key);
        }
    }

    public static List<String> getCommandList() {
        return commandList;
    }

    public boolean parseCommand(UserModel userModel, String s){
        if(!s.startsWith("/")){
            return false;
        }
        String[] parts = s.split(" ");
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        String commandAlone = parts[0].substring(1, parts[0].length());

        if(!stringCommandMap.containsKey(commandAlone)){
            userModel.sendMessagePacket("Taka komenda nie istnieje");
            return true;
        }

        Command command = stringCommandMap.get(commandAlone);

        if(command.argsCount() != -1){
            if(command.argsCount() != args.length){
                userModel.sendMessagePacket(command.error());
                return true;
            }
        }

        command.parseCommand(userModel, userList, args);
        return true;
    }
}
