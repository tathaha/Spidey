package dev.mlnr.spidey.events;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.objects.Paginator;
import dev.mlnr.spidey.objects.Emojis;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PaginatorEvent extends ListenerAdapter {
	private final Cache cache;

	public PaginatorEvent(Cache cache) {
		this.cache = cache;
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		event.deferEdit().queue();

		var paginatorCache = cache.getPaginatorCache();
		var button = event.getComponent();
		var paginatorId = button.getId().split(":")[0];
		if (!paginatorCache.isPaginator(paginatorId)) {
			return;
		}
		var paginator = paginatorCache.getPaginator(paginatorId);
		if (event.getUser().getIdLong() != paginator.getAuthorId()) {
			return;
		}
		var emoji = button.getEmoji();
		Paginator.Action move;
		switch (emoji.getName()) {
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
		paginator.switchPage(move);
	}
}