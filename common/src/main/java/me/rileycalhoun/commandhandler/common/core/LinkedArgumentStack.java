package me.rileycalhoun.commandhandler.common.core;

import me.rileycalhoun.commandhandler.common.ArgumentStack;
import me.rileycalhoun.commandhandler.common.CommandHandler;
import me.rileycalhoun.commandhandler.common.CommandParameter;
import me.rileycalhoun.commandhandler.common.exception.MissingParameterException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public final class LinkedArgumentStack extends LinkedList<String> implements ArgumentStack {

    private final List<String> immutableArgsList;
    private final CommandHandler handler;

    public LinkedArgumentStack(@NotNull String[] args, CommandHandler handler) {
        Collections.addAll(this, args);
        this.immutableArgsList = Utils.immutable(args);
        this.handler = handler;
    }

    private LinkedArgumentStack(List<String> immutableArgsList, CommandHandler handler) {
        this.immutableArgsList = immutableArgsList;
        this.handler = handler;
    }

    @Override public @NotNull @Unmodifiable List<String> asImmutableList() {
        return immutableArgsList;
    }

    @Override public @NotNull String combine(String delimiter) {
        return String.join(delimiter, this);
    }

    @Override public @NotNull String combine(@NotNull String delimiter, int startIndex) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (int i = startIndex; i < size(); i++)
            joiner.add(get(i));
        return joiner.toString();
    }

    @Override public @NotNull ArgumentStack copy() {
        LinkedArgumentStack stack = new LinkedArgumentStack(immutableArgsList, handler);
        stack.addAll(this);
        return stack;
    }

    @Override public @NotNull ArgumentStack subList(int a, int b) {
        LinkedArgumentStack stack = new LinkedArgumentStack(immutableArgsList, handler);
        stack.clear();
        for (int i = a; i < Math.min(size(), b); i++) {
            stack.add(get(i));
        }
        return stack;
    }

    @Override
    public @NotNull CommandHandler getCommandHandler() {
        return handler;
    }

    @Override
    public String popForParameter(CommandParameter parameter) {
        try {
            if (!parameter.consumesAllString()) return pop();
            String value = combine (" ");
            clear();
            return value;
        } catch (NoSuchElementException e) {
            throw new MissingParameterException(parameter, parameter.getResolver());
        }
    }

}