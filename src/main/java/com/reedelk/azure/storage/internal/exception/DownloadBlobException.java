package com.reedelk.azure.storage.internal.exception;

import com.reedelk.runtime.api.exception.PlatformException;

public class DownloadBlobException extends PlatformException {

    public DownloadBlobException(String message) {
        super(message);
    }

    public DownloadBlobException(String message, Throwable exception) {
        super(message, exception);
    }
}
