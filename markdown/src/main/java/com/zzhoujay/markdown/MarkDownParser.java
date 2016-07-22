package com.zzhoujay.markdown;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.zzhoujay.markdown.parser.Line;
import com.zzhoujay.markdown.parser.LineQueue;
import com.zzhoujay.markdown.parser.QueueConsumer;
import com.zzhoujay.markdown.parser.StyleBuilder;
import com.zzhoujay.markdown.parser.Tag;
import com.zzhoujay.markdown.parser.TagHandler;
import com.zzhoujay.markdown.parser.TagHandlerImpl;
import com.zzhoujay.markdown.style.ScaleHeightSpan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhou on 16-6-25.
 */
class MarkDownParser {


    private BufferedReader reader;
    private StyleBuilder styleBuilder;
    private TagHandler tagHandler;

    MarkDownParser(BufferedReader reader, StyleBuilder styleBuilder) {
        this.reader = reader;
        this.styleBuilder = styleBuilder;
        tagHandler = new TagHandlerImpl(styleBuilder);
    }

    MarkDownParser(InputStream inputStream, StyleBuilder styleBuilder) {
        this(new BufferedReader(new InputStreamReader(inputStream)), styleBuilder);
    }

    MarkDownParser(String text, StyleBuilder styleBuilder) {
        this(new BufferedReader(new StringReader(text)), styleBuilder);
    }


    public Spannable parser() throws IOException {
        LineQueue queue = collect();
        return merge(queue);
    }

    private LineQueue collect() throws IOException {
        String line;
        Line root = null;
        LineQueue queue = null;
        while ((line = reader.readLine()) != null) {
            if (!(tagHandler.imageId(line) || tagHandler.linkId(line))) {
                Line l = new Line(line);
                if (root == null) {
                    root = l;
                    queue = new LineQueue(root);
                } else {
                    queue.append(l);
                }
            }
        }
        return queue;
    }

    private Spannable merge(final LineQueue queue) {
        tagHandler.setQueueProvider(new QueueConsumer.QueueProvider() {
            @Override
            public LineQueue getQueue() {
                return queue;
            }
        });
        boolean block2 = false;
        removeBlankLine(queue);
        boolean need_next;
        boolean notBlock;
        do {
            notBlock = false;
            need_next = true;
            Line next = queue.nextLine();
            Line curr = queue.currLine();

            if (queue.prevLine() != null && (queue.prevLine().getType() == Line.LINE_TYPE_OL || queue.prevLine().getType() == Line.LINE_TYPE_UL)
                    && (tagHandler.find(Tag.UL, queue.currLine()) || tagHandler.find(Tag.OL, queue.currLine()))) {
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
                    removeBlankLine(queue, true);
                }
                need_next = false;
                continue;
            }

            if (block2) {
                queue.setType(Line.LINE_TYPE_CODE_BLOCK_2);
                queue.setStyle(queue.getSource());
                continue;
            }


            if (tagHandler.find(Tag.H1_2, next)) {
                curr.setType(Line.LINE_TYPE_H1);
                curr.setStyle(SpannableStringBuilder.valueOf(curr.getSource()));
                tagHandler.inline(curr);
                curr.setStyle(styleBuilder.h1(curr.getStyle()));
                curr.removeNext();
                removeBlankLine(queue);
                continue;
            }

            if (tagHandler.find(Tag.H2_2, next)) {
                curr.setType(Line.LINE_TYPE_H2);
                curr.setStyle(SpannableStringBuilder.valueOf(curr.getSource()));
                tagHandler.inline(curr);
                curr.setStyle(styleBuilder.h2(curr.getStyle()));
                curr.removeNext();
                removeBlankLine(queue);
                continue;
            }

            boolean isNewLine = tagHandler.find(Tag.NEW_LINE, queue.currLine()) || tagHandler.find(Tag.GAP, queue.currLine());
            if (isNewLine) {
                removeBlankLine(queue);
            }
            while (!isNewLine) {
                if (queue.nextLine() == null || removeBlankLine(queue)) {
                    break;
                }

                if (tagHandler.find(Tag.CODE_BLOCK_1, queue.nextLine()) || tagHandler.find(Tag.CODE_BLOCK_2, queue.nextLine()) ||
                        tagHandler.find(Tag.GAP, queue.nextLine()) || tagHandler.find(Tag.UL, queue.nextLine()) ||
                        tagHandler.find(Tag.OL, queue.nextLine()) || tagHandler.find(Tag.H, queue.nextLine())) {
                    break;
                }
                int nextQuotaCount = tagHandler.findCount(Tag.QUOTA, queue.nextLine(), 1);
                int currQuotaCount = tagHandler.findCount(Tag.QUOTA, queue.currLine(), 1);
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
                        queue.currLine().setSource(curr.getSource() + ' ' + r);
                        queue.removeNext();
                    }
                }

            }
            if (tagHandler.gap(queue) || tagHandler.quota(queue) || tagHandler.ol(queue) || tagHandler.ul(queue) ||
                    tagHandler.h(queue)) {
                continue;
            }
            curr.setStyle(SpannableStringBuilder.valueOf(queue.getSource()));
            tagHandler.inline(queue);
        } while (!need_next || queue.next());
        return mergeSpannable(queue);
    }

    private Spannable mergeSpannable(LineQueue queue) {
        queue.reset();
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
                    codeBlock.add(curr.getStyle());
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
                    codeBlock.add(curr.getStyle());
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
            builder.append(curr.getStyle()).append('\n');
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
        return removeBlankLine(queue, false);
    }

    private boolean removeBlankLine(LineQueue queue, boolean curr) {
        boolean flag = false;
        if (curr) {
            queue = queue.copy();
        } else {
            queue = queue.copyNext();
        }
        if (queue == null) {
            return false;
        }
        do {
            if (!tagHandler.find(Tag.BLANK, queue.get())) {
                break;
            }
            flag = true;
        } while (queue.removeCurrLine() != null);
        return flag;
    }


    private SpannableString listMarginBottom() {
        SpannableString ss = new SpannableString(" ");
        ss.setSpan(new ScaleHeightSpan(0.4f), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

}
