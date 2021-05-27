package dev.mlnr.spidey.objects.akinator;

import dev.mlnr.spidey.cache.AkinatorCache;
import dev.mlnr.spidey.objects.I18n;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class AkinatorContext {
	private final SlashCommandEvent event;

	private final AkinatorCache akinatorCache;
	private final I18n i18n;

	public AkinatorContext(SlashCommandEvent event, AkinatorCache akinatorCache, I18n i18n) {
		this.event = event;

		this.akinatorCache = akinatorCache;
		this.i18n = i18n;
	}

	public TextChannel getChannel() {
		return event.getTextChannel();
	}

	public AkinatorCache getAkinatorCache() {
		return akinatorCache;
	}

	public I18n getI18n() {
		return i18n;
	}
}