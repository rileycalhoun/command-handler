package me.rileycalhoun.commandhandler.cli.base;

import me.rileycalhoun.commandhandler.common.CommandContext;
import me.rileycalhoun.commandhandler.common.CommandHandler;
import me.rileycalhoun.commandhandler.common.CommandSubject;
import me.rileycalhoun.commandhandler.common.HandledCommand;
import me.rileycalhoun.commandhandler.common.exception.*;

import java.util.List;

public class DefaultExceptionHandler implements ExceptionHandler {

    private static final String VOWELS = "aeiou";
    public static DefaultExceptionHandler INSTANCE = new DefaultExceptionHandler();

    @Override
    public void handleException(CommandSubject sender,
                                CommandHandler handler,
                                HandledCommand command,
                                List<String> asImmutableList,
                                CommandContext context,
                                Throwable e,
                                boolean b) {
        if (e instanceof InvalidValueException ce) {
            sender.reply("Invalid " + ce.getValueType().getId() + ": " + e.getMessage());
        } else if (e instanceof InvalidCommandException ce) {
            sender.reply("Invalid command: " + ce.getInput());
        } else if (e instanceof MissingParameterException ce) {
            String article = VOWELS.indexOf(Character.toLowerCase(ce.getParameter().getName().charAt(0))) != -1 ?"an" : "a";
            sender.reply("You must specify " + article + " " + ce.getParameter().getName() + "!");
        } else if (e instanceof MissingPermissionException) {
            sender.reply("You do not have permission to execute this command!");
        } else if (e instanceof ResolverFailedException ce) {
            sender.reply("Cannot resolve " + ce.getParameter().getName() + " from value " + ce.getInput());
        } else if (e instanceof CooldownException) {
            sender.reply("You must wait " + ((CooldownException) e).getTimeFancy() + " before using this command again.");
        } else if (e instanceof SimpleCommandException) {
            sender.reply(e.getMessage());
        } else {
            sender.reply("An error occured while executing this command. Check console for details.");
            e.printStackTrace();
        }
    }

}
