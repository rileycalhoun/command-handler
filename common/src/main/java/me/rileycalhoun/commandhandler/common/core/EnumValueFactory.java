package me.rileycalhoun.commandhandler.common.core;

import me.rileycalhoun.commandhandler.common.*;
import me.rileycalhoun.commandhandler.common.annotation.CaseSensitive;
import me.rileycalhoun.commandhandler.common.exception.ResolverFailedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class EnumValueFactory implements ValueResolverFactory {

    @SuppressWarnings("unchecked")
    @Override
    public ParameterResolver.@Nullable ValueResolver<?> create(@NotNull CommandParameter parameter, @NotNull HandledCommand command, @NotNull CommandHandler handler) {
        if(!parameter.getType().isEnum()) return null;
        Class<? extends Enum> type = (Class<? extends Enum>) parameter.getType();
        if(parameter.hasAnnotation(CaseSensitive.class)) {
            return new ParameterResolver.ValueResolver<Object>() {

                @Override
                public Object resolve(@NotNull ArgumentStack args, @NotNull CommandSubject subject, @NotNull CommandParameter parameter1) throws Throwable {
                    String value = args.popForParameter(parameter);
                    try {
                        return Enum.valueOf(type, value);
                    } catch (IllegalArgumentException e) {
                        throw new ResolverFailedException(this, value, parameter, e);
                    }

                }
            };
        } else {
            Map<String, Enum> enums = new HashMap<>();
            for(Enum e : type.getEnumConstants())
                enums.put(e.name().toLowerCase(), e);
            return new ParameterResolver.ValueResolver<Object>() {
                @Override
                public Object resolve(@NotNull ArgumentStack args, @NotNull CommandSubject subject, @NotNull CommandParameter parameter) throws Throwable {
                    String value = args.popForParameter(parameter);
                    Enum e = enums.get(value.toLowerCase());
                    if(e == null) throw new ResolverFailedException(this, value, parameter, null);
                    return e;
                }
            };
        }
    }

}
