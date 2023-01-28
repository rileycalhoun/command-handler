package me.rileycalhoun.commandhandler.core.base;

import me.rileycalhoun.commandhandler.core.CommandData;
import me.rileycalhoun.commandhandler.core.CommandHelpWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseCommandHelpWriter implements CommandHelpWriter {

    @Override
    public String write(Map<String, CommandData> commandData) {
        StringBuilder builder = new StringBuilder();

        List<CommandData> used = new ArrayList<>();
        List<CommandData> values = commandData.values().stream().toList();
        for(int i = 0; i < commandData.size(); i++) {
            CommandData data = values.get(i);
            if(used.contains(data)) continue;
            used.add(data);
            builder.append(data.getName());
            if(data.getDescription() != null && !(data.getDescription().isBlank())) builder.append(" - ").append(data.getDescription());
            if(i != commandData.size() - 1) builder.append("\n");
        }

        return builder.toString();
    }

}
