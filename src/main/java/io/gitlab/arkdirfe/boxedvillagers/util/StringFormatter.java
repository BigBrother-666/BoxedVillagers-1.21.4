package io.gitlab.arkdirfe.boxedvillagers.util;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringFormatter
{
    private static final Pattern hexPattern = Pattern.compile("#[a-fA-F0-9]{6}");
    private static final Pattern formatPattern = Pattern.compile("§[a-f0-9klmnorx]");
    private static Map<String, String> colorCodes = null;

    private StringFormatter()
    {
    }

    /**
     * Initializes the default color map.
     */
    public static void restoreDefaultColors()
    {
        colorCodes = new HashMap<>();
        colorCodes.put("<norm>", "#ffffff"); // Normal Text
        colorCodes.put("<uiheader>", "#3f3f3f"); // UI Header
        colorCodes.put("<info>", "#00b7ff"); // Info
        colorCodes.put("<basic>", "#55ff55"); // Basic item
        colorCodes.put("<advanced>", "#00ff00"); // Advanced item
        colorCodes.put("<item>", "#8bff8b"); // Item in tooltip
        colorCodes.put("<warn>", "#ff0000"); // Warning
        colorCodes.put("<evil>", "#990000"); // Evil
        colorCodes.put("<static>", "#ffb300"); // Static Number (costs, caps for cures/slots)
        colorCodes.put("<dynamic>", "#ffce52"); // Dynamic Number (current values)
        colorCodes.put("<title>", "#008100"); // Title
        colorCodes.put("<money>", "#ffdc00"); // Money
        colorCodes.put("<crystals>", "#008eff"); // Crystals
        colorCodes.put("<enchant>", "#b52fff"); // Heading
    }

    /**
     * Sets a value on the color map if it is correctly formatted.
     *
     * @param key   Which value to set.
     * @param value The new value (needs to be in hex format (#xxxxxx).
     * @return True if the value was properly formatted and not new, false otherwise.
     */
    public static boolean setColor(@NotNull final String key, @NotNull final String value)
    {
        if(!colorCodes.containsKey(key) || !hexPattern.matcher(value).matches())
        {
            return false;
        }

        colorCodes.put(key, value);
        return true;
    }

    /**
     * Strips all formatting symbols from a string for accurate length calculation.
     *
     * @param string The string to process.
     * @return The string without formatting symbols.
     */
    @NotNull
    public static String stripFormatting(@NotNull String string)
    {
        for(Map.Entry<String, String> e : colorCodes.entrySet())
        {
            string = string.replace(e.getKey(), "");
        }

        Matcher match = formatPattern.matcher(string);
        while(match.find())
        {
            String found = string.substring(match.start(), match.end());
            string = string.replace(found, "");
            match = formatPattern.matcher(string);
        }

        return string;
    }

    /**
     * Replaces the color indicators with their hex color.
     *
     * @param string The string to replace in.
     * @return String with replaced indicators.
     */
    @NotNull
    private static String replaceIndicators(@NotNull String string)
    {
        for(Map.Entry<String, String> e : colorCodes.entrySet())
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
        Matcher match = hexPattern.matcher(string);

        while(match.find())
        {
            String color = string.substring(match.start(), match.end());
            string = string.replace(color, ChatColor.of(color) + "");
            match = hexPattern.matcher(string);
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
        return "§r" + formatHex(replaceIndicators(Strings.get(StringRef.FORMAT_DEFAULT_COLOR) + line));
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
     * Splits strings along \n.
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
