package dev.mlnr.spidey.objects.commands.slash;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.objects.I18n;
import dev.mlnr.spidey.objects.commands.CommandContextBase;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SlashCommandContext extends CommandContextBase {
	private final SlashCommandInteractionEvent event;
	private final boolean hideResponse;

	public SlashCommandContext(SlashCommandInteractionEvent event, boolean hideResponse, I18n i18n, Cache cache) {
		super(event, i18n, cache);
		this.event = event;
		this.hideResponse = hideResponse;
	}

	public Member getMember() {
		return event.getMember();
	}

	public TextChannel getTextChannel() {
		return event.getTextChannel();
	}

	public MessageChannel getChannel() {
		return event.getChannel();
	}

	// overriden methods

	@Override
	public SlashCommandInteractionEvent getEvent() {
		return this.event;
	}

	@Override
	public boolean shouldHideResponse() {
		var shouldHide = getBooleanOption("hide");
		return shouldHide == null ? hideResponse : shouldHide;
	}

	// options

	public String getStringOption(String name) {
		var option = event.getOption(name);
		return option == null ? null : option.getAsString();
	}

	public Long getLongOption(String name) {
		var option = event.getOption(name);
		return option == null ? null : option.getAsLong();
	}

	public User getUserOption(String name) {
		var option = event.getOption(name);
		return option == null ? null : option.getAsUser();
	}

	public Member getMemberOption(String name) {
		var option = event.getOption(name);
		return option == null ? null : option.getAsMember();
	}

	public Role getRoleOption(String name) {
		var option = event.getOption(name);
		return option == null ? null : option.getAsRole();
	}

	public GuildChannel getChannelOption(String name) {
		var option = event.getOption(name);
		return option == null ? null : option.getAsGuildChannel();
	}

	public Boolean getBooleanOption(String name) {
		var option = event.getOption(name);
		return option == null ? null : option.getAsBoolean();
	}
}