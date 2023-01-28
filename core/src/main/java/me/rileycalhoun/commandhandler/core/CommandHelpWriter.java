package me.rileycalhoun.commandhandler.core;

import java.util.Map;

public interface CommandHelpWriter {

    String write(Map<String, CommandData> commandData);

}
