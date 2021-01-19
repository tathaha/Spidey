package dev.mlnr.spidey.objects.guild;

import dev.mlnr.spidey.DatabaseManager;
import dev.mlnr.spidey.objects.I18n;

public class GuildSettings
{
    private final long guildId;

    private long logChannelId;
    private long joinRoleId;
    private String prefix;
    private I18n i18n;

    private boolean snipingEnabled;
    private boolean vip;

    private long djRoleId;
    private boolean segmentSkippingEnabled;
    private int defaultVolume;

    private boolean fairQueueEnabled;
    private int fairQueueThreshold;

    public GuildSettings(long guildId, long logChannelId, long joinRoleId, String prefix, String language, boolean snipingEnabled, boolean vip, long djRoleId,
                         boolean segmentSkippingEnabled, int defaultVolume, boolean fairQueueEnabled, int fairQueueThreshold)
    {
        this.guildId = guildId;

        this.logChannelId = logChannelId;
        this.joinRoleId = joinRoleId;
        this.prefix = prefix;
        this.i18n = I18n.ofLanguage(language);

        this.snipingEnabled = snipingEnabled;
        this.vip = vip;

        this.djRoleId = djRoleId;
        this.segmentSkippingEnabled = segmentSkippingEnabled;
        this.defaultVolume = defaultVolume;

        this.fairQueueEnabled = fairQueueEnabled;
        this.fairQueueThreshold = fairQueueThreshold;
    }

    // getters

    public long getLogChannelId()
    {
        return this.logChannelId;
    }

    public long getJoinRoleId()
    {
        return this.joinRoleId;
    }

    public String getPrefix()
    {
        return this.prefix;
    }

    public I18n getI18n()
    {
        return this.i18n;
    }

    public boolean isSnipingEnabled()
    {
        return this.snipingEnabled;
    }

    public boolean isVip()
    {
        return this.vip;
    }

    // music getters

    public long getDjRoleId()
    {
        return this.djRoleId;
    }

    public boolean isSegmentSkippingEnabled()
    {
        return this.segmentSkippingEnabled;
    }

    public int getDefaultVolume()
    {
        return this.defaultVolume;
    }

    // fair queue getters

    public boolean isFairQueueEnabled()
    {
        return this.fairQueueEnabled;
    }

    public int getFairQueueThreshold()
    {
        return this.fairQueueThreshold;
    }

    // setters

    public void setLogChannelId(long logChannelId)
    {
        this.logChannelId = logChannelId;
        DatabaseManager.setLogChannelId(guildId, logChannelId);
    }

    public void setJoinRoleId(long joinRoleId)
    {
        this.joinRoleId = joinRoleId;
        DatabaseManager.setJoinRoleId(guildId, joinRoleId);
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
        DatabaseManager.setPrefix(guildId, prefix);
    }

    public void setLanguage(String language)
    {
        this.i18n = I18n.ofLanguage(language);
        DatabaseManager.setLanguage(guildId, language);
    }

    public void setSnipingEnabled(boolean enabled)
    {
        this.snipingEnabled = enabled;
        DatabaseManager.setSnipingEnabled(guildId, enabled);
    }

    public void setVip(boolean vip)
    {
        this.vip = vip;
        DatabaseManager.setVip(guildId, vip);
    }

    // music setters

    public void setDjRoleId(long djRoleId)
    {
        this.djRoleId = djRoleId;
        DatabaseManager.setDJRoleId(guildId, djRoleId);
    }

    public void setSegmentSkippingEnabled(boolean enabled)
    {
        this.segmentSkippingEnabled = enabled;
        DatabaseManager.setSegmentSkippingEnabled(guildId, enabled);
    }

    public void setDefaultVolume(int defaultVolume)
    {
        this.defaultVolume = defaultVolume;
        DatabaseManager.setDefaultVolume(guildId, defaultVolume);
    }

    // fair queue setters

    public void setFairQueueEnabled(boolean enabled)
    {
        this.fairQueueEnabled = enabled;
        DatabaseManager.setFairQueueEnabled(guildId, enabled);
    }

    public void setFairQueueThreshold(int threshold)
    {
        this.fairQueueThreshold = threshold;
        DatabaseManager.setFairQueueThreshold(guildId, threshold);
    }
}