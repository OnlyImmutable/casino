package net.casino.casino.defaultgames.slots.slot;

import java.util.List;

public class SlotMachineItem {

    private String displayName;
    private List<String> commands;

    public SlotMachineItem(String displayName, List<String> commands) {
        this.displayName = displayName;
        this.commands = commands;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getCommands() {
        return commands;
    }
}
