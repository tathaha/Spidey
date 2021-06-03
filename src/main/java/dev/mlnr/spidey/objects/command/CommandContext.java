package dev.mlnr.spidey.objects.command;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.objects.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

public class CommandContext {
	private final SlashCommandEvent event;
	private final I18n i18n;

	private final Cache cache;

	public CommandContext(SlashCommandEvent event, I18n i18n, Cache cache) {
		this.event = event;
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

	public void reply(String content) {
		event.reply(content).setEphemeral(true).queue();
	}

	public void reply(String content, boolean ephemeral) {
		event.reply(content).setEphemeral(ephemeral).queue();
	}

	public void reply(EmbedBuilder embedBuilder) {
		reply(embedBuilder, true);
	}

	public void reply(EmbedBuilder embedBuilder, boolean ephemeral) {
		event.replyEmbeds(embedBuilder.build()).setEphemeral(ephemeral).queue();
	}

	public void replyWithButtons(String content, Button... buttons) {
		event.reply(content).addActionRow(buttons).queue();
	}

	public void replyWithButtons(EmbedBuilder embedBuilder, Button... buttons) {
		event.replyEmbeds(embedBuilder.build()).addActionRow(buttons).queue();
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