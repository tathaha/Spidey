package dev.mlnr.spidey.utils;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.internal.utils.Checks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventWaiter implements EventListener
{
    private final HashMap<Class<?>, Set<WaitingEvent<?>>> waitingEvents;
    private final ScheduledExecutorService threadpool;
    private final boolean shutdownAutomatically;

    public EventWaiter(ScheduledExecutorService threadpool, boolean shutdownAutomatically)
    {
        Checks.notNull(threadpool, "ScheduledExecutorService");
        Checks.check(!threadpool.isShutdown(), "Cannot construct EventWaiter with a closed ScheduledExecutorService!");

        this.waitingEvents = new HashMap<>();
        this.threadpool = threadpool;
        this.shutdownAutomatically = shutdownAutomatically;
    }

    public boolean isShutdown()
    {
        return threadpool.isShutdown();
    }

    public <T extends Event> void waitForEvent(Class<T> classType, Predicate<T> condition, Consumer<T> action,
                                               long timeout, TimeUnit unit, Runnable timeoutAction)
    {
        Checks.check(!isShutdown(), "Attempted to register a WaitingEvent while the EventWaiter's threadpool was already shut down!");
        Checks.notNull(classType, "The provided class type");
        Checks.notNull(condition, "The provided condition predicate");
        Checks.notNull(action, "The provided action consumer");

        final var we = new WaitingEvent<>(condition, action);
        final var set = waitingEvents.computeIfAbsent(classType, c -> new HashSet<>());
        set.add(we);

        if (timeout > 0 && unit != null)
        {
            threadpool.schedule(() ->
            {
                if (set.remove(we) && timeoutAction != null)
                    timeoutAction.run();
            }, timeout, unit);
        }
    }

    @Override
    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public final void onEvent(GenericEvent event)
    {
        Class<?> c = event.getClass();

        while (c != null)
        {
            final var set = waitingEvents.get(c);
            if (set != null)
                set.removeAll(Stream.of(set.toArray(new WaitingEvent[0])).filter(i -> i.attempt(event)).collect(Collectors.toSet()));
            if (event instanceof ShutdownEvent && shutdownAutomatically)
                threadpool.shutdown();
            c = c.getSuperclass();
        }
    }

    private static class WaitingEvent<T extends GenericEvent>
    {
        final Predicate<T> condition;
        final Consumer<T> action;

        WaitingEvent(Predicate<T> condition, Consumer<T> action)
        {
            this.condition = condition;
            this.action = action;
        }

        boolean attempt(T event)
        {
            if (condition.test(event))
            {
                action.accept(event);
                return true;
            }
            return false;
        }
    }
}