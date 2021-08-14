package io.gitlab.arkdirfe.boxedvillagers.data;

import io.gitlab.arkdirfe.boxedvillagers.util.StringFormatter;
import io.gitlab.arkdirfe.boxedvillagers.util.StringRef;
import io.gitlab.arkdirfe.boxedvillagers.util.StringUtil;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes a single help page.
 *
 * @param title   Title of the help page.
 * @param content Content of the help page.
 */
public record HelpData(String title, String content)
{
    /**
     * Formats the help page to be printed out in chat.
     *
     * @param lineWidth How many standard symbols wide the message should be before a line wraps.
     *
     * @return An array of lines.
     */
    @NotNull
    public String[] getFormatted(final int lineWidth)
    {
        List<String> lines = new ArrayList<>();
        
        lines.add("");
        lines.add(Strings.get(StringRef.FORMAT_HELP_COLOR) + "=".repeat(lineWidth));
        lines.add(Strings.get(StringRef.FORMAT_HELP_COLOR) + getCenterPadded(title, lineWidth * StringUtil.DEFAULT_CHARACTER_WIDTH));
        lines.add(Strings.get(StringRef.FORMAT_HELP_COLOR) + "=".repeat(lineWidth));
        lines.addAll(getContent(lineWidth * StringUtil.DEFAULT_CHARACTER_WIDTH));
        lines.add(Strings.get(StringRef.FORMAT_HELP_COLOR) + "-".repeat(lineWidth));
        
        return StringFormatter.formatAll(lines).toArray(new String[0]);
    }
    
    /**
     * Formats the content to fit.
     *
     * @param lineWidth How many standard symbols wide the message should be before a line wraps.
     *
     * @return List of lines.
     */
    @NotNull
    private List<String> getContent(final int lineWidth)
    {
        List<String> lines = new ArrayList<>();
        
        StringBuilder line = new StringBuilder();
        StringBuilder word = new StringBuilder();
        
        for(int i = 0; i < content.length(); i++)
        {
            char c = content.charAt(i);
            
            if(c == ' ')
            {
                if(StringUtil.stringWidth(line.toString()) + StringUtil.stringWidth(word.toString()) > lineWidth)
                {
                    lines.add(line.toString());
                    line = new StringBuilder();
                }
                
                line.append(word).append(" ");
                word = new StringBuilder();
            }
            else if(c == '\n')
            {
                line.append(word).append(" ");
                word = new StringBuilder();
                lines.add(line.toString());
                line = new StringBuilder();
            }
            else
            {
                word.append(c);
            }
        }
        
        line.append(word);
        lines.add(line.toString());
        
        return lines;
    }
    
    /**
     * Pads a string to be as centered as possible.
     *
     * @param string      The string to pad.
     * @param targetWidth How wide the line is.
     *
     * @return The padded string.
     */
    @NotNull
    private String getCenterPadded(@NotNull final String string, final int targetWidth)
    {
        int width = StringUtil.stringWidth(string);
        return " ".repeat((targetWidth - width) / (2 * 4)) + string;
    }
}
