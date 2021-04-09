package dev.mlnr.spidey.objects.messages;

import net.dv8tion.jda.api.entities.Message;

import java.time.OffsetDateTime;

public class MessageData {
	private final long authorId;
	private final OffsetDateTime creation;
	private final String content;
	private final long guildId;
	private final String jumpUrl;

	public MessageData(Message message) {
		this.authorId = message.getAuthor().getIdLong();
		this.creation = message.getTimeCreated();
		this.content = message.getContentRaw();
		this.guildId = message.getGuild().getIdLong();
		this.jumpUrl = message.getJumpUrl();
	}

	public long getAuthorId() {
		return this.authorId;
	}

	public OffsetDateTime getCreation() {
		return this.creation;
	}

	public String getContent() {
		return this.content;
	}

	public long getGuildId() {
		return this.guildId;
	}

	public String getJumpUrl() {
		return this.jumpUrl;
	}
}