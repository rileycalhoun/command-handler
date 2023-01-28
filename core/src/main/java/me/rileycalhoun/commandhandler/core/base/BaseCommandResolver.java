package me.rileycalhoun.commandhandler.core.base;

import me.rileycalhoun.commandhandler.core.CommandContext;
import me.rileycalhoun.commandhandler.core.CommandData;
import me.rileycalhoun.commandhandler.core.CommandHandler;
import me.rileycalhoun.commandhandler.core.CommandResolver;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class BaseCommandResolver implements CommandResolver {

    protected final CommandHandler commandHandler;

    public BaseCommandResolver (CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public CommandData find(String name, List<CommandData> dataList) {
        CommandData data = null;
        for(CommandData cd : dataList) {
            if(cd.getName() != null && cd.getName().equalsIgnoreCase(name)) {
                data = cd;
                break;
            } else {
                if(cd.getAliases() == null) continue;
                for(String alias : cd.getAliases()) {
                    if(alias == null) continue;
                    if (alias.equalsIgnoreCase(name)) {
                        data = cd;
                        break;
                    }
                }
            }
        }

        return data;
    }

    @Override
    public boolean execute(String commandString, CommandContext context) {
        if(commandString.contains(" ")) {
            String[] commandArray = commandString.split(" ");
            CommandData data = find(commandArray[0], commandHandler.getCommands());
            if(data == null) {
                context.getSubject().reply("That command cannot be found!");
                return false;
            }

            try {
                data.execute(context, Arrays.copyOfRange(commandArray, 1, commandArray.length));
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            CommandData data = find(commandString, commandHandler.getCommands());
            if(data == null) {
                context.getSubject().reply("That command cannot be found!");
                return false;
            }

            try {
                data.execute(context, new String[0]);
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

}
