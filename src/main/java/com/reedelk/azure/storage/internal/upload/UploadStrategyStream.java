package com.reedelk.azure.storage.internal.upload;

import com.azure.core.util.FluxUtil;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlockBlobItem;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import com.reedelk.azure.storage.component.AzureStorageConfiguration;
import com.reedelk.azure.storage.component.UploadBlob;
import com.reedelk.azure.storage.internal.AzureBlobServiceAsyncClientProvider;
import com.reedelk.azure.storage.internal.attribute.UploadBlobAttributes;
import com.reedelk.azure.storage.internal.commons.Default;
import com.reedelk.azure.storage.internal.exception.UploadBlobException;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.message.content.TypedPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.time.Duration;

import static com.reedelk.azure.storage.internal.commons.Messages.UploadBlob.ERROR;

/**
 * Using this strategy if the payload is a stream, then it is consumed.
 * The output of the component is empty (the payload has been consumed in the upload process).
 */
public class UploadStrategyStream implements UploadStrategy {

    private final ConverterService converter;

    public UploadStrategyStream(ConverterService converter) {
        this.converter = converter;
    }

    @Override
    public Message upload(ProcessorSync user,
                          AzureStorageConfiguration configuration,
                                 Message message,
                                 String containerName,
                                 String blobName) {

        // We must convert the stream to byte buffer.
        TypedPublisher<byte[]> convert = converter.convert(message.content().stream(), byte[].class);
        Flux<ByteBuffer> map = Flux.from(convert).map(ByteBuffer::wrap);

        BlobServiceAsyncClient client = AzureBlobServiceAsyncClientProvider.from(configuration);
        BlobHttpHeaders httpHeaders = new BlobHttpHeaders();
        httpHeaders.setContentType(message.content().mimeType().toString());

        BlobParallelUploadOptions options = new BlobParallelUploadOptions(map);
        options.setHeaders(httpHeaders);
        options.setTimeout(Duration.ofSeconds(Default.UPLOAD_TIMEOUT_SECONDS));
        Mono<BlockBlobItem> uploadItemMono = client
                .getBlobContainerAsyncClient(containerName)
                .getBlobAsyncClient(blobName)
                .uploadWithResponse(options)
                .flatMap(FluxUtil::toMono);

        BlockBlobItem uploadedBlobItem;
        try {
            uploadedBlobItem = uploadItemMono.block();
        } catch (Exception exception) {
            String error = ERROR.format(containerName, blobName, exception.getMessage());
            throw new UploadBlobException(error);
        }

        if (uploadedBlobItem == null) {
            String error = ERROR.format(containerName, blobName, "Uploaded blob item was null.");
            throw new UploadBlobException(error);
        }

        // Empty payload. The payload content stream has been consumed in the upload phase..
        UploadBlobAttributes attributes = new UploadBlobAttributes(uploadedBlobItem);
        return MessageBuilder.get(UploadBlob.class)
                .empty()
                .attributes(attributes)
                .build();
    }
}
