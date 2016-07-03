package com.zzhoujay.markdown.parser;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Pair;

import com.zzhoujay.markdown.style.CodeBlockSpan;
import com.zzhoujay.markdown.style.CodeSpan;
import com.zzhoujay.markdown.style.ScaleHeightSpan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhou on 16-6-25.
 */
public class MarkDownParser {

    private static final Pattern patternH = Pattern.compile("^\\s*#{1,6}\\s+(.*)");
    private static final Pattern patternH1 = Pattern.compile("^\\s*#\\s+(.*)");
    private static final Pattern patternH2 = Pattern.compile("^\\s*#{2}\\s+(.*)");
    private static final Pattern patternH3 = Pattern.compile("^\\s*#{3}\\s+(.*)");
    private static final Pattern patternH4 = Pattern.compile("^\\s*#{4}\\s+(.*)");
    private static final Pattern patternH5 = Pattern.compile("^\\s*#{5}\\s+(.*)");
    private static final Pattern patternH6 = Pattern.compile("^\\s*#{6}\\s+(.*)");

    private static final Pattern patternQuota = Pattern.compile("^\\s{0,3}>\\s+(.*)");
    private static final Pattern patternUl = Pattern.compile("^\\s{0,3}[*+-]\\s+(.*)");
    private static final Pattern patternOl = Pattern.compile("^\\s{0,3}\\d+\\.\\s+(.*)");

    private static final Pattern patternItalic = Pattern.compile("[^*^_]*(([*_])([^*_].*?)\\2)");
    private static final Pattern patternEm = Pattern.compile("[^*_]*(([*_])\\2([^*_].*?)\\2\\2)");
    private static final Pattern patternEmItalic = Pattern.compile("[^*_]*(([*_])\\2\\2([^*_].*?)\\2\\2\\2)");
    private static final Pattern patternDelete = Pattern.compile("[^~]*((~{2,4})([^~].*?)\\2)");
    private static final Pattern patternCode = Pattern.compile("[^`]*((`+)([^`].*?)\\2)");

    private static final Pattern patternLink = Pattern.compile(".*?(\\[\\s*(.*?)\\s*]\\(\\s*(\\S*?)(\\s+(['\"])(.*?)\\5)?\\s*?\\))");
    private static final Pattern patternImage = Pattern.compile(".*?(!\\[\\s*(.*?)\\s*]\\(\\s*(\\S*?)(\\s+(['\"])(.*?)\\5)?\\s*?\\))");
    private static final Pattern patternLink2 = Pattern.compile(".*?(\\[\\s*(.*?)\\s*]\\s*\\[\\s*(.*?)\\s*])");
    private static final Pattern patternLink2Link = Pattern.compile("^\\s*\\[\\s*(.*?)\\s*]:\\s*(\\S+?)(\\s+(['\"])(.*?)\\4)?\\s*$");
    private static final Pattern patternImage2 = Pattern.compile(".*?(!\\[\\s*(.*?)\\s*]\\s*\\[\\s*(.*?)\\s*])");
    private static final Pattern patternImage2Link = Pattern.compile("^\\s*!\\[\\s*(.*?)\\s*]:\\s*(\\S+?)(\\s+(['\"])(.*?)\\4)?\\s*$");

    private static final Pattern patternEmail = Pattern.compile(".*?(<(\\S+@\\S+\\.\\S+)>).*?");
    private static final Pattern patternAutoLink = Pattern.compile("https?://\\S+?");

    private static final Pattern patternEndSpace = Pattern.compile("(.*?) {2} *$");
    private static final Pattern patternInlineSpace = Pattern.compile("\\S*(\\s+)\\S+");

    private static final Pattern patternCodeBlock = Pattern.compile("^( {4}|\\t)(.*)");
    private static final Pattern patternCodeBlock2 = Pattern.compile("^\\s*```");

    private static final Pattern patternBlankLine = Pattern.compile("^\\s*$");

    private BufferedReader reader;
    private StyleBuilder styleBuilder;
    private int length;
    private int olIndex;
    private boolean isOl;
    private HashSet<Integer> ignoreLine;
    private HashMap<String, Pair<String, String>> idLinkLinks;
    private HashMap<String, Pair<String, String>> idImageUrl;

    public MarkDownParser(BufferedReader reader, StyleBuilder styleBuilder) {
        this.reader = reader;
        this.styleBuilder = styleBuilder;
        ignoreLine = new HashSet<>();
        idLinkLinks = new HashMap<>();
        idImageUrl = new HashMap<>();
    }

    public MarkDownParser(InputStream inputStream, StyleBuilder styleBuilder) {
        this(new BufferedReader(new InputStreamReader(inputStream)), styleBuilder);
    }

    public MarkDownParser(String text, StyleBuilder styleBuilder) {
        this(new BufferedReader(new StringReader(text)), styleBuilder);
    }

    private void findIds() throws IOException {
        ignoreLine.clear();
        idLinkLinks.clear();
        idImageUrl.clear();
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        int lineNum = -1;
        while ((line = reader.readLine()) != null) {
            lineNum++;
            if (findIdImage(line) || findIdLink(line)) {
                ignoreLine.add(lineNum);
            } else {
                stringBuilder.append(line).append('\n');
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        length = stringBuilder.length();
        System.out.println(stringBuilder.toString());
        reader = new BufferedReader(new StringReader(stringBuilder.toString()));
    }

    public SpannableStringBuilder parser() throws IOException {
        findIds();
        String l;
        int lineCount = 0;
        int lineNum = -1;
        boolean block2 = false;
        ArrayList<Line> lines = new ArrayList<>();
        while ((l = reader.readLine()) != null) {
//            System.out.println(l);
            Line line = new Line(lineCount++, l);
            lines.add(line);
            boolean hasHandle = false;
            if (!block2 && findCodeBlock(line)) {
                removeBlankLine();
                continue;
            }

            if (findCodeBlock2(line)) {
                block2 = !block2;
                lineCount--;
                lines.remove(lines.size() - 1);
                if (!block2) {
                    removeBlankLine();
                }
                continue;
            }

            if (block2) {
                line.setType(Line.LINE_TYPE_CODE_BLOCK_2);
                line.setBuilder(styleBuilder.codeBlock(l, CodeBlockSpan.FLAG_CENTER));
                continue;
            }

            boolean isNewLine = isNewLine(line);
            String currLine = line.getSource();
            while (!isNewLine) {
                reader.mark(length);
                String newLine = reader.readLine();
                System.out.println(newLine);
                if (newLine == null) {
                    break;
                }
                while (TextUtils.isEmpty(newLine) || isBlankLine(newLine)) {
                    reader.mark(length);
                    newLine = reader.readLine();
                    isNewLine = true;
                }
                if (isNewLine) {
                    reader.reset();
                    break;
                }
                if (isCodeBlock(newLine) || isCodeBlock2(newLine) || findUl(newLine) || findOl(newLine) || findH(newLine)) {
                    isNewLine = true;
                    reader.reset();
                } else {
                    int newQuotaCount = findQuota(newLine);
                    int currQuotaCount = findQuota(currLine);
                    if (newQuotaCount > 0 && newQuotaCount > currQuotaCount) {
                        isNewLine = true;
                        reader.reset();
                    } else {
                        String r = newLine;
                        if (newQuotaCount > 0) {
                            r = r.replaceFirst("^\\s{0,3}(>\\s+){" + newQuotaCount + "}", "");
                        }
//                        if (findUl(r)||findOl(r)) {
                        if (findUl(r)) {
                            isNewLine = true;
                            reader.reset();
                        } else {
                            currLine = currLine + ' ' + r;
                        }
                    }
                }
            }
            line.setSource(currLine);

            if (findQuota(line) || findOl(line) || findUl(line) || findH(line)) {
                continue;
            }

            SpannableStringBuilder builder = new SpannableStringBuilder(line.getSource());
            line.setBuilder(handleInline(builder));
        }
        return merge(lines);
    }

    private boolean removeBlankLine() throws IOException {
        reader.mark(length);
        String newLine = reader.readLine();
        System.out.println(newLine);
        if (newLine == null) {
            return true;
        }
        boolean isNewLine = false;
        while (TextUtils.isEmpty(newLine) || isBlankLine(newLine)) {
            reader.mark(length);
            newLine = reader.readLine();
            isNewLine = true;
        }
        if (isNewLine) {
            reader.reset();
            return true;
        }
        return false;

    }

    private SpannableStringBuilder merge(List<Line> lines) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        LineQueue queue = new LineQueue(lines);
        do {
            Line line = queue.get();
            Line prev = queue.prevLine();
            Line next = queue.nextLine();
            switch (line.getType()) {
                case Line.LINE_TYPE_CODE_BLOCK_2:
                    if (prev == null || prev.getType() != Line.LINE_TYPE_CODE_BLOCK_2) {
                        builder.append(styleBuilder.codeBlock(" ", CodeBlockSpan.FLAG_START));
                        builder.append('\n');
                    }
                    break;
                case Line.LINE_TYPE_CODE_BLOCK_1:
                    if (prev == null || prev.getType() != Line.LINE_TYPE_CODE_BLOCK_1) {
                        builder.append(styleBuilder.codeBlock(" ", CodeBlockSpan.FLAG_START));
                        builder.append('\n');
                    }
                    break;
            }
            builder.append(line.getBuilder()).append('\n');
            switch (line.getType()) {
                case Line.LINE_TYPE_QUOTA:
                    if (next != null && next.getType() == Line.LINE_TYPE_QUOTA) {
                        int num = line.getTypeCount();
                        SpannableStringBuilder ssb = new SpannableStringBuilder(" ");
                        while (num > 0) {
                            ssb = styleBuilder.quota(ssb);
                            num--;
                        }
                        ssb.append('\n');
                        builder.append(ssb);
                    } else {
                        builder.append('\n');
                    }
                    break;
                case Line.LINE_TYPE_H3:
                    builder.append(styleBuilder.h3(" "));
                    builder.append('\n');
                    break;
                case Line.LINE_TYPE_H4:
                    builder.append(styleBuilder.h4(" "));
                    builder.append('\n');
                    break;
                case Line.LINE_TYPE_CODE_BLOCK_1:
                    if (next == null || next.getType() != Line.LINE_TYPE_CODE_BLOCK_1) {
                        builder.append(styleBuilder.codeBlock(" ", CodeBlockSpan.FLAG_END));
                        builder.append('\n');
                        builder.append('\n');
                    }
                    break;
                case Line.LINE_TYPE_CODE_BLOCK_2:
                    if (next == null || next.getType() != Line.LINE_TYPE_CODE_BLOCK_2) {
                        builder.append(styleBuilder.codeBlock(" ", CodeBlockSpan.FLAG_END));
                        builder.append('\n');
                        builder.append('\n');
                    }
                    break;
                case Line.LINE_TYPE_H5:
                case Line.LINE_TYPE_H6:
                case Line.LINE_TYPE_H1:
                case Line.LINE_TYPE_H2:
                case Line.LINE_NORMAL:
                    builder.append('\n');
                    break;
                case Line.LINE_TYPE_UL:
                    if (next != null && next.getType() == Line.LINE_TYPE_UL)
                        builder.append(listMarginBottom());
                    builder.append('\n');
                    break;
                case Line.LINE_TYPE_OL:
                    if (next != null && next.getType() == Line.LINE_TYPE_OL) {
                        builder.append(listMarginBottom());
                    }
                    builder.append('\n');
            }
        } while (queue.next());
        return builder;
    }

    private SpannableString listMarginBottom() {
        SpannableString ss = new SpannableString(" ");
        ss.setSpan(new ScaleHeightSpan(0.4f), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    private boolean isBlankLine(String line) {
        Matcher matcher = patternBlankLine.matcher(line);
        return matcher.find();
    }

    private boolean findCodeBlock2(Line line) {
        Matcher matcher = patternCodeBlock2.matcher(line.getSource());
        return matcher.find();
    }

    private boolean findCodeBlock(Line line) {
        Matcher matcher = patternCodeBlock.matcher(line.getSource());
        if (matcher.find()) {
            String content = matcher.group(2);
            line.setType(Line.LINE_TYPE_CODE_BLOCK_1);
            line.setCodeBlock(true);
            line.setBuilder(styleBuilder.codeBlock(content, CodeBlockSpan.FLAG_CENTER));
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
            line.setType(Line.LINE_TYPE_H1);
            String content = matcher.group(1);
            SpannableStringBuilder builder = new SpannableStringBuilder(content);
            handleInline(builder);
            line.setBuilder(styleBuilder.h1(builder));
            return true;
        }
        return false;
    }

    private boolean findH2(Line line) {
        Matcher matcher = patternH2.matcher(line.getSource());
        if (matcher.find()) {
            line.setType(Line.LINE_TYPE_H2);
            String content = matcher.group(1);
            SpannableStringBuilder builder = new SpannableStringBuilder(content);
            handleInline(builder);
            line.setBuilder(styleBuilder.h2(builder));
            return true;
        }
        return false;
    }

    private boolean findH3(Line line) {
        Matcher matcher = patternH3.matcher(line.getSource());
        if (matcher.find()) {
            line.setType(Line.LINE_TYPE_H3);
            String content = matcher.group(1);
            SpannableStringBuilder builder = new SpannableStringBuilder(content);
            handleInline(builder);
            line.setBuilder(styleBuilder.h3(builder));
            return true;
        }
        return false;
    }

    private boolean findH4(Line line) {
        Matcher matcher = patternH4.matcher(line.getSource());
        if (matcher.find()) {
            line.setType(Line.LINE_TYPE_H4);
            String content = matcher.group(1);
            SpannableStringBuilder builder = new SpannableStringBuilder(content);
            handleInline(builder);
            line.setBuilder(styleBuilder.h4(builder));
            return true;
        }
        return false;
    }

    private boolean findH5(Line line) {
        Matcher matcher = patternH5.matcher(line.getSource());
        if (matcher.find()) {
            line.setType(Line.LINE_TYPE_H5);
            String content = matcher.group(1);
            SpannableStringBuilder builder = new SpannableStringBuilder(content);
            handleInline(builder);
            line.setBuilder(styleBuilder.h5(builder));
            return true;
        }
        return false;
    }

    private boolean findH6(Line line) {
        Matcher matcher = patternH6.matcher(line.getSource());
        if (matcher.find()) {
            line.setType(Line.LINE_TYPE_H6);
            String content = matcher.group(1);
            SpannableStringBuilder builder = new SpannableStringBuilder(content);
            handleInline(builder);
            line.setBuilder(styleBuilder.h6(builder));
            return true;
        }
        return false;
    }

    private SpannableStringBuilder handleInline(SpannableStringBuilder builder) {
        handleCode(builder);
        handleEmItalic(builder);
        handleEm(builder);
        handleItalic(builder);
        handleDelete(builder);
        handleEmail(builder);
        handleImage(builder);
        handleImage2(builder);
        handleLink(builder);
        handleLink2(builder);
        handleAutoLink(builder);
        return builder;
    }

    private SpannableStringBuilder handleEm(SpannableStringBuilder c) {
        Matcher matcher = patternEm.matcher(c);
        while (matcher.find()) {
            int start = matcher.start(1);
            int end = matcher.end(1);
            if (checkInCode(c, start, end)) {
                continue;
            }
            SpannableStringBuilder sb = (SpannableStringBuilder) c.subSequence(matcher.start(3), matcher.end(3));
            c.delete(matcher.start(1), matcher.end(1));
            c.insert(matcher.start(1), styleBuilder.em(sb));
            return handleEm(c);
        }
        return c;
    }

    private SpannableStringBuilder handleItalic(SpannableStringBuilder builder) {
        Matcher matcher = patternItalic.matcher(builder);
        while (matcher.find()) {
            int start = matcher.start(1);
            int end = matcher.end(1);
            if (checkInCode(builder, start, end)) {
                continue;
            }
            SpannableStringBuilder sb = (SpannableStringBuilder) builder.subSequence(matcher.start(3), matcher.end(3));
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.italic(sb));
            return handleItalic(builder);
        }
        return builder;
    }

    private SpannableStringBuilder handleEmItalic(SpannableStringBuilder builder) {
        Matcher matcher = patternEmItalic.matcher(builder);
        while (matcher.find()) {
            int start = matcher.start(1);
            int end = matcher.end(1);
            if (checkInCode(builder, start, end)) {
                continue;
            }
            SpannableStringBuilder sb = (SpannableStringBuilder) builder.subSequence(matcher.start(3), matcher.end(3));
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.emItalic(sb));
            return handleEmItalic(builder);
        }
        return builder;
    }

    private SpannableStringBuilder handleCode(SpannableStringBuilder builder) {
        Matcher matcher = patternCode.matcher(builder);
        if (matcher.find()) {
            String content = matcher.group(3);
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.code(content));
            return handleCode(builder);
        }
        return builder;
    }

    private SpannableStringBuilder handleDelete(SpannableStringBuilder builder) {
        Matcher matcher = patternDelete.matcher(builder);
        while (matcher.find()) {
            int start = matcher.start(1);
            int end = matcher.end(1);
            if (checkInCode(builder, start, end)) {
                continue;
            }
            SpannableStringBuilder sb = (SpannableStringBuilder) builder.subSequence(matcher.start(3), matcher.end(3));
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.delete(sb));
            return handleDelete(builder);
        }
        return builder;
    }

    private SpannableStringBuilder handleEmail(SpannableStringBuilder builder) {
        Matcher matcher = patternEmail.matcher(builder);
        if (matcher.find()) {
            SpannableStringBuilder sb = (SpannableStringBuilder) builder.subSequence(matcher.start(2), matcher.end(2));
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.email(sb));
            return handleEmail(builder);
        }
        return builder;
    }

    private SpannableStringBuilder handleAutoLink(SpannableStringBuilder builder) {
        Matcher matcher = patternAutoLink.matcher(builder);
        while (matcher.find()) {
            String content = matcher.group();
            builder.delete(matcher.start(), matcher.end());
            builder.insert(matcher.start(), styleBuilder.link(content, content, ""));
        }
        return builder;
    }

    private SpannableStringBuilder handleLink(SpannableStringBuilder builder) {
        Matcher matcher = patternLink.matcher(builder);
        if (matcher.find()) {
            String title = matcher.group(2);
            String link = matcher.group(3);
            String hint = matcher.group(6);
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.link(title, link, hint));
            return handleLink(builder);
        }
        return builder;
    }

    private SpannableStringBuilder handleLink2(SpannableStringBuilder builder) {
        Matcher matcher = patternLink2.matcher(builder);
        if (matcher.find()) {
            String title = matcher.group(2);
            String id = matcher.group(3);
            Pair<String, String> link = idLinkLinks.get(id);
            if (link != null) {
                builder.delete(matcher.start(1), matcher.end(1));
                builder.insert(matcher.start(1), styleBuilder.link(title, link.first, link.second));
            }
            return handleLink2(builder);
        }
        return builder;
    }

    private SpannableStringBuilder handleImage2(SpannableStringBuilder builder) {
        Matcher matcher = patternImage2.matcher(builder);
        if (matcher.find()) {
            String title = matcher.group(2);
            String id = matcher.group(3);
            Pair<String, String> image = idImageUrl.get(id);
            if (image != null) {
                builder.delete(matcher.start(1), matcher.end(1));
                builder.insert(matcher.start(1), styleBuilder.image(title, image.first, image.second));
            }
            return handleImage2(builder);
        }
        return builder;
    }

    private boolean findIdLink(String line) {
        Matcher matcher = patternLink2Link.matcher(line);
        if (matcher.find()) {
            String id = matcher.group(1);
            String link = matcher.group(2);
            String hint = matcher.group(5);
            idLinkLinks.put(id, new Pair<>(link, hint));
            return true;
        }
        return false;
    }


    private boolean findIdImage(String line) {
        Matcher matcher = patternImage2Link.matcher(line);
        if (matcher.find()) {
            String id = matcher.group(1);
            String link = matcher.group(2);
            String hint = matcher.group(5);
            idImageUrl.put(id, new Pair<>(link, hint));
            return true;
        }
        return false;
    }

    private SpannableStringBuilder handleImage(SpannableStringBuilder builder) {
        Matcher matcher = patternImage.matcher(builder);
        if (matcher.find()) {
            String title = matcher.group(2);
            String link = matcher.group(3);
            String hint = matcher.group(6);
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.image(title, link, hint));
            return handleImage(builder);
        }
        return builder;
    }

    private int findQuota(String line) {
        Matcher matcher = patternQuota.matcher(line);
        if (matcher.find()) {
            return findQuota(matcher.group(1)) + 1;
        }
        return 0;
    }


    private boolean findQuota(Line line) {
        Matcher matcher = patternQuota.matcher(line.getSource());
        if (matcher.find()) {
            isOl = false;
            line.setType(Line.LINE_TYPE_QUOTA);
            Line line1 = new Line(line.lineNum, matcher.group(1));
            CharSequence userText;
            line.setTypeCount(1);
            if (findQuota(line1)) {
                if (line1.getType() == Line.LINE_TYPE_QUOTA)
                    line.setTypeCount(line1.getTypeCount() + 1);
                line.setBuilder(styleBuilder.quota(line1.getBuilder()));
                return true;
            }
            if (findUl(line1, true) || findOl(line1, true) || findH(line1)) {
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
            handleInline(builder);
            line.setBuilder(styleBuilder.quota(builder));
            return true;
        }
        return false;
    }

    private boolean findUl(String line) {
        Matcher matcher = patternUl.matcher(line);
        return matcher.find();
    }

    private boolean findUl(Line line) {
        return findUl(line, false);
    }


    private boolean findUl(Line line, boolean normal) {
        Matcher matcher = patternUl.matcher(line.getSource());
        if (matcher.find()) {
            line.setType(Line.LINE_TYPE_UL);
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
            handleInline(builder);
            line.setBuilder(normal ? styleBuilder.ul2(builder) : styleBuilder.ul(builder));
            return true;
        }
        return false;
    }

    private boolean findOl(String line) {
        Matcher matcher = patternOl.matcher(line);
        return matcher.find();
    }

    private boolean findOl(Line line) {
        return findOl(line, false);
    }

    private boolean findOl(Line line, boolean normal) {
        Matcher matcher = patternOl.matcher(line.getSource());
        if (matcher.find()) {
            line.setType(Line.LINE_TYPE_OL);
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
            handleInline(builder);
            int index = 1;
            if (isOl) {
                index = ++olIndex;
            } else {
                olIndex = index;
            }
            line.setBuilder(normal ? styleBuilder.ol2(builder, index) : styleBuilder.ol(builder, index));
            isOl = true;
            return true;
        }
        isOl = false;
        return false;
    }

    private boolean findH(String line) {
        Matcher matcher = patternH.matcher(line);
        return matcher.find();
    }


    private boolean checkInCode(SpannableStringBuilder builder, int start, int end) {
        CodeSpan[] css = builder.getSpans(0, builder.length(), CodeSpan.class);
        for (CodeSpan cs : css) {
            int c_start = builder.getSpanStart(cs);
            int c_end = builder.getSpanEnd(cs);
            if (!(c_start >= end || c_end <= start)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNewLine(Line line) {
        Matcher matcher = patternEndSpace.matcher(line.getSource());
        return matcher.find();
    }

    private boolean isCodeBlock(String line) {
        return patternCodeBlock.matcher(line).find();
    }

    private boolean isCodeBlock2(String line) {
        return patternCodeBlock2.matcher(line).find();
    }

}
