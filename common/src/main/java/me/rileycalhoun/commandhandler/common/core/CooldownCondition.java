package me.rileycalhoun.commandhandler.common.core;

import me.rileycalhoun.commandhandler.common.CommandCondition;
import me.rileycalhoun.commandhandler.common.CommandContext;
import me.rileycalhoun.commandhandler.common.CommandSubject;
import me.rileycalhoun.commandhandler.common.HandledCommand;
import me.rileycalhoun.commandhandler.common.annotation.Cooldown;
import me.rileycalhoun.commandhandler.common.exception.CooldownException;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class CooldownCondition implements CommandCondition {

    private static final ScheduledExecutorService COOLDOWN_POOL = Executors.newSingleThreadScheduledExecutor();
    private final Map<UUID, Map<Integer, Long>> cooldowns = new ConcurrentHashMap<>();


    @Override
    public void test(@NotNull CommandSubject subject, @NotNull List<String> args, @NotNull HandledCommand command, @NotNull CommandContext context) throws Throwable {
        Cooldown cooldown = command.getAnnotation(Cooldown.class);
        if(cooldown == null || cooldown.value() == 0) return;
        UUID uuid = subject.getUUID();
        Map<Integer, Long> spans = get(uuid);
        Long created = spans.get(command.hashCode());
        if(created == null) {
            spans.put(command.hashCode(), System.currentTimeMillis());
            COOLDOWN_POOL.schedule(() -> spans.remove(command.hashCode()), cooldown.value(), cooldown.unit());
            return;
        }

        long passed = System.currentTimeMillis() - created;
        long left = cooldown.unit().toMillis(cooldown.value()) - passed;
        if(left > 0 && left < 1000) left = 1000L;
        throw new CooldownException(left);
    }

    private Map<Integer, Long> get(@NotNull UUID uuid) {
        return cooldowns.computeIfAbsent(uuid, u -> new ConcurrentHashMap<>());
    }
}
