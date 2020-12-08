package com.reedelk.azure.storage.internal.attribute;

import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;
import com.reedelk.runtime.api.message.MessageAttributes;

@Type
@TypeProperty(name = ListBlobAttributes.CONTINUATION_TOKEN, type = String.class)
public class ListBlobAttributes extends MessageAttributes {

    static final String CONTINUATION_TOKEN = "continuationToken";

    public ListBlobAttributes(String nextContinuationToken) {
        put(CONTINUATION_TOKEN, nextContinuationToken);
    }
}
