package com.reedelk.azure.storage.internal.attribute;

import com.azure.core.http.HttpHeader;
import com.azure.core.http.HttpHeaders;

import java.io.Serializable;

public class AttributesUtils {

    public static Serializable headerValueOrNull(HttpHeaders httpHeaders, String headerName) {
        if (httpHeaders == null) return null;
        HttpHeader httpHeader = httpHeaders.get(headerName);
        if (httpHeader != null) return httpHeader.getValue();
        return null;
    }
}
