package com.reedelk.azure.storage.internal.upload;

import com.reedelk.azure.storage.component.AzureStorageConfiguration;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.message.Message;

public interface UploadStrategy {

    Message upload(ProcessorSync user,
                   AzureStorageConfiguration configuration,
                   Message message,
                   String containerName,
                   String blobName);
}
