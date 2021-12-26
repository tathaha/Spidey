package dev.mlnr.spidey.objects.command;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.objects.Emojis;
import dev.mlnr.spidey.objects.I18n;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;

import java.util.List;

import static dev.mlnr.spidey.utils.Utils.splitComponents;

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

	// reply methods

	public boolean shouldHideResponse() {
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
		event.reply(content).addActionRows(splitComponents(components)).queue();
	}

	public void replyWithComponents(EmbedBuilder embedBuilder, Component... components) {
		event.replyEmbeds(embedBuilder.build()).addActionRows(splitComponents(components)).queue();
	}

	public void replyLocalized(String key, Object... args) {
		reply(i18n.get(key, args));
	}

	public void replyError(String error) {
		reply(Emojis.NO_ENTRY + " " + error);
	}

	public void replyErrorLocalized(String key, Object... args) {
		replyError(i18n.get(key, args));
	}

	public void replyErrorNoPerm(Permission permission, String action) {
		replyErrorLocalized("command_failures.self_no_perms", permission.getName(), action);
	}

	// followups

	public void sendFollowup(String content) {
		event.getHook().sendMessage(content).setEphemeral(shouldHideResponse()).queue();
	}

	public void sendFollowup(EmbedBuilder embedBuilder) {
		event.getHook().sendMessageEmbeds(embedBuilder.build()).setEphemeral(shouldHideResponse()).queue();
	}

	public void sendFollowUpWithComponents(String content, Component... components) {
		event.getHook().sendMessage(content).addActionRows(splitComponents(components)).queue();
	}

	public void sendFollowUpWithComponents(EmbedBuilder embedBuilder, List<ActionRow> components) {
		event.getHook().sendMessageEmbeds(embedBuilder.build()).addActionRows(components).queue();
	}

	public void sendFollowupError(String error) {
		event.getHook().sendMessage(Emojis.NO_ENTRY + " " + error).queue();
	}

	public void sendFollowupErrorLocalized(String key, Object... args) {
		sendFollowupError(i18n.get(key, args));
	}

	// editing

	public void editReply(EmbedBuilder embedBuilder) {
		event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
	}

	public void editComponents(EmbedBuilder embedBuilder, List<ActionRow> components) {
		event.getHook().editOriginalComponents(components).setEmbeds(embedBuilder.build()).queue();
	}

	// deleting the reply

	public void deleteReply() {
		event.getHook().deleteOriginal().queue();
	}

	// deferring

	public void deferAndRun(Runnable runnable) {
		deferAndRun(false, runnable);
	}

	public void deferAndRun(boolean ephemeral, Runnable runnable) {
		event.deferReply().setEphemeral(ephemeral).queue(); // JDA handles this async defer action, no need to wait
		runnable.run();
	}

	// utils

	public boolean hasSelfPermission(Permission... permissions) {
		return getGuild().getSelfMember().hasPermission(permissions);
	}

	public boolean hasSelfChannelPermissions(GuildChannel channel, Permission... permissions) {
		return getGuild().getSelfMember().hasPermission((IPermissionContainer) channel, permissions);
	}
}