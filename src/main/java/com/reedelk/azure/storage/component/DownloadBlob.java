package com.reedelk.azure.storage.component;

import com.azure.core.http.HttpHeaders;
import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobDownloadResponse;
import com.reedelk.azure.storage.internal.AzureBlobServiceSyncClientProvider;
import com.reedelk.azure.storage.internal.attribute.DownloadBlobAttributes;
import com.reedelk.azure.storage.internal.commons.Default;
import com.reedelk.azure.storage.internal.commons.Validator;
import com.reedelk.azure.storage.internal.exception.DownloadBlobException;
import com.reedelk.azure.storage.internal.exception.UploadBlobException;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;

import static com.reedelk.azure.storage.internal.commons.Messages.DownloadBlob.*;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNullOrBlank;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("Azure Storage Download Blob")
@ComponentOutput(
        attributes = DownloadBlobAttributes.class,
        payload = byte[].class,
        description = "A byte array containing the data of the object downloaded from Azure Storage.")
@ComponentInput(
        payload = Object.class,
        description = "The component input is used to evaluate the dynamic " +
                "values provided for the container and the blob name to be downloaded.")
@Description("The Azure Storage Download Blob gets the object stored in Azure Storage from the given " +
        "container and blob name.")
@Component(service = DownloadBlob.class, scope = PROTOTYPE)
public class DownloadBlob implements ProcessorSync {

    @DialogTitle("Azure Storage Configuration")
    @Property("Configuration")
    @Mandatory
    @Description("The Azure Storage configuration to be used by this component.")
    private AzureStorageConfiguration configuration;

    @Property("Container Name")
    @Mandatory
    @Example("my-container")
    @Hint("my-container")
    @Description("The name of the container from which the blob should be downloaded from.")
    private DynamicString container;

    @Property("Blob Name")
    @Mandatory
    @Example("my-blob")
    @Hint("my-blob")
    @Description("The name of the blob to be downloaded.")
    private DynamicString blobName;

    @Reference
    ScriptEngineService scriptService;

    @Override
    public void initialize() {
        Validator.validate(DownloadBlob.class, configuration);
        requireNotNullOrBlank(DownloadBlob.class, container, "Container must not be empty.");
        requireNotNullOrBlank(DownloadBlob.class, blobName, "Blob Name must not be empty.");
    }

    @Override
    public Message apply(FlowContext flowContext, Message message) {

        String evaluatedContainer = scriptService.evaluate(this.container, flowContext, message)
                .orElseThrow(() -> new UploadBlobException(CONTAINER_EMPTY.format(container.value())));

        String evaluatedBlobName = scriptService.evaluate(this.blobName, flowContext, message)
                .orElseThrow(() -> new UploadBlobException(BLOB_NAME_EMPTY.format(container.value())));


        BlobServiceClient serviceClient = AzureBlobServiceSyncClientProvider.from(configuration);
        BlobClient client = serviceClient
                .getBlobContainerClient(evaluatedContainer)
                .getBlobClient(evaluatedBlobName);

        HttpHeaders headers;
        byte[] data;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BlobDownloadResponse response =
                    client.downloadWithResponse(
                            outputStream,
                            null,
                            null,
                            null,
                            false,
                            Duration.ofSeconds(Default.DOWNLOAD_TIMEOUT_SECONDS),
                            Context.NONE);
            headers = response.getHeaders();
            data = outputStream.toByteArray();

        } catch (IOException exception) {
            String error = ERROR.format(exception.getMessage());
            throw new DownloadBlobException(error);
        }

        DownloadBlobAttributes attributes = new DownloadBlobAttributes(headers);
        MimeType contentType = attributes.getContentType();

        return MessageBuilder.get(DownloadBlob.class)
                .withBinary(data, contentType)
                .attributes(attributes)
                .build();
    }

    public void setConfiguration(AzureStorageConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setContainer(DynamicString container) {
        this.container = container;
    }

    public void setBlobName(DynamicString blobName) {
        this.blobName = blobName;
    }
}
