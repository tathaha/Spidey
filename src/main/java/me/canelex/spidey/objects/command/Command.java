package me.canelex.spidey.objects.command;

import me.canelex.jda.api.Permission;
import me.canelex.jda.api.entities.Message;

public abstract class Command
{
    private final String invoke;
    private final String[] aliases;
    private final String description;
    private final String usage;
    private final Category category;
    private final Permission requiredPermission;
    private final int maxArgs;

    public Command(final String invoke, final String[] aliases, final String description, final String usage, final Category category, final Permission requiredPermission,
                   final int maxArgs)
    {
        this.invoke = invoke;
        this.aliases = aliases;
        this.description = description;
        this.usage = usage;
        this.category = category;
        this.requiredPermission = requiredPermission;
        this.maxArgs = maxArgs;
    }

    public abstract void execute(final String[] args, final Message msg);

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
}