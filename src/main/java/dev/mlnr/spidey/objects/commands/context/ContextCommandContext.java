package dev.mlnr.spidey.objects.commands.context;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.objects.I18n;
import dev.mlnr.spidey.objects.commands.CommandContextBase;
import net.dv8tion.jda.api.events.interaction.command.*;

public class ContextCommandContext<T> extends CommandContextBase {
	private final GenericContextInteractionEvent<T> event;

	public ContextCommandContext(GenericContextInteractionEvent<T> event, I18n i18n, Cache cache) {
		super(event, i18n ,cache);
		this.event = event;
	}

	public T getTarget() {
		return event.getTarget();
	}

	public UserContextInteractionEvent getEventAsUserContext() {
		return (UserContextInteractionEvent) event;
	}

	public MessageContextInteractionEvent getEventAsMessageContext() {
		return (MessageContextInteractionEvent) event;
	}

	// overriden methods

	@Override
	public GenericContextInteractionEvent<T> getEvent() {
		return event;
	}

	@Override
	public boolean shouldHideResponse() {
		return true;
	}
}