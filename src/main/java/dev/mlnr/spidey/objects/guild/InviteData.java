package dev.mlnr.spidey.objects.guild;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;

public class InviteData {
	private final long guildId;
	private int uses;

	public InviteData(Invite invite, Guild guild) {
		this.guildId = guild.getIdLong();
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