package com.tanmay.composegallery.utils

enum class MimeType(val type: String) {
    GIF("image/gif"),
    PNG("image/png"),
    JPEG("image/jpeg"),
    BMP("image/bmp"),
    WEBP("image/webp");
}

fun MimeType.equalsMimeType(mimeType: String) = this.type == mimeType