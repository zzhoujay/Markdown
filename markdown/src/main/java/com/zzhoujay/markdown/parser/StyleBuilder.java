package com.zzhoujay.markdown.parser;

import android.text.SpannableStringBuilder;

/**
 * Created by zhou on 16-6-28.
 */
public interface StyleBuilder {

    SpannableStringBuilder em(CharSequence charSequence);

    SpannableStringBuilder italic(CharSequence charSequence);

    SpannableStringBuilder emItalic(CharSequence charSequence);

    SpannableStringBuilder delete(CharSequence charSequence);

    SpannableStringBuilder email(CharSequence charSequence);

    SpannableStringBuilder link(CharSequence title, String link, String hint);

    SpannableStringBuilder image(CharSequence title, String url, String hint);

    SpannableStringBuilder code(CharSequence charSequence);

    SpannableStringBuilder h1(CharSequence charSequence);

    SpannableStringBuilder h2(CharSequence charSequence);

    SpannableStringBuilder h3(CharSequence charSequence);

    SpannableStringBuilder h4(CharSequence charSequence);

    SpannableStringBuilder h5(CharSequence charSequence);

    SpannableStringBuilder h6(CharSequence charSequence);

    SpannableStringBuilder quota(CharSequence charSequence);

    SpannableStringBuilder ul(CharSequence charSequence);

    SpannableStringBuilder ol(CharSequence charSequence, int index);

    SpannableStringBuilder ul2(CharSequence charSequence);

    SpannableStringBuilder ol2(CharSequence charSequence, int index);

    SpannableStringBuilder codeBlock(CharSequence charSequence, int flag);

}
