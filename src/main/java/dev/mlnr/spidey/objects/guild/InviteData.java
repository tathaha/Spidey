package dev.mlnr.spidey.objects.guild;

import net.dv8tion.jda.api.entities.Invite;

public class InviteData {
	private final long guildId;
	private int uses;

	public InviteData(Invite invite) {
		var guild = invite.getGuild();
		this.guildId = guild == null ? -1 : guild.getIdLong(); // TODO remove this once jda releases support for stage channels
		this.uses = invite.getUses();
	}

	public long getGuildId() {
		return this.guildId;
	}

	public int getUses() {
		return this.uses;
	}

	public void incrementUses() {
		this.uses++;
	}
}