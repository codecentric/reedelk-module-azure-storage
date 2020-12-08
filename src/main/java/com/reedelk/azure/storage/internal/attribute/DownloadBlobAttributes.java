package com.reedelk.azure.storage.internal.attribute;

import com.azure.core.http.HttpHeaders;
import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.content.MimeType;

import static com.reedelk.azure.storage.internal.attribute.AttributesUtils.headerValueOrNull;

@Type
@TypeProperty(name = DownloadBlobAttributes.CONTENT_TYPE, type = String.class)
@TypeProperty(name = DownloadBlobAttributes.CONTENT_LENGTH, type = String.class)
@TypeProperty(name = DownloadBlobAttributes.LAST_MODIFIED, type = String.class)
@TypeProperty(name = DownloadBlobAttributes.ETAG, type = String.class)
public class DownloadBlobAttributes extends MessageAttributes {

    static final String CONTENT_TYPE = "contentType";
    static final String CONTENT_LENGTH = "contentLength";
    static final String LAST_MODIFIED = "lastModified";
    static final String ETAG = "eTag";

    public DownloadBlobAttributes(HttpHeaders httpHeaders) {
        put(CONTENT_TYPE, headerValueOrNull(httpHeaders, "content-type"));
        put(CONTENT_LENGTH, headerValueOrNull(httpHeaders, "content-length"));
        put(LAST_MODIFIED, headerValueOrNull(httpHeaders, "last-modified"));
        put(ETAG, headerValueOrNull(httpHeaders, "etag"));
    }

    public MimeType getContentType() {
        String contentType = get(CONTENT_TYPE);
        return MimeType.parse(contentType, MimeType.APPLICATION_BINARY);
    }
}
