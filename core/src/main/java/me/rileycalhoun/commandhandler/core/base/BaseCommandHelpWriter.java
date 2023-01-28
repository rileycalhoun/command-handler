package me.rileycalhoun.commandhandler.core.base;

import me.rileycalhoun.commandhandler.core.CommandData;
import me.rileycalhoun.commandhandler.core.CommandHelpWriter;

import java.util.List;

public class BaseCommandHelpWriter implements CommandHelpWriter {

    @Override
    public String write(List<CommandData> commandData) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < commandData.size(); i++) {
            CommandData data = commandData.get(i);
            builder.append(data.getName()).append(" - ").append(data.getDescription());
            if(i != commandData.size() - 1) builder.append("\n");
        }
        return builder.toString();
    }

}
