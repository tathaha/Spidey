package dev.mlnr.spidey.objects.commands;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.objects.Emojis;
import dev.mlnr.spidey.objects.I18n;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.List;

import static net.dv8tion.jda.api.interactions.components.ActionRow.partitionOf;

public abstract class CommandContextBase {
	private final GenericCommandInteractionEvent event;
	public final I18n i18n;
	public final Cache cache;

	protected CommandContextBase(GenericCommandInteractionEvent event, I18n i18n, Cache cache) {
		this.event = event;
		this.i18n = i18n;
		this.cache = cache;
	}

	public abstract GenericCommandInteractionEvent getEvent();
	public abstract boolean shouldHideResponse();

	public User getUser() {
		return event.getUser();
	}

	public Guild getGuild() {
		return event.getGuild();
	}

	public JDA getJDA() {
		return event.getJDA();
	}

	public I18n getI18n() {
		return i18n;
	}

	public Cache getCache() {
		return cache;
	}

	// reply methods

	public void reply(String content) {
		event.reply(content).setEphemeral(shouldHideResponse()).queue();
	}

	public void reply(EmbedBuilder embedBuilder) {
		event.replyEmbeds(embedBuilder.build()).setEphemeral(shouldHideResponse()).queue();
	}

	public void replyWithComponents(String content, ActionComponent... components) {
		event.reply(content).addActionRows(partitionOf(components)).queue();
	}

	public void replyWithComponents(EmbedBuilder embedBuilder, ActionComponent... components) {
		event.replyEmbeds(embedBuilder.build()).addActionRows(partitionOf(components)).queue();
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

	public void sendFollowUpWithComponents(String content, ActionComponent... components) {
		event.getHook().sendMessage(content).addActionRows(partitionOf(components)).queue();
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
		event.deferReply(ephemeral).queue(); // JDA handles this async defer action, no need to wait
		runnable.run();
	}

	// utils

	public boolean hasSelfPermission(Permission... permissions) {
		return getGuild().getSelfMember().hasPermission(permissions);
	}

	public boolean hasSelfChannelPermissions(GuildChannel channel, Permission... permissions) {
		return getGuild().getSelfMember().hasPermission(channel, permissions);
	}
}