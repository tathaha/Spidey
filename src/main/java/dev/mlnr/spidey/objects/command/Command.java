package dev.mlnr.spidey.objects.command;

import net.dv8tion.jda.api.Permission;

public abstract class Command
{
    private final String invoke;
    private final String[] aliases;
    private final String description;
    private final String usage;
    private final Category category;
    private final Permission requiredPermission;
    private final int maxArgs;
    private final int cooldown;

    protected Command(final String invoke, final String[] aliases, final String description, final String usage, final Category category, final Permission requiredPermission, final int maxArgs, final int cooldown)
    {
        this.invoke = invoke;
        this.aliases = aliases;
        this.description = description;
        this.usage = usage;
        this.category = category;
        this.requiredPermission = requiredPermission;
        this.maxArgs = maxArgs;
        this.cooldown = cooldown;
    }

    public abstract void execute(final String[] args, final CommandContext context);

    public String getInvoke()
    {
        return this.invoke;
    }

    public String[] getAliases()
    {
        return this.aliases;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getUsage()
    {
        return this.usage;
    }

    public Category getCategory()
    {
        return this.category;
    }

    public Permission getRequiredPermission()
    {
        return this.requiredPermission;
    }

    public int getMaxArgs()
    {
        return this.maxArgs;
    }

    public int getCooldown()
    {
        return this.cooldown;
    }
}