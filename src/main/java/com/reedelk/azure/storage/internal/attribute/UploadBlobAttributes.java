package com.reedelk.azure.storage.internal.attribute;

import com.azure.storage.blob.models.BlockBlobItem;
import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.time.OffsetDateTime;

@Type
@TypeProperty(name = UploadBlobAttributes.ETAG, type = String.class)
@TypeProperty(name = UploadBlobAttributes.LAST_MODIFIED, type = OffsetDateTime.class)
@TypeProperty(name = UploadBlobAttributes.VERSION_ID, type = String.class)
public class UploadBlobAttributes extends MessageAttributes {

    static final String ETAG = "eTag";
    static final String LAST_MODIFIED = "lastModified";
    static final String VERSION_ID = "versionId";

    public UploadBlobAttributes(BlockBlobItem item) {
        if (item != null) {
            put(ETAG, item.getETag());
            put(LAST_MODIFIED, item.getLastModified());
            put(VERSION_ID, item.getVersionId());
        }
    }
}
