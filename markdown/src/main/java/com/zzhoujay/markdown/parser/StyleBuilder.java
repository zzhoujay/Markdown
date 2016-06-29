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

    SpannableStringBuilder link(CharSequence title,CharSequence link,CharSequence hint);

    SpannableStringBuilder image(CharSequence title,CharSequence url,CharSequence hint);

    SpannableStringBuilder code(CharSequence charSequence);

    SpannableStringBuilder h1(CharSequence charSequence);

    SpannableStringBuilder h2(CharSequence charSequence);

    SpannableStringBuilder h3(CharSequence charSequence);

    SpannableStringBuilder h4(CharSequence charSequence);

    SpannableStringBuilder h5(CharSequence charSequence);

    SpannableStringBuilder h6(CharSequence charSequence);

    SpannableStringBuilder quota(CharSequence charSequence);

    SpannableStringBuilder ul(CharSequence charSequence);

    SpannableStringBuilder ol(CharSequence charSequence);

    SpannableStringBuilder codeBlock(CharSequence charSequence);

}
