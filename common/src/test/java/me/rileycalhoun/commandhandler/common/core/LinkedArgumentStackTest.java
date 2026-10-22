package me.rileycalhoun.commandhandler.common.core;

import me.rileycalhoun.commandhandler.common.ArgumentStack;
import me.rileycalhoun.commandhandler.common.CommandHandler;
import me.rileycalhoun.commandhandler.common.CommandParameter;
import me.rileycalhoun.commandhandler.common.HandledCommand;
import me.rileycalhoun.commandhandler.common.ParameterResolver;
import me.rileycalhoun.commandhandler.common.exception.MissingParameterException;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinkedArgumentStackTest {

    private static CommandParameter parameter(boolean consumesAll) {
        return new CommandParameter() {
            @Override public String getName() { return "arg"; }
            @Override public int getMethodIndex() { return 0; }
            @Override public Class<?> getType() { return String.class; }
            @Override public Type getFullType() { return String.class; }
            @Override public String getDefaultValue() { return null; }
            @Override public boolean consumesAllString() { return consumesAll; }
            @Override public Parameter getJavaParameter() { return null; }
            @Override public boolean isOptional() { return false; }
            @Override public <A extends Annotation> A getAnnotation(Class<A> annotation) { return null; }
            @Override public boolean isSwitch() { return false; }
            @Override public String getSwitchName() { return null; }
            @Override public boolean isFlag() { return false; }
            @Override public String getFlagName() { return null; }
            @Override public boolean getDefaultSwitch() { return false; }
            @Override public boolean hasAnnotation(Class<? extends Annotation> annotation) { return false; }
            @Override @SuppressWarnings("unchecked")
            public <T> ParameterResolver<?, T> getResolver() { return null; }
            @Override public CommandHandler getCommandHandler() { return null; }
            @Override public HandledCommand getDeclaringCommand() { return null; }
        };
    }

    private static LinkedArgumentStack stack(String... args) {
        return new LinkedArgumentStack(args, null);
    }

    @Test
    void combineJoinsAllArguments() {
        assertEquals("one two three", stack("one", "two", "three").combine(" "));
    }

    @Test
    void combineFromIndexSkipsEarlierArguments() {
        assertEquals("two,three", stack("one", "two", "three").combine(",", 1));
    }

    @Test
    void combineFromIndexPastEndIsEmpty() {
        assertEquals("", stack("one").combine(" ", 5));
    }

    @Test
    void immutableListReflectsOriginalArguments() {
        LinkedArgumentStack stack = stack("a", "b");
        stack.pop();
        assertEquals(List.of("a", "b"), stack.asImmutableList());
    }

    @Test
    void immutableListCannotBeModified() {
        assertThrows(UnsupportedOperationException.class,
                () -> stack("a").asImmutableList().add("b"));
    }

    @Test
    void copyIsIndependentOfOriginal() {
        LinkedArgumentStack original = stack("a", "b", "c");
        ArgumentStack copy = original.copy();
        copy.pop();
        assertEquals(3, original.size());
        assertEquals(2, copy.size());
        assertEquals(original.asImmutableList(), copy.asImmutableList());
    }

    @Test
    void subListReturnsRequestedRange() {
        ArgumentStack sub = stack("a", "b", "c", "d").subList(1, 3);
        assertEquals(List.of("b", "c"), List.copyOf(sub));
    }

    @Test
    void subListClampsEndIndexToSize() {
        ArgumentStack sub = stack("a", "b").subList(0, 99);
        assertEquals(List.of("a", "b"), List.copyOf(sub));
    }

    @Test
    void popForParameterReturnsAndRemovesFirstArgument() {
        LinkedArgumentStack stack = stack("first", "second");
        assertEquals("first", stack.popForParameter(parameter(false)));
        assertEquals(List.of("second"), List.copyOf(stack));
    }

    @Test
    void popForParameterConsumingAllJoinsAndClears() {
        LinkedArgumentStack stack = stack("hello", "wide", "world");
        assertEquals("hello wide world", stack.popForParameter(parameter(true)));
        assertTrue(stack.isEmpty());
    }

    @Test
    void popForParameterOnEmptyStackThrowsMissingParameter() {
        LinkedArgumentStack stack = stack();
        CommandParameter parameter = parameter(false);
        MissingParameterException e = assertThrows(MissingParameterException.class,
                () -> stack.popForParameter(parameter));
        assertSame(parameter, e.getParameter());
    }
}
