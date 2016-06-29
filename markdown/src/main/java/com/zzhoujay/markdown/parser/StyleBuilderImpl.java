package com.zzhoujay.markdown.parser;

import android.text.SpannableStringBuilder;

/**
 * Created by zhou on 16-6-28.
 */
public class StyleBuilderImpl implements StyleBuilder {
    @Override
    public SpannableStringBuilder em(CharSequence charSequence) {
        return new SpannableStringBuilder(String.format("{em:%s}", charSequence));
    }

    @Override
    public SpannableStringBuilder italic(CharSequence charSequence) {
        return new SpannableStringBuilder(String.format("{italic:%s}", charSequence));
    }

    @Override
    public SpannableStringBuilder emItalic(CharSequence charSequence) {
        return new SpannableStringBuilder(String.format("{emItalic:%s}", charSequence));
    }

    @Override
    public SpannableStringBuilder delete(CharSequence charSequence) {
        return new SpannableStringBuilder(String.format("{delete:%s}", charSequence));
    }

    @Override
    public SpannableStringBuilder email(CharSequence charSequence) {
        return new SpannableStringBuilder(String.format("{email:%s}", charSequence));
    }

    @Override
    public SpannableStringBuilder code(CharSequence charSequence) {
        return new SpannableStringBuilder(String.format("{code:%s}", charSequence));
    }

    @Override
    public SpannableStringBuilder h1(CharSequence charSequence) {
        return new SpannableStringBuilder(String.format("h1:{%s}", charSequence));
    }

    @Override
    public SpannableStringBuilder h2(CharSequence charSequence) {
        return new SpannableStringBuilder(String.format("h2:{%s}", charSequence));
    }

    @Override
    public SpannableStringBuilder h3(CharSequence charSequence) {
        return new SpannableStringBuilder(String.format("h3:{%s}", charSequence));
    }

    @Override
    public SpannableStringBuilder h4(CharSequence charSequence) {
        return new SpannableStringBuilder(String.format("h4:{%s}", charSequence));
    }

    @Override
    public SpannableStringBuilder h5(CharSequence charSequence) {
        return new SpannableStringBuilder(String.format("h5:{%s}", charSequence));
    }

    @Override
    public SpannableStringBuilder h6(CharSequence charSequence) {
        return new SpannableStringBuilder(String.format("h6:{%s}", charSequence));
    }

    @Override
    public SpannableStringBuilder quota(CharSequence charSequence) {
        return new SpannableStringBuilder(String.format("quota:{%s}", charSequence));
    }

    @Override
    public SpannableStringBuilder ul(CharSequence charSequence) {
        return new SpannableStringBuilder(String.format("ul:{%s}", charSequence));
    }

    @Override
    public SpannableStringBuilder ol(CharSequence charSequence) {
        return new SpannableStringBuilder(String.format("ol:{%s}", charSequence));
    }

    @Override
    public SpannableStringBuilder codeBlock(CharSequence charSequence) {
        return new SpannableStringBuilder(String.format("codeBlock:{%s}", charSequence));
    }

    @Override
    public SpannableStringBuilder link(CharSequence title, CharSequence link, CharSequence hint) {
        return new SpannableStringBuilder(String.format("{title:%s,link:%s,hint:%s}", title, link, hint));
    }

    @Override
    public SpannableStringBuilder image(CharSequence title, CharSequence url, CharSequence hint) {
        return new SpannableStringBuilder(String.format("{title:%s,url:%s,hint:%s}", title, url, hint));
    }
}
