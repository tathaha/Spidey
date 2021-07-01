package dev.mlnr.spidey.objects.command;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.objects.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Component;

public class CommandContext {
	private final SlashCommandEvent event;
	private final boolean hideResponse;
	private final I18n i18n;

	private final Cache cache;

	public CommandContext(SlashCommandEvent event, boolean hideResponse, I18n i18n, Cache cache) {
		this.event = event;
		this.hideResponse = hideResponse;
		this.i18n = i18n;

		this.cache = cache;
	}

	public User getUser() {
		return event.getUser();
	}

	public Member getMember() {
		return event.getMember();
	}

	public TextChannel getTextChannel() {
		return event.getTextChannel();
	}

	public Guild getGuild() {
		return event.getGuild();
	}

	public JDA getJDA() {
		return event.getJDA();
	}

	public I18n getI18n() {
		return this.i18n;
	}

	public SlashCommandEvent getEvent() {
		return this.event;
	}

	public Cache getCache() {
		return cache;
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

	// reply methods

	private boolean shouldHideResponse() {
		var shouldHide = getBooleanOption("hide");
		return shouldHide == null ? hideResponse : shouldHide;
	}

	public void reply(String content) {
		event.reply(content).setEphemeral(shouldHideResponse()).queue();
	}

	public void reply(EmbedBuilder embedBuilder) {
		event.replyEmbeds(embedBuilder.build()).setEphemeral(shouldHideResponse()).queue();
	}

	public void replyWithComponents(String content, Component... components) {
		event.reply(content).addActionRow(components).queue();
	}

	public void replyWithComponents(EmbedBuilder embedBuilder, Component... components) {
		event.replyEmbeds(embedBuilder.build()).addActionRow(components).queue();
	}

	public void replyLocalized(String key, Object... args) {
		reply(i18n.get(key, args));
	}

	public void replyError(String error) {
		reply(":no_entry: " + error);
	}

	public void replyErrorLocalized(String key, Object... args) {
		replyError(i18n.get(key, args));
	}

	public void editReply(EmbedBuilder embedBuilder) {
		event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
	}

	public void deleteReply() {
		event.getHook().deleteOriginal().queue();
	}
}