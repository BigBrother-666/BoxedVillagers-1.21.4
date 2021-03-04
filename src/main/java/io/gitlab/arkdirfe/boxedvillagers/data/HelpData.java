package io.gitlab.arkdirfe.boxedvillagers.data;

import io.gitlab.arkdirfe.boxedvillagers.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HelpData
{
    private final String title;
    private final String content;

    public HelpData(final String title, final String content)
    {
        this.title = title;
        this.content = content;
    }

    @NotNull
    public String[] getFormatted(final int lineWidth)
    {
        List<String> lines = new ArrayList<>();

        lines.add("");
        lines.add("§e" + "=".repeat(lineWidth));
        lines.add("§e" + getCenterPadded(title, lineWidth * StringUtil.defaultCharacterWidth));
        lines.add("§e" + "=".repeat(lineWidth));
        lines.addAll(getContent(lineWidth * StringUtil.defaultCharacterWidth));
        lines.add("§e" + "-".repeat(lineWidth));

        System.out.println("Title Length " + title.length());
        System.out.println("Difference " + (lineWidth - title.length()));

        return lines.toArray(new String[0]);
    }

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
                line.append(word.toString()).append(" ");
                word = new StringBuilder();
            }
            else
            {
                word.append(c);
            }

            if(StringUtil.stringWidth(line.toString()) + StringUtil.stringWidth(word.toString()) > lineWidth)
            {
                lines.add(line.toString());
                line = new StringBuilder();
            }
        }

        line.append(word.toString());
        lines.add(line.toString());

        return lines;
    }

    @NotNull
    private String getCenterPadded(@NotNull final String string, final int targetWidth)
    {
        int width = StringUtil.stringWidth(string);
        return " ".repeat((targetWidth - width) / (2 * 4)) + string;
    }
}
