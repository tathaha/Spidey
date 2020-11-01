package dev.mlnr.spidey.objects.invites;

import net.dv8tion.jda.api.entities.Invite;

public class InviteData
{
    private final long guildId;
    private int uses;

    public InviteData(final Invite invite)
    {
        this.guildId = invite.getGuild().getIdLong();
        this.uses = invite.getUses();
    }

    public long getGuildId()
    {
        return this.guildId;
    }

    public int getUses()
    {
        return this.uses;
    }

    public void incrementUses()
    {
        this.uses++;
    }
}