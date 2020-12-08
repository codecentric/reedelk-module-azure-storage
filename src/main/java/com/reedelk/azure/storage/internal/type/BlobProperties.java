package com.reedelk.azure.storage.internal.type;

import com.azure.storage.blob.models.BlobItemProperties;
import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashMap;

@Type
@TypeProperty(name = BlobProperties.CONTENT_LENGTH, type = Long.class)
@TypeProperty(name = BlobProperties.BLOB_SEQUENCE_NUMBER, type = Long.class)
@TypeProperty(name = BlobProperties.CONTENT_ENCODING, type = String.class)
@TypeProperty(name = BlobProperties.ETAG, type = String.class)
@TypeProperty(name = BlobProperties.LAST_MODIFIED, type = OffsetDateTime.class)
public class BlobProperties extends HashMap<String, Serializable> {

    static final String BLOB_SEQUENCE_NUMBER = "blobSequenceNumber";
    static final String CONTENT_LENGTH = "contentLength";
    static final String CONTENT_ENCODING = "contentEncoding";
    static final String ETAG = "eTag";
    static final String LAST_MODIFIED = "lastModified";

    public BlobProperties(BlobItemProperties properties) {
        put(BLOB_SEQUENCE_NUMBER, properties.getBlobSequenceNumber());
        put(CONTENT_LENGTH, properties.getContentLength());
        put(CONTENT_ENCODING, properties.getContentEncoding());
        put(ETAG, properties.getETag());
        put(LAST_MODIFIED, properties.getLastModified());
    }
}
