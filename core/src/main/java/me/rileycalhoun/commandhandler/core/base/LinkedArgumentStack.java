package me.rileycalhoun.commandhandler.core.base;

import me.rileycalhoun.commandhandler.core.ArgumentStack;
import me.rileycalhoun.commandhandler.core.CommandHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

public final class LinkedArgumentStack extends LinkedList<String> implements ArgumentStack {

    private final List<String> immutableArgsList;

    public LinkedArgumentStack(@NotNull String[] args) {
        Collections.addAll(this, args);
        immutableArgsList = Utils.immutable(args);
    }

    private LinkedArgumentStack(List<String> immutableArgsList) {
        this.immutableArgsList = immutableArgsList;
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
        LinkedArgumentStack stack = new LinkedArgumentStack(immutableArgsList);
        stack.addAll(this);
        return stack;
    }

    @Override public @NotNull ArgumentStack subList(int a, int b) {
        LinkedArgumentStack stack = new LinkedArgumentStack(immutableArgsList);
        stack.clear();
        for (int i = a; i < Math.min(size(), b); i++) {
            stack.add(get(i));
        }
        return stack;
    }

}