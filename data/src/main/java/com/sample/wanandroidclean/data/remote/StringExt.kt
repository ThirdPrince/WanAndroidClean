package com.sample.wanandroidclean.data.remote

import android.text.Html

/**
 * Decodes HTML entities like &ldquo; &rdquo; &amp; etc.
 */
fun String.decodeHtml(): String {
    return if (this.isEmpty()) {
        this
    } else {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
    }
}
