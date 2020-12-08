package com.reedelk.azure.storage.internal.upload;

import com.azure.core.util.Context;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlockBlobItem;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import com.reedelk.azure.storage.component.AzureStorageConfiguration;
import com.reedelk.azure.storage.component.UploadBlob;
import com.reedelk.azure.storage.internal.AzureBlobServiceSyncClientProvider;
import com.reedelk.azure.storage.internal.attribute.UploadBlobAttributes;
import com.reedelk.azure.storage.internal.commons.Default;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

public class UploadStrategyDefault implements UploadStrategy {

    private final ConverterService converter;

    public UploadStrategyDefault(ConverterService converter) {
        this.converter = converter;

    }

    @Override
    public Message upload(ProcessorSync user,
                          AzureStorageConfiguration configuration,
                          Message message,
                          String containerName,
                          String blobName) {

        // We must convert the stream to byte buffer.
        Object payload = message.payload();
        byte[] data = converter.convert(payload, byte[].class);

        BlobServiceClient blobServiceClient = AzureBlobServiceSyncClientProvider.from(configuration);
        BlobHttpHeaders httpHeaders = new BlobHttpHeaders();
        httpHeaders.setContentType(message.content().mimeType().toString());

        BlockBlobItem uploadedBlobItem = null;
        try (InputStream is = new ByteArrayInputStream(data)) {
            BlobParallelUploadOptions uploadOptions = new BlobParallelUploadOptions(is, data.length);
            uploadOptions.setHeaders(httpHeaders);
            uploadedBlobItem = blobServiceClient
                    .getBlobContainerClient(containerName)
                    .getBlobClient(blobName)
                    .uploadWithResponse(
                            uploadOptions,
                            Duration.ofSeconds(Default.UPLOAD_TIMEOUT_SECONDS),
                            Context.NONE)
                    .getValue();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        // Empty payload. The payload content stream has been consumed in the upload phase..
        UploadBlobAttributes attributes = new UploadBlobAttributes(uploadedBlobItem);
        return MessageBuilder.get(UploadBlob.class)
                .withTypedContent(message.content())
                .attributes(attributes)
                .build();
    }
}
