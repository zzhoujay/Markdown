package com.zzhoujay.markdown.parser;

import android.text.SpannableStringBuilder;
import android.util.Pair;
import android.util.SparseArray;

import com.zzhoujay.markdown.style.CodeSpan;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhou on 16-7-10.
 */
public class TagHandlerImpl implements TagHandler {

    private static final Pattern patternH1_2 = Pattern.compile("^\\s*=+$");
    private static final Pattern patternH2_2 = Pattern.compile("^\\s*-+$");

    private static final Pattern patternH = Pattern.compile("^\\s*#{1,6}\\s+([^#]*)(\\s+#)?");
    private static final Pattern patternH1 = Pattern.compile("^\\s*#\\s+([^#]*)(\\s+#)?");
    private static final Pattern patternH2 = Pattern.compile("^\\s*#{2}\\s+([^#]*)(\\s+#)?");
    private static final Pattern patternH3 = Pattern.compile("^\\s*#{3}\\s+([^#]*)(\\s+#)?");
    private static final Pattern patternH4 = Pattern.compile("^\\s*#{4}\\s+([^#]*)(\\s+#)?");
    private static final Pattern patternH5 = Pattern.compile("^\\s*#{5}\\s+([^#]*)(\\s+#)?");
    private static final Pattern patternH6 = Pattern.compile("^\\s*#{6}\\s+([^#]*)(\\s+#)?");

    private static final Pattern patternQuota = Pattern.compile("^\\s{0,3}>\\s+(.*)");
    private static final Pattern patternUl = Pattern.compile("^\\s*[*+-]\\s+(.*)");
    private static final Pattern patternOl = Pattern.compile("^\\s*\\d+\\.\\s+(.*)");

    private static final Pattern patternItalic = Pattern.compile("[^*^_]*(([*_])([^*_].*?)\\2)");
    private static final Pattern patternEm = Pattern.compile("[^*_]*(([*_])\\2([^*_].*?)\\2\\2)");
    private static final Pattern patternEmItalic = Pattern.compile("[^*_]*(([*_])\\2\\2([^*_].*?)\\2\\2\\2)");
    private static final Pattern patternDelete = Pattern.compile("[^~]*((~{2,4})([^~].*?)\\2)");
    private static final Pattern patternCode = Pattern.compile("[^`]*((`+)([^`].*?)\\2)");

    private static final Pattern patternLink = Pattern.compile(".*?(\\[\\s*(.*?)\\s*]\\(\\s*(\\S*?)(\\s+(['\"])(.*?)\\5)?\\s*?\\))");
    private static final Pattern patternImage = Pattern.compile(".*?(!\\[\\s*(.*?)\\s*]\\(\\s*(\\S*?)(\\s+(['\"])(.*?)\\5)?\\s*?\\))");
    private static final Pattern patternLink2 = Pattern.compile(".*?(\\[\\s*(.*?)\\s*]\\s*\\[\\s*(.*?)\\s*])");
    private static final Pattern patternLinkId = Pattern.compile("^\\s*\\[\\s*(.*?)\\s*]:\\s*(\\S+?)(\\s+(['\"])(.*?)\\4)?\\s*$");
    private static final Pattern patternImage2 = Pattern.compile(".*?(!\\[\\s*(.*?)\\s*]\\s*\\[\\s*(.*?)\\s*])");
    private static final Pattern patternImageId = Pattern.compile("^\\s*!\\[\\s*(.*?)\\s*]:\\s*(\\S+?)(\\s+(['\"])(.*?)\\4)?\\s*$");

    private static final Pattern patternEmail = Pattern.compile(".*?(<(\\S+@\\S+\\.\\S+)>).*?");
    private static final Pattern patternAutoLink = Pattern.compile("https?://\\S+?");

    private static final Pattern patternEndSpace = Pattern.compile("(.*?) {2} *$");
    private static final Pattern patternInlineSpace = Pattern.compile("\\S*(\\s+)\\S+");

    private static final Pattern patternCodeBlock = Pattern.compile("^( {4}|\\t)(.*)");
    private static final Pattern patternCodeBlock2 = Pattern.compile("^\\s*```");

    private static final Pattern patternBlankLine = Pattern.compile("^\\s*$");

    private static final Pattern patternGap = Pattern.compile("^\\s*([-*]\\s*){3,}$");

    private static final SparseArray<Pattern> patterns = new SparseArray<>();

    static {
        patterns.put(Tag.CODE_BLOCK_1, patternCodeBlock);
        patterns.put(Tag.CODE_BLOCK_2, patternCodeBlock2);
        patterns.put(Tag.H1, patternH1);
        patterns.put(Tag.H2, patternH2);
        patterns.put(Tag.H3, patternH3);
        patterns.put(Tag.H4, patternH4);
        patterns.put(Tag.H5, patternH5);
        patterns.put(Tag.H6, patternH6);
        patterns.put(Tag.H, patternH);
        patterns.put(Tag.QUOTA, patternQuota);
        patterns.put(Tag.UL, patternUl);
        patterns.put(Tag.OL, patternOl);
        patterns.put(Tag.EM, patternEm);
        patterns.put(Tag.ITALIC, patternItalic);
        patterns.put(Tag.EM_ITALIC, patternEmItalic);
        patterns.put(Tag.EMAIL, patternEmail);
        patterns.put(Tag.AUTO_LINK, patternAutoLink);
        patterns.put(Tag.DELETE, patternDelete);
        patterns.put(Tag.LINK, patternLink);
        patterns.put(Tag.LINK2, patternLink2);
        patterns.put(Tag.LINK_ID, patternLinkId);
        patterns.put(Tag.IMAGE, patternImage);
        patterns.put(Tag.IMAGE2, patternImage2);
        patterns.put(Tag.IMAGE_ID, patternImageId);
        patterns.put(Tag.BLANK, patternBlankLine);
        patterns.put(Tag.NEW_LINE, patternEndSpace);
        patterns.put(Tag.GAP, patternGap);
        patterns.put(Tag.H1_2, patternH1_2);
        patterns.put(Tag.H2_2, patternH2_2);
    }

    private StyleBuilder styleBuilder;
    private HashMap<String, Pair<String, String>> idLinkLinks;
    private HashMap<String, Pair<String, String>> idImageUrl;


    public TagHandlerImpl(StyleBuilder styleBuilder) {
        this.styleBuilder = styleBuilder;
        idImageUrl = new HashMap<>();
        idLinkLinks = new HashMap<>();
    }

    @Override
    public boolean h(Line line) {
        return h1(line) || h2(line) || h3(line) || h4(line) || h5(line) || h6(line);
    }

    @Override
    public boolean h1(Line line) {
        Matcher matcher = patternH1.matcher(line.source);
        if (matcher.find()) {
            line.type = Line.LINE_TYPE_H1;
            line.style = SpannableStringBuilder.valueOf(matcher.group(1));
            inline(line);
            line.style = styleBuilder.h1(line.style);
            return true;
        }
        return false;
    }

    @Override
    public boolean h2(Line line) {
        Matcher matcher = patternH2.matcher(line.source);
        if (matcher.find()) {
            line.type = Line.LINE_TYPE_H2;
            line.style = SpannableStringBuilder.valueOf(matcher.group(1));
            inline(line);
            line.style = styleBuilder.h2(line.style);

            return true;
        }
        return false;
    }

    @Override
    public boolean h3(Line line) {
        Matcher matcher = patternH3.matcher(line.source);
        if (matcher.find()) {
            line.type = Line.LINE_TYPE_H3;
            line.style = SpannableStringBuilder.valueOf(matcher.group(1));
            inline(line);
            line.style = styleBuilder.h3(line.style);

            return true;
        }
        return false;
    }

    @Override
    public boolean h4(Line line) {
        Matcher matcher = patternH4.matcher(line.source);
        if (matcher.find()) {
            line.type = Line.LINE_TYPE_H4;
            line.style = SpannableStringBuilder.valueOf(matcher.group(1));
            inline(line);
            line.style = styleBuilder.h4(line.style);

            return true;
        }
        return false;
    }

    @Override
    public boolean h5(Line line) {
        Matcher matcher = patternH5.matcher(line.source);
        if (matcher.find()) {
            line.type = Line.LINE_TYPE_H5;
            line.style = SpannableStringBuilder.valueOf(matcher.group(1));
            inline(line);
            line.style = styleBuilder.h5(line.style);

            return true;
        }
        return false;
    }

    @Override
    public boolean h6(Line line) {
        Matcher matcher = patternH6.matcher(line.source);
        if (matcher.find()) {
            line.type = Line.LINE_TYPE_H6;
            line.style = SpannableStringBuilder.valueOf(matcher.group(1));
            inline(line);
            line.style = styleBuilder.h6(line.style);

            return true;
        }
        return false;
    }

    @Override
    public boolean quota(Line line) {
        Matcher matcher = patternQuota.matcher(line.source);
        if (matcher.find()) {
            line.type = Line.LINE_TYPE_QUOTA;
            Line line1 = line.createChild(matcher.group(1));
            line.attachChildToNext();
            line.attachChildToPrev();
            line1.parent = line;
            CharSequence userText;
            line.count = 1;
            if (quota(line1)) {
                if (line1.type == Line.LINE_TYPE_QUOTA)
                    line.count = line1.count + 1;
                line.style = styleBuilder.quota(line1.style);
                return true;
            }
            if (ul(line1, true, 0) || ol(line1, true, 0) || h(line1)) {
                userText = line1.style;
            } else {
                userText = line1.source;
            }
            SpannableStringBuilder builder;
            if (userText instanceof SpannableStringBuilder) {
                builder = (SpannableStringBuilder) userText;
            } else {
                builder = new SpannableStringBuilder(userText);
            }
            line.style = builder;
            inline(line);
            line.style = styleBuilder.quota(line.style);
            return true;
        }
        return false;
    }

    @Override
    public boolean ul(Line line) {
        return ul(line, false, 0);
    }

    private boolean ul(Line line, boolean normal, int level) {
        Matcher matcher = patternUl.matcher(line.source);
        if (matcher.find()) {
            line.type = Line.LINE_TYPE_UL;
            Line line1 = line.createChild(matcher.group(1));
            line.attr = 0;

            Line parent = line.parent;
            LineQueue queue = null;
            Line prev = line.prev;


//            boolean isQuota = false;
//
//            if (line instanceof LineQueue) {
//                queue = (LineQueue) line;
//                prev = queue.prevLine();
//            } else if (parent != null && parent.type == Line.LINE_TYPE_QUOTA && parent instanceof LineQueue) {
//                queue = (LineQueue) parent;
//                Line p = queue.prevLine();
//                if (p != null && p.type == Line.LINE_TYPE_QUOTA) {
//                    prev = p.getChild();
//                }
//                isQuota = true;
//            }
            if (prev != null && (prev.type == Line.LINE_TYPE_OL || prev.type == Line.LINE_TYPE_UL)) {
                if (level > 0) {
                    line.attr = level;
                } else {
                    String m = line.source.substring(matcher.start(), matcher.start(1) - 2);
                    m = m.replaceAll("\\t", "    ");
                    if (m.length() > prev.attr * 2 + 1)
                        line.attr = prev.attr + 1;
                    else
                        line.attr = m.length() / 2;
                }

            }
            if (find(Tag.UL, line1)) {
                int nextLevel = line.attr + 1;
                line.style = normal ? styleBuilder.ul2(" ", line.attr) : styleBuilder.ul(" ", line.attr);
                line1.unAttachFromParent();
                line.addNext(line1);
//                queue.insert(line1);
//                queue.next();
                return ul(line1, normal, nextLevel);
            }
//            if (queue != null) {
//                if (prev != null && (prev.type == Line.LINE_TYPE_OL || prev.type == Line.LINE_TYPE_UL)) {
//                    if (level > 0) {
//                        line.attr=level);
//                    } else {
//                        String m = line.source.substring(matcher.start(), matcher.start(1) - 2);
//                        m = m.replaceAll("\\t", "    ");
//                        if (m.length() > prev.attr * 2 + 1)
//                            line.attr=prev.attr + 1);
//                        else
//                            line.attr=m.length() / 2);
//                    }
//                }
//                if (find(Tag.UL, line1)) {
//                    int nextLevel = line.attr + 1;
////                    if (isQuota) {
////                        line.style=styleBuilder.quota(styleBuilder.ul2(" ", line.attr)));
////                        Line quota = new Line("");
////                        quota.setChild(line1);
////                        quota.type=Line.LINE_TYPE_QUOTA);
//////                        LineQueue q = queue.offset(0);
////                        queue.insert(quota);
////                        queue.next();
////                        line1.setParent(queue);
////                        ul(line1, true, nextLevel);
////                        quota.style=styleBuilder.quota(line1.style));
////                        return true;
////                    }
//                    line.style=normal ? styleBuilder.ul2(" ", line.attr) : styleBuilder.ul(" ", line.attr));
//                    queue.insert(line1);
//                    queue.next();
//                    return ul(queue, normal, nextLevel);
//                } else if (find(Tag.OL, line1)) {
//                    line.style=normal ? styleBuilder.ul2(" ", line.attr) : styleBuilder.ul(" ", line.attr));
//                    int nextLevel = line.attr + 1;
//                    queue.insert(line1);
//                    queue.next();
//                    return ol(queue, normal, nextLevel);
//                }
////            } else if (parent != null && parent.type == Line.LINE_TYPE_QUOTA) {
////
//            }
            CharSequence userText;
            if (h(line1)) {
                userText = line1.style;
            } else {
                userText = line1.source;
            }
            SpannableStringBuilder builder;
            if (userText instanceof SpannableStringBuilder) {
                builder = (SpannableStringBuilder) userText;
            } else {
                builder = new SpannableStringBuilder(userText);
            }
            line.style = builder;
            inline(line);
            line.style = normal ? styleBuilder.ul2(line.style, line.attr) : styleBuilder.ul(line.style, line.attr);
            return true;
        }
        return false;
    }

    @Override
    public boolean ol(Line line) {
        return ol(line, false, 0);
    }

    private boolean ol(Line line, boolean normal, int level) {
        Matcher matcher = patternOl.matcher(line.source);
        if (matcher.find()) {
            int index = 1;
            line.type = Line.LINE_TYPE_OL;
            Line line1 = new Line(matcher.group(1));
            line.attr = 0;
//            if (line instanceof LineQueue) {
//                LineQueue queue = (LineQueue) line;
//                Line prev = queue.prevLine();
//                if (prev != null && prev.type == Line.LINE_TYPE_OL) {
//                    index = prev.count + 1;
//                }
//                if (prev != null && (prev.type == Line.LINE_TYPE_OL || prev.type == Line.LINE_TYPE_UL)) {
//                    if (level > 0) {
//                        line.attr=level);
//                    } else {
//                        String s = line.source.substring(matcher.start(), matcher.start(1) - 2);
//                        s = s.replaceAll("\\t", "    ");
//                        if (s.length() > prev.attr * 2 + 1)
//                            line.attr=prev.attr + 1);
//                        else
//                            line.attr=s.length() / 2);
//                    }
//                }
//                if (find(Tag.UL, line1)) {
//                    line.style=normal ? styleBuilder.ol2(" ", line.attr, index) : styleBuilder.ol(" ", line.attr, index));
//                    queue.insert(line1);
//                    queue.next();
//                    return ul(queue, normal, line.attr + 1);
//                } else if (find(Tag.OL, line1)) {
//                    line.style=normal ? styleBuilder.ol2(" ", line.attr, index) : styleBuilder.ol(" ", line.attr, index));
//                    queue.insert(line1);
//                    queue.next();
//                    return ol(queue, normal, line.attr + 1);
//                }
//            }
            CharSequence userText;
            if (h(line1)) {
                userText = line1.style;
            } else {
                userText = line1.source;
            }
            SpannableStringBuilder builder;
            if (userText instanceof SpannableStringBuilder) {
                builder = (SpannableStringBuilder) userText;
            } else {
                builder = new SpannableStringBuilder(userText);
            }
            line.style = builder;
            inline(line);

            line.count = index;
            line.style = normal ? styleBuilder.ol2(line.style, line.attr, index) : styleBuilder.ol(line.style, line.attr, index);
            return true;
        }
        return false;
    }

    @Override
    public boolean gap(Line line) {
        Matcher matcher = patternGap.matcher(line.source);
        if (matcher.matches()) {
            line.type = Line.LINE_TYPE_GAP;
            line.style = styleBuilder.gap();
            return true;
        }
        return false;
    }

    @Override
    public boolean em(Line line) {
        SpannableStringBuilder builder = (SpannableStringBuilder) line.style;
        Matcher matcher = patternEm.matcher(line.style);
        while (matcher.find()) {
            int start = matcher.start(1);
            int end = matcher.end(1);
            if (checkInCode(builder, start, end)) {
                continue;
            }
            SpannableStringBuilder sb = (SpannableStringBuilder) builder.subSequence(matcher.start(3), matcher.end(3));
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.em(sb));
            em(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean italic(Line line) {
        SpannableStringBuilder builder = (SpannableStringBuilder) line.style;
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
            italic(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean emItalic(Line line) {
        SpannableStringBuilder builder = (SpannableStringBuilder) line.style;
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
            emItalic(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean code(Line line) {
        SpannableStringBuilder builder = (SpannableStringBuilder) line.style;
        Matcher matcher = patternCode.matcher(builder);
        if (matcher.find()) {
            String content = matcher.group(3);
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.code(content));
            code(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean email(Line line) {
        SpannableStringBuilder builder = (SpannableStringBuilder) line.style;
        Matcher matcher = patternEmail.matcher(builder);
        if (matcher.find()) {
            SpannableStringBuilder sb = (SpannableStringBuilder) builder.subSequence(matcher.start(2), matcher.end(2));
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.email(sb));
            email(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Line line) {
        SpannableStringBuilder builder = (SpannableStringBuilder) line.style;
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
            delete(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean autoLink(Line line) {
        SpannableStringBuilder builder = (SpannableStringBuilder) line.style;
        Matcher matcher = patternAutoLink.matcher(builder);
        while (matcher.find()) {
            String content = matcher.group();
            builder.delete(matcher.start(), matcher.end());
            builder.insert(matcher.start(), styleBuilder.link(content, content, ""));
        }
        return false;
    }

    @Override
    public boolean link(Line line) {
        SpannableStringBuilder builder = (SpannableStringBuilder) line.style;
        Matcher matcher = patternLink.matcher(builder);
        if (matcher.find()) {
            String title = matcher.group(2);
            String link = matcher.group(3);
            String hint = matcher.group(6);
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.link(title, link, hint));
            link(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean link2(Line line) {
        SpannableStringBuilder builder = (SpannableStringBuilder) line.style;
        Matcher matcher = patternLink2.matcher(builder);
        if (matcher.find()) {
            String title = matcher.group(2);
            String id = matcher.group(3);
            Pair<String, String> link = idLinkLinks.get(id);
            if (link != null) {
                builder.delete(matcher.start(1), matcher.end(1));
                builder.insert(matcher.start(1), styleBuilder.link(title, link.first, link.second));
            }
            link2(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean linkId(String line) {
        Matcher matcher = patternLinkId.matcher(line);
        if (matcher.find()) {
            String id = matcher.group(1);
            String link = matcher.group(2);
            String hint = matcher.group(5);
            idLinkLinks.put(id, new Pair<>(link, hint));
            return true;
        }
        return false;
    }

    @Override
    public boolean image(Line line) {
        SpannableStringBuilder builder = (SpannableStringBuilder) line.style;
        Matcher matcher = patternImage.matcher(builder);
        if (matcher.find()) {
            String title = matcher.group(2);
            String link = matcher.group(3);
            String hint = matcher.group(6);
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.image(title, link, hint));
            image(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean image2(Line line) {
        SpannableStringBuilder builder = (SpannableStringBuilder) line.style;
        Matcher matcher = patternImage2.matcher(builder);
        if (matcher.find()) {
            String title = matcher.group(2);
            String id = matcher.group(3);
            Pair<String, String> image = idImageUrl.get(id);
            if (image != null) {
                builder.delete(matcher.start(1), matcher.end(1));
                builder.insert(matcher.start(1), styleBuilder.image(title, image.first, image.second));
            }
            image2(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean imageId(String line) {
        Matcher matcher = patternImageId.matcher(line);
        if (matcher.find()) {
            String id = matcher.group(1);
            String link = matcher.group(2);
            String hint = matcher.group(5);
            idImageUrl.put(id, new Pair<>(link, hint));
            return true;
        }
        return false;
    }

    @Override
    public boolean codeBlock1(Line line) {
        Matcher matcher = patternCodeBlock.matcher(line.source);
        if (matcher.find()) {
            String content = matcher.group(2);
            line.type = Line.LINE_TYPE_CODE_BLOCK_1;
            line.style = content;
            return true;
        }
        return false;
    }

    @Override
    public boolean codeBlock2(Line line) {
        return find(Tag.CODE_BLOCK_2, line);
    }

    @Override
    public boolean inline(Line line) {
        boolean flag = code(line);
        flag = emItalic(line) || flag;
        flag = em(line) || flag;
        flag = italic(line) || flag;
        flag = delete(line) || flag;
        flag = email(line) || flag;
        flag = image(line) || flag;
        flag = image2(line) || flag;
        flag = link(line) || flag;
        flag = link2(line) || flag;
        flag = autoLink(line) || flag;
        return flag;
    }

    @Override
    public boolean find(int tag, Line line) {
        return line != null && find(tag, line.source);
    }

    @Override
    public boolean find(int tag, String line) {
        if (line == null) {
            return false;
        }
        Pattern pattern = patterns.get(tag);
        return pattern != null && pattern.matcher(line).find();
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
}