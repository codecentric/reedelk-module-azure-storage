package com.reedelk.azure.storage.internal.exception;

import com.reedelk.runtime.api.exception.PlatformException;

public class ListBlobsException extends PlatformException {

    public ListBlobsException(String message) {
        super(message);
    }

    public ListBlobsException(String message, Throwable exception) {
        super(message, exception);
    }
}
