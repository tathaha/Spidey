package dev.mlnr.spidey.objects.invites;

import net.dv8tion.jda.api.entities.Invite;

public class WrappedInvite
{
    private final long guildId;
    private int uses;

    public WrappedInvite(final Invite invite)
    {
        this.guildId = invite.getGuild().getIdLong();
        this.uses = invite.getUses();
    }

    public long getGuildId()
    {
        return guildId;
    }

    public int getUses()
    {
        return uses;
    }

    public void incrementUses()
    {
        this.uses++;
    }
}