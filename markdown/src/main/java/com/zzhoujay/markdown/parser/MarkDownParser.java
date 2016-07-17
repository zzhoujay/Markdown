package com.zzhoujay.markdown.parser;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.zzhoujay.markdown.style.ScaleHeightSpan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhou on 16-6-25.
 */
public class MarkDownParser {

    private static final Pattern patternH = Pattern.compile("^\\s*#{1,6}\\s+([^#]*)(\\s+#)?");
    private static final Pattern patternH1 = Pattern.compile("^\\s*#\\s+([^#]*)(\\s+#)?");
    private static final Pattern patternH2 = Pattern.compile("^\\s*#{2}\\s+([^#]*)(\\s+#)?");
    private static final Pattern patternH3 = Pattern.compile("^\\s*#{3}\\s+([^#]*)(\\s+#)?");
    private static final Pattern patternH4 = Pattern.compile("^\\s*#{4}\\s+([^#]*)(\\s+#)?");
    private static final Pattern patternH5 = Pattern.compile("^\\s*#{5}\\s+([^#]*)(\\s+#)?");
    private static final Pattern patternH6 = Pattern.compile("^\\s*#{6}\\s+([^#]*)(\\s+#)?");

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

    private static final Pattern patternGap = Pattern.compile("^\\s*([-*]\\s*){3,}$");

    private BufferedReader reader;
    private StyleBuilder styleBuilder;
    private TagHandler tagHandler;

    public MarkDownParser(BufferedReader reader, StyleBuilder styleBuilder) {
        this.reader = reader;
        this.styleBuilder = styleBuilder;
        tagHandler = new TagHandlerImpl(styleBuilder);
    }

    public MarkDownParser(InputStream inputStream, StyleBuilder styleBuilder) {
        this(new BufferedReader(new InputStreamReader(inputStream)), styleBuilder);
    }

    public MarkDownParser(String text, StyleBuilder styleBuilder) {
        this(new BufferedReader(new StringReader(text)), styleBuilder);
    }


    public Spannable get() throws IOException {
        LineQueue queue = collect();
        return merge(queue);
    }

    private LineQueue collect() throws IOException {
        String line;
        List<Line> lines = new LinkedList<>();
        while ((line = reader.readLine()) != null) {
            if (!(tagHandler.imageId(line) || tagHandler.linkId(line))) {
                Line l = new Line(line);
                lines.add(l);
            }
        }
        return new LineQueue(lines);
    }

    private Spannable merge(LineQueue queue) {
        boolean block2 = false;
        removeBlankLine(queue);
        boolean need_next;
        boolean notBlock;
        do {
            notBlock = false;
            need_next = true;
            Line prev = queue.prevLine();
            Line next = queue.nextLine();
            Line curr = queue.get();

            if (prev != null && (prev.getType() == Line.LINE_TYPE_OL || prev.getType() == Line.LINE_TYPE_UL)
                    && (tagHandler.find(Tag.UL, curr) || tagHandler.find(Tag.OL, curr))) {
                notBlock = true;
            }

            if (!notBlock && !block2 && tagHandler.codeBlock1(queue)) {
                if (next != null) {
                    removeBlankLine(queue);
                }
                continue;
            }

            if (!notBlock && tagHandler.codeBlock2(queue)) {
                block2 = !block2;
                queue.remove();
                if (!block2) {
                    removeBlankLine(queue, 0);
                }
                need_next = false;
                System.out.println(queue);
                continue;
            }

            if (block2) {
                curr.setType(Line.LINE_TYPE_CODE_BLOCK_2);
                curr.setBuilder(curr.getSource());
                continue;
            }


            if (tagHandler.find(Tag.H1_2, next)) {
                curr.setType(Line.LINE_TYPE_H1);
                curr.setBuilder(SpannableStringBuilder.valueOf(curr.getSource()));
                tagHandler.inline(curr);
                curr.setBuilder(styleBuilder.h1(curr.getBuilder()));
                queue.removeNext();
                removeBlankLine(queue);
                continue;
            }

            if (tagHandler.find(Tag.H2_2, next)) {
                curr.setType(Line.LINE_TYPE_H2);
                curr.setBuilder(SpannableStringBuilder.valueOf(curr.getSource()));
                tagHandler.inline(curr);
                curr.setBuilder(styleBuilder.h2(curr.getBuilder()));
                queue.removeNext();
                removeBlankLine(queue);
                continue;
            }

            boolean isNewLine = tagHandler.find(Tag.NEW_LINE, curr) || tagHandler.find(Tag.GAP, curr);
            if (isNewLine) {
                removeBlankLine(queue);
            }
            while (!isNewLine) {
                if (next == null || removeBlankLine(queue)) {
                    break;
                }

                if (tagHandler.find(Tag.CODE_BLOCK_1, next) || tagHandler.find(Tag.CODE_BLOCK_2, next) ||
                        tagHandler.find(Tag.GAP, next) || tagHandler.find(Tag.UL, next) ||
                        tagHandler.find(Tag.OL, next) || tagHandler.find(Tag.H, next)) {
                    break;
                }

                int nextQuotaCount = findQuotaCount(next);
                int currQuotaCount = findQuotaCount(curr);
                if (nextQuotaCount > 0 && nextQuotaCount > currQuotaCount) {
                    break;
                } else {
                    String r = next.getSource();
                    if (nextQuotaCount > 0) {
                        r = r.replaceFirst("^\\s{0,3}(>\\s+){" + nextQuotaCount + "}", "");
                    }
//                        if (findUl(r)||findOl(r)) {
                    if (tagHandler.find(Tag.UL, r) || tagHandler.find(Tag.OL, r) || tagHandler.find(Tag.H, r)) {
                        break;
                    } else {
                        curr.setSource(curr.getSource() + ' ' + r);
                        queue.removeNext();
                        next = queue.nextLine();
                    }
                }

            }
            if (tagHandler.gap(queue) || tagHandler.quota(queue) || tagHandler.ol(queue) || tagHandler.ul(queue) ||
                    tagHandler.h(queue)) {
                continue;
            }
            curr.setBuilder(SpannableStringBuilder.valueOf(curr.getSource()));
            tagHandler.inline(queue);
        } while (!need_next || queue.next());
        return mergeSpannable(queue);
    }

    private Spannable mergeSpannable(LineQueue queue) {
        queue.seek(0);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        List<CharSequence> codeBlock = new ArrayList<>();
        do {
            Line curr = queue.get();
            Line prev = queue.prevLine();
            Line next = queue.nextLine();
            switch (curr.getType()) {
                case Line.LINE_TYPE_CODE_BLOCK_2:
                    if (prev != null && prev.getType() == Line.LINE_TYPE_CODE_BLOCK_1) {
                        CharSequence[] cs = new CharSequence[codeBlock.size()];
                        builder.append(styleBuilder.codeBlock(codeBlock.toArray(cs))).append('\n').append('\n');
                        codeBlock.clear();
                    }
                    codeBlock.add(curr.getBuilder());
                    if (next == null) {
                        CharSequence[] cs = new CharSequence[codeBlock.size()];
                        builder.append(styleBuilder.codeBlock(codeBlock.toArray(cs))).append('\n').append('\n');
                        codeBlock.clear();
                    }
                    continue;
                case Line.LINE_TYPE_CODE_BLOCK_1:
                    if (prev != null && prev.getType() == Line.LINE_TYPE_CODE_BLOCK_2) {
                        CharSequence[] cs = new CharSequence[codeBlock.size()];
                        builder.append(styleBuilder.codeBlock(codeBlock.toArray(cs))).append('\n').append('\n');
                        codeBlock.clear();
                    }
                    codeBlock.add(curr.getBuilder());
                    if (next == null) {
                        CharSequence[] cs = new CharSequence[codeBlock.size()];
                        builder.append(styleBuilder.codeBlock(codeBlock.toArray(cs))).append('\n').append('\n');
                        codeBlock.clear();
                    }
                    continue;
                default:
                    if (prev != null && (prev.getType() == Line.LINE_TYPE_CODE_BLOCK_1 || prev.getType() == Line.LINE_TYPE_CODE_BLOCK_2)) {
                        CharSequence[] cs = new CharSequence[codeBlock.size()];
                        builder.append(styleBuilder.codeBlock(codeBlock.toArray(cs))).append('\n').append('\n');
                        codeBlock.clear();
                    }
            }
            builder.append(curr.getBuilder()).append('\n');
            switch (curr.getType()) {
                case Line.LINE_TYPE_QUOTA:
                    if (next != null && next.getType() == Line.LINE_TYPE_QUOTA) {
                        int num = curr.getCount();
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
                    builder.append('\n');
                    break;
                case Line.LINE_TYPE_H4:
                    builder.append('\n');
                    break;
                case Line.LINE_TYPE_H5:
                case Line.LINE_TYPE_H6:
                case Line.LINE_TYPE_H1:
                case Line.LINE_TYPE_H2:
                case Line.LINE_TYPE_GAP:
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

    private boolean removeBlankLine(LineQueue queue) {
        return removeBlankLine(queue, 1);
    }

    private boolean removeBlankLine(LineQueue queue, int offset) {
        boolean flag = false;
        queue = queue.offset(offset);
        if (queue == null) {
            return false;
        }
        do {
            if (!tagHandler.find(Tag.BLANK, queue.get())) {
                break;
            }
            flag = true;
        } while (queue.remove() != null);
        return flag;
    }


    private SpannableString listMarginBottom() {
        SpannableString ss = new SpannableString(" ");
        ss.setSpan(new ScaleHeightSpan(0.4f), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }


    private int findQuota(String line) {
        Matcher matcher = patternQuota.matcher(line);
        if (matcher.find()) {
            return findQuota(matcher.group(1)) + 1;
        }
        return 0;
    }


    private int findQuotaCount(Line line) {
        Matcher matcher = patternQuota.matcher(line.getSource());
        if (matcher.find()) {
            return findQuota(matcher.group(1)) + 1;
        }
        return 0;
    }
}
