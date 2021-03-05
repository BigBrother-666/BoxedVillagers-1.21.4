package io.gitlab.arkdirfe.boxedvillagers.util;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringFormatter
{
    private StringFormatter()
    {
    }

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    private static final Map<String, String> colorPlaceholders = Map.ofEntries(
            // Tooltip Colors
            new AbstractMap.SimpleImmutableEntry<>("<norm>", "#ffffff"), // Normal Text
            new AbstractMap.SimpleImmutableEntry<>("<info>", "#00b7ff"), // Info
            new AbstractMap.SimpleImmutableEntry<>("<basic>", "#55ff55"), // Basic item
            new AbstractMap.SimpleImmutableEntry<>("<advanced>", "#00ff00"), // Advanced item
            new AbstractMap.SimpleImmutableEntry<>("<item>", "#8bff8b"), // Item in tooltip
            new AbstractMap.SimpleImmutableEntry<>("<warn>", "#ff0000"), // Warning
            new AbstractMap.SimpleImmutableEntry<>("<evil>", "#990000"), // Evil
            new AbstractMap.SimpleImmutableEntry<>("<static>", "#ffb300"), // Static Number (costs, caps for cures/slots)
            new AbstractMap.SimpleImmutableEntry<>("<dynamic>", "#ffce52"), // Dynamic Number (current values)
            new AbstractMap.SimpleImmutableEntry<>("<title>", "#008100"), // Title
            new AbstractMap.SimpleImmutableEntry<>("<money>", "#ffdc00"), // Money
            new AbstractMap.SimpleImmutableEntry<>("<crystals>", "#008eff"), // Crystals
            new AbstractMap.SimpleImmutableEntry<>("<enchant>", "#b52fff") // Heading
    );

    /**
     * Replaces the color indicators with their hex color.
     *
     * @param string The string to replace in.
     * @return String with replaced indicators.
     */
    @NotNull
    private static String replaceIndicators(@NotNull String string)
    {
        for(Map.Entry<String, String> e : colorPlaceholders.entrySet())
        {
            string = string.replace(e.getKey(), e.getValue());

        }
        return string;
    }

    /**
     * Formats the hex codes.
     *
     * @param string String with hex codes.
     * @return Formatted string.
     */
    @NotNull
    private static String formatHex(@NotNull String string)
    {
        Matcher match = pattern.matcher(string);

        while(match.find())
        {
            String color = string.substring(match.start(), match.end());
            string = string.replace(color, ChatColor.of(color) + "");
            match = pattern.matcher(string);
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * Formats a single line.
     *
     * @param line The line to format.
     * @return Formatted line.
     */
    @NotNull
    public static String formatLine(@NotNull final String line)
    {
        return "§r" + formatHex(replaceIndicators("<norm>" + line));
    }

    /**
     * Splits a string at \n and formats the lines.
     *
     * @param string String to process.
     * @return List of lines.
     */
    @NotNull
    public static List<String> splitAndFormatLines(@NotNull final String string)
    {
        List<String> lines = new ArrayList<>();

        for(String s : string.split("\n"))
        {
            lines.add(formatLine(s));
        }

        return lines;
    }

    /**
     * Formats a list of lines.
     *
     * @param lines The lines to format.
     * @return Formatted lines.
     */
    @NotNull
    public static List<String> formatAll(@NotNull final List<String> lines)
    {
        List<String> out = new ArrayList<>();
        for(String s : lines)
        {
            out.add(formatLine(s));
        }
        return out;
    }

    /**
     * Splits along \n.
     *
     * @param string The string to split.
     * @return List of lines.
     */
    @NotNull
    public static List<String> split(@NotNull final String string)
    {
        return Arrays.asList(string.split("\n"));
    }
}
