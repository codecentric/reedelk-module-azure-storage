package com.reedelk.azure.storage.internal.exception;

import com.reedelk.runtime.api.exception.PlatformException;

public class UploadBlobException extends PlatformException {

    public UploadBlobException(String message) {
        super(message);
    }

    public UploadBlobException(String message, Throwable exception) {
        super(message, exception);
    }
}
