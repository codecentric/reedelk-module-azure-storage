package com.reedelk.azure.storage.internal.commons;

import com.reedelk.runtime.api.commons.FormattedMessage;

public class Messages {

    private Messages() {
    }

    public enum DeleteBlob implements FormattedMessage {

        CONTAINER_EMPTY("The container name is empty. The container name must not be empty (DynamicValue=[%s])."),
        BLOB_NAME_EMPTY("The blob name is empty. The blob name must not be empty (DynamicValue=[%s]).");

        private final String message;

        DeleteBlob(String message) {
            this.message = message;
        }

        @Override
        public String template() {
            return message;
        }
    }

    public enum DownloadBlob implements FormattedMessage {

        ERROR("The blob could not be downloaded, cause=[%s]"),
        CONTAINER_EMPTY("The container name is empty. The container name must not be empty (DynamicValue=[%s])."),
        BLOB_NAME_EMPTY("The blob name is empty. The blob name must not be empty (DynamicValue=[%s]).");

        private final String message;

        DownloadBlob(String message) {
            this.message = message;
        }

        @Override
        public String template() {
            return message;
        }
    }

    public enum ListBlobs implements FormattedMessage {

        CONTAINER_EMPTY("The container name is empty. The container name must not be empty (DynamicValue=[%s]).");

        private final String message;

        ListBlobs(String message) {
            this.message = message;
        }

        @Override
        public String template() {
            return message;
        }
    }

    public enum UploadBlob implements FormattedMessage {

        ERROR("An error occurred while uploading a blob on container=[%s], blob name=[%s], cause=[%s]."),
        CONTAINER_EMPTY("The container name is empty. The container name must not be empty (DynamicValue=[%s])."),
        BLOB_NAME_EMPTY("The blob name is empty. The blob name must not be empty (DynamicValue=[%s]).");

        private final String message;

        UploadBlob(String message) {
            this.message = message;
        }

        @Override
        public String template() {
            return message;
        }
    }
}
