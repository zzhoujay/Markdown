package com.zzhoujay.markdown.parser;

import android.text.SpannableStringBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhou on 16-6-25.
 */
public class MarkDownParser {

    private static final Pattern patternH1 = Pattern.compile("#\\s+(.*)");
    private static final Pattern patternH2 = Pattern.compile("#{2}\\s+(.*)");
    private static final Pattern patternH3 = Pattern.compile("#{3}\\s+(.*)");
    private static final Pattern patternH4 = Pattern.compile("#{4}\\s+(.*)");
    private static final Pattern patternH5 = Pattern.compile("#{5}\\s+(.*)");
    private static final Pattern patternH6 = Pattern.compile("#{6}\\s+(.*)");

    private static final Pattern patternQuota = Pattern.compile("\\s{0,3}>\\s+(.*)");
    private static final Pattern patternUl = Pattern.compile("\\s{0,3}[*+-]\\s+(.*)");
    private static final Pattern patternOl = Pattern.compile("\\s{0,3}\\d+\\.\\s+(.*)");

    private static final Pattern patternItalic = Pattern.compile("[^*^_]*(([*_])([^*_].*?)\\2)");
    private static final Pattern patternEm = Pattern.compile("[^*_]*(([*_])\\2([^*_].*?)\\2\\2)");
    private static final Pattern patternEmItalic = Pattern.compile("[^*_]*(([*_])\\2\\2([^*_].*?)\\2\\2\\2)");
    private static final Pattern patternDelete = Pattern.compile("[^~]*((~{2,4})([^~].*?)\\2)");
    private static final Pattern patternCode = Pattern.compile("[^`]*((`+)([^`].*?)\\2)");

    private static final Pattern patternLink = Pattern.compile(".*?(\\[\\s*(.*?)\\s*]\\(\\s*(\\S*)(\\s+(['\"])(.*?)\\5)?\\s*?\\))");
    private static final Pattern patternImage = Pattern.compile(".*?(!\\[\\s*(.*?)\\s*]\\(\\s*(\\S*)(\\s+(['\"])(.*?)\\5)?\\s*?\\))");
    private static final Pattern patternLink2 = Pattern.compile(".*?(\\[\\s*(.*?)\\s*]\\s*\\[\\s*(.*?)\\s*])");
    private static final Pattern patternLink2Link = Pattern.compile("\\s*\\[\\s*(.*?)\\s*]:\\s*(\\S+?)(\\s+(['\"])(.*?)\\4)?\\s*");
    private static final Pattern patternImage2 = Pattern.compile(".*?(!\\[\\s*(.*?)\\s*]\\s*\\[\\s*(.*?)\\s*])");
    private static final Pattern patternImage2Link = Pattern.compile("\\s*!\\[\\s*(.*?)\\s*]:\\s*(\\S+?)(\\s+(['\"])(.*?)\\4)?\\s*");

    private static final Pattern patternEmail = Pattern.compile(".*?(<(\\S+@\\S+\\.\\S+)>).*?");
    private static final Pattern patternAutoLink = Pattern.compile("https?://\\S+?");

    private static final Pattern patternEndSpace = Pattern.compile("(.*?) {2} *$");
    private static final Pattern patternInlineSpace = Pattern.compile("\\S*(\\s+)\\S+");

    private static final Pattern patternCodeBlock = Pattern.compile("[( {4})\\t](.*)");
    private static final Pattern patternCodeBlock2 = Pattern.compile("^\\s*```");

    private BufferedReader reader;
    private StyleBuilder styleBuilder;

    public MarkDownParser(BufferedReader reader) {
        this.reader = reader;
        styleBuilder = new StyleBuilderImpl();
    }

    public MarkDownParser(InputStream inputStream) {
        this(new BufferedReader(new InputStreamReader(inputStream)));
    }

    public MarkDownParser(String text) {
        this(new BufferedReader(new StringReader(text)));
    }

    public LinkedList<Line> praser() throws IOException {
        String l;
        int lineCount = 0;
        boolean block2 = false;
        LinkedList<Line> lines = new LinkedList<>();
        while ((l = reader.readLine()) != null) {
            Line line = new Line(lineCount++, l);
            lines.add(line);
            if (!block2 && findCodeBlock(line)) {
                continue;
            }

            if (findCodeBlock2(line)) {
                block2 = !block2;
                lineCount--;
                lines.removeLast();
                continue;
            }

            if (block2) {
                line.setBuilder(styleBuilder.codeBlock(l));
                continue;
            }

            if (findQuota(line)) {
                continue;
            }

            if (findUl(line)) {
                continue;
            }

            if (findOl(line)) {
                continue;
            }

            if (findH(line)) {
                continue;
            }

            SpannableStringBuilder builder = new SpannableStringBuilder(l);
            line.setBuilder(findInline(builder));
        }
        return lines;
    }

    private boolean findCodeBlock2(Line line) {
        Matcher matcher = patternCodeBlock2.matcher(line.getSource());
        return matcher.find();
    }

    private boolean findCodeBlock(Line line) {
        Matcher matcher = patternCodeBlock.matcher(line.getSource());
        if (matcher.find()) {
            String content = matcher.group(1);
            line.setCodeBlock(true);
            line.setBuilder(styleBuilder.codeBlock(content));
            return true;
        }
        return false;
    }

    private boolean findH(Line line) {
        return findH1(line) || findH2(line) || findH3(line) || findH4(line) || findH5(line) || findH6(line);
    }

    private boolean findH1(Line line) {
        Matcher matcher = patternH1.matcher(line.getSource());
        if (matcher.find()) {
            String content = matcher.group(1);
            SpannableStringBuilder builder = new SpannableStringBuilder(content);
            findInline(builder);
            line.setBuilder(styleBuilder.h1(builder));
            return true;
        }
        return false;
    }

    private boolean findH2(Line line) {
        Matcher matcher = patternH2.matcher(line.getSource());
        if (matcher.find()) {
            String content = matcher.group(1);
            SpannableStringBuilder builder = new SpannableStringBuilder(content);
            findInline(builder);
            line.setBuilder(styleBuilder.h2(builder));
            return true;
        }
        return false;
    }

    private boolean findH3(Line line) {
        Matcher matcher = patternH3.matcher(line.getSource());
        if (matcher.find()) {
            String content = matcher.group(1);
            SpannableStringBuilder builder = new SpannableStringBuilder(content);
            findInline(builder);
            line.setBuilder(styleBuilder.h3(builder));
            return true;
        }
        return false;
    }

    private boolean findH4(Line line) {
        Matcher matcher = patternH4.matcher(line.getSource());
        if (matcher.find()) {
            String content = matcher.group(1);
            SpannableStringBuilder builder = new SpannableStringBuilder(content);
            findInline(builder);
            line.setBuilder(styleBuilder.h4(builder));
            return true;
        }
        return false;
    }

    private boolean findH5(Line line) {
        Matcher matcher = patternH5.matcher(line.getSource());
        if (matcher.find()) {
            String content = matcher.group(1);
            SpannableStringBuilder builder = new SpannableStringBuilder(content);
            findInline(builder);
            line.setBuilder(styleBuilder.h5(builder));
            return true;
        }
        return false;
    }

    private boolean findH6(Line line) {
        Matcher matcher = patternH6.matcher(line.getSource());
        if (matcher.find()) {
            String content = matcher.group(1);
            SpannableStringBuilder builder = new SpannableStringBuilder(content);
            findInline(builder);
            line.setBuilder(styleBuilder.h6(builder));
            return true;
        }
        return false;
    }

    private SpannableStringBuilder findInline(SpannableStringBuilder builder) {
        findEmItalic(builder);
        findEm(builder);
        findItalic(builder);
        findDelete(builder);
        findCode(builder);
        findEmail(builder);
        findAutoLink(builder);
        findLink(builder);
        findImage(builder);
        return builder;
    }

    private SpannableStringBuilder findEm(SpannableStringBuilder c) {
        Matcher matcher = patternEm.matcher(c);
        while (matcher.find()) {
            String content = matcher.group(3);
            c.delete(matcher.start(1), matcher.end(1));
            c.insert(matcher.start(1), styleBuilder.em(content));
        }
        return c;
    }

    private SpannableStringBuilder findItalic(SpannableStringBuilder builder) {
        Matcher matcher = patternItalic.matcher(builder);
        while (matcher.find()) {
            String content = matcher.group(3);
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.italic(content));
        }
        return builder;
    }

    private SpannableStringBuilder findEmItalic(SpannableStringBuilder builder) {
        Matcher matcher = patternEmItalic.matcher(builder);
        while (matcher.find()) {
            String content = matcher.group(3);
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.emItalic(content));
        }
        return builder;
    }

    private SpannableStringBuilder findCode(SpannableStringBuilder builder) {
        Matcher matcher = patternCode.matcher(builder);
        while (matcher.find()) {
            String content = matcher.group(3);
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.code(content));
        }
        return builder;
    }

    private SpannableStringBuilder findDelete(SpannableStringBuilder builder) {
        Matcher matcher = patternDelete.matcher(builder);
        while (matcher.find()) {
            String content = matcher.group(3);
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.delete(content));
        }
        return builder;
    }

    private SpannableStringBuilder findEmail(SpannableStringBuilder builder) {
        Matcher matcher = patternEmail.matcher(builder);
        while (matcher.find()) {
            String content = matcher.group(2);
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.email(content));
        }
        return builder;
    }

    private SpannableStringBuilder findAutoLink(SpannableStringBuilder builder) {
        Matcher matcher = patternAutoLink.matcher(builder);
        while (matcher.find()) {
            String content = matcher.group();
            builder.delete(matcher.start(), matcher.end());
            builder.insert(matcher.start(), styleBuilder.link(content, content, ""));
        }
        return builder;
    }

    private SpannableStringBuilder findLink(SpannableStringBuilder builder) {
        Matcher matcher = patternLink.matcher(builder);
        while (matcher.find()) {
            String title = matcher.group(2);
            String link = matcher.group(4);
            String hint = matcher.group(6);
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.link(title, link, hint));
        }
        return builder;
    }

    private SpannableStringBuilder findImage(SpannableStringBuilder builder) {
        Matcher matcher = patternImage.matcher(builder);
        while (matcher.find()) {
            String title = matcher.group(2);
            String link = matcher.group(4);
            String hint = matcher.group(6);
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.image(title, link, hint));
        }
        return builder;
    }

    private boolean findQuota(Line line) {
        Matcher matcher = patternQuota.matcher(line.getSource());
        if (matcher.find()) {
            Line line1 = new Line(line.lineNum, matcher.group(1));
            CharSequence userText;
            if (findH(line1)) {
                userText = line1.getBuilder();
            } else {
                userText = line1.getSource();
            }
            SpannableStringBuilder builder;
            if (userText instanceof SpannableStringBuilder) {
                builder = (SpannableStringBuilder) userText;
            } else {
                builder = new SpannableStringBuilder(userText);
            }
            findInline(builder);
            line.setBuilder(styleBuilder.quota(builder));
            return true;
        }
        return false;
    }

    private boolean findUl(Line line) {
        Matcher matcher = patternUl.matcher(line.getSource());
        if (matcher.find()) {
            Line line1 = new Line(line.lineNum, matcher.group(1));
            CharSequence userText;
            if (findH(line1)) {
                userText = line1.getBuilder();
            } else {
                userText = line1.getSource();
            }
            SpannableStringBuilder builder;
            if (userText instanceof SpannableStringBuilder) {
                builder = (SpannableStringBuilder) userText;
            } else {
                builder = new SpannableStringBuilder(userText);
            }
            findInline(builder);
            line.setBuilder(styleBuilder.ul(builder));
            return true;
        }
        return false;
    }

    private boolean findOl(Line line) {
        Matcher matcher = patternOl.matcher(line.getSource());
        if (matcher.find()) {
            Line line1 = new Line(line.lineNum, matcher.group(1));
            CharSequence userText;
            if (findH(line1)) {
                userText = line1.getBuilder();
            } else {
                userText = line1.getSource();
            }
            SpannableStringBuilder builder;
            if (userText instanceof SpannableStringBuilder) {
                builder = (SpannableStringBuilder) userText;
            } else {
                builder = new SpannableStringBuilder(userText);
            }
            findInline(builder);
            line.setBuilder(styleBuilder.ol(builder));
            return true;
        }
        return false;
    }

}
