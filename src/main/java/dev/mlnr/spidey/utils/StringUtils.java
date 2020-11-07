package dev.mlnr.spidey.utils;

import dev.mlnr.spidey.handlers.command.CommandHandler;

import static java.lang.Math.min;

public class StringUtils
{
    private StringUtils() {}

    public static String pluralize(final long size, final String base)
    {
        if (size == 1)
            return "1 " + base;
        return size + " " + base + "s";
    }

    public static String getSimilarCommand(final String command)
    {
        return CommandHandler.getCommands().keySet().stream().filter(invoke -> getSimilarity(invoke, command) > 0.5).findFirst().orElse(null);
    }

    private static double getSimilarity(final String s1, final String s2)
    {
        var longer = s1;
        var shorter = s2;
        if (s1.length() < s2.length())
        {
            longer = s2;
            shorter = s1;
        }
        final var longerLength = longer.length();
        if (longerLength == 0)
            return 1.0;
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    private static int editDistance(String s1, String s2)
    {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        final var costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++)
        {
            var lastValue = i;
            for (var j = 0; j <= s2.length(); j++)
            {
                if (i == 0)
                {
                    costs[j] = j;
                    continue;
                }
                if (j <= 0)
                    continue;
                var newValue = costs[j - 1];
                if (s1.charAt(i - 1) != s2.charAt(j - 1))
                    newValue = min(min(newValue, lastValue), costs[j]) + 1;
                costs[j - 1] = lastValue;
                lastValue = newValue;
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }
}