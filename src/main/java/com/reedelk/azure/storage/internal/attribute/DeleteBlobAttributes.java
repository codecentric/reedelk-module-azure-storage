package com.reedelk.azure.storage.internal.attribute;

import com.azure.core.http.HttpHeaders;
import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;
import com.reedelk.runtime.api.message.MessageAttributes;

import static com.reedelk.azure.storage.internal.attribute.AttributesUtils.headerValueOrNull;

@Type
@TypeProperty(name = DeleteBlobAttributes.DATE, type = String.class)
public class DeleteBlobAttributes extends MessageAttributes {

    static final String DATE = "date";

    public DeleteBlobAttributes(HttpHeaders headers) {
        put(DATE, headerValueOrNull(headers, "date"));
    }
}
