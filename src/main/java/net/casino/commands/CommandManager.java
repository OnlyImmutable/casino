package net.casino.commands;

import net.casino.commands.attributes.CommandPermission;
import net.casino.commands.defaults.CoinCommand;
import net.casino.utils.InterfaceUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.SimplePluginManager;
import org.junit.runners.model.TestClass;

import java.lang.reflect.Field;
import java.util.HashMap;

public class CommandManager {

    private HashMap<String, Command> commands;

    public CommandManager() {
        this.commands = new HashMap<>();
    }

    public void registerCommands() {
        registerCommand(new CoinCommand());
    }

    public void registerCommand(Command command) {
        Class clazz = command.getClass();

        if (clazz.isAnnotationPresent(CommandPermission.class)) {
            CommandPermission permission = (CommandPermission) clazz.getAnnotation(CommandPermission.class);
            command.setPermission(permission.permission());
        }

        commands.put(command.getName(), command);

        try {
            Field map = SimplePluginManager.class.getDeclaredField("commandMap");
            map.setAccessible(true);
            CommandMap commandMap = (CommandMap) map.get(Bukkit.getPluginManager());
            commandMap.register(command.getName(), command);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
