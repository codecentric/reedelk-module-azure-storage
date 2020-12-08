package com.reedelk.azure.storage.internal.type;

import com.azure.storage.blob.models.BlobItem;
import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;
import com.reedelk.runtime.api.commons.SerializableUtils;

import java.io.Serializable;
import java.util.HashMap;

@Type
@TypeProperty(name = Blob.NAME, type = String.class)
@TypeProperty(name = Blob.METADATA, type = String.class)
@TypeProperty(name = Blob.SNAPSHOT, type = long.class)
@TypeProperty(name = Blob.TAGS, type = String.class)
@TypeProperty(name = Blob.VERSION_ID, type = String.class)
@TypeProperty(name = Blob.PROPERTIES, type = BlobProperties.class)
public class Blob extends HashMap<String, Serializable> {

    static final String NAME = "name";
    static final String METADATA = "metadata";
    static final String SNAPSHOT = "snapshot";
    static final String TAGS = "tags";
    static final String VERSION_ID = "versionId";
    static final String PROPERTIES = "properties";

    public Blob(BlobItem blobItem) {
        put(NAME, blobItem.getName());
        put(METADATA, SerializableUtils.asSerializableMap(blobItem.getMetadata()));
        put(SNAPSHOT, blobItem.getSnapshot());
        put(TAGS, SerializableUtils.asSerializableMap(blobItem.getTags()));
        put(VERSION_ID, blobItem.getVersionId());
        if (blobItem.getProperties() != null) {
            put(PROPERTIES, new BlobProperties(blobItem.getProperties()));
        }
    }
}
