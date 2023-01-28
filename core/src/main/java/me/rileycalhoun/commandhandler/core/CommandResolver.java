package me.rileycalhoun.commandhandler.core;

import java.util.List;

public interface CommandResolver {

    CommandData find(String name, List<CommandData> dataList);
    boolean execute(String commandString, CommandContext context);

}
