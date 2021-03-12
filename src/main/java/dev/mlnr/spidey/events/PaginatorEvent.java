package dev.mlnr.spidey.events;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.objects.Paginator;
import dev.mlnr.spidey.utils.Emojis;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PaginatorEvent extends ListenerAdapter {
	private final Cache cache;

	public PaginatorEvent(Cache cache) {
		this.cache = cache;
	}

	@Override
	public void onGenericGuildMessageReaction(GenericGuildMessageReactionEvent event) {
		var paginatorCache = cache.getPaginatorCache();
		var messageId = event.getMessageIdLong();
		if (!paginatorCache.isPaginator(messageId)) {
			return;
		}
		var paginator = paginatorCache.getPaginator(messageId);
		if (event.getUserIdLong() != paginator.getAuthorId()) {
			return;
		}
		var reactionEmote = event.getReactionEmote();
		if (!reactionEmote.isEmoji()) {
			return;
		}
		Paginator.Action move;
		switch (reactionEmote.getEmoji()) {
			case Emojis.BACKWARDS:
				move = Paginator.Action.BACKWARDS;
				break;
			case Emojis.FORWARD:
				move = Paginator.Action.FORWARD;
				break;
			case Emojis.WASTEBASKET:
				move = Paginator.Action.REMOVE;
				break;
			default:
				return;
		}
		paginator.switchPage(move, event.getChannel());
	}
}