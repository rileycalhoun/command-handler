package me.rileycalhoun.commandhandler.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Deque;
import java.util.List;

public interface ArgumentStack extends Deque<String>, List<String> {

    @NotNull @Unmodifiable List<String> asImmutableList();

    @NotNull String combine(String delimiter);

    @NotNull String combine(@NotNull String delimiter, int startIndex);

    @NotNull ArgumentStack copy();

    @NotNull ArgumentStack subList(int startIndex, int endIndex);

}