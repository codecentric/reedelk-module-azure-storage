package com.reedelk.azure.storage.component;

import com.azure.core.http.HttpHeaders;
import com.azure.core.http.rest.Response;
import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.reedelk.azure.storage.internal.AzureBlobServiceSyncClientProvider;
import com.reedelk.azure.storage.internal.attribute.DeleteBlobAttributes;
import com.reedelk.azure.storage.internal.commons.Default;
import com.reedelk.azure.storage.internal.commons.Validator;
import com.reedelk.azure.storage.internal.exception.UploadBlobException;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.time.Duration;

import static com.reedelk.azure.storage.internal.commons.Messages.DeleteBlob.BLOB_NAME_EMPTY;
import static com.reedelk.azure.storage.internal.commons.Messages.DeleteBlob.CONTAINER_EMPTY;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNullOrBlank;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("Azure Storage Delete Blob")
@ComponentOutput(
        attributes = DeleteBlobAttributes.class,
        payload = ComponentOutput.PreviousComponent.class,
        description = "The Delete Blob Component output is the original input message. " +
                "The payload is not changed by this component.")
@ComponentInput(
        payload = Object.class,
        description = "The component input is used to evaluate the dynamic value " +
                "provided for the container name and blob name.")
@Description("The Azure Storage Delete Blob Component deletes the specified blob from the specified container. " +
        "The container name can be a dynamic expression and it is mandatory. " +
        "The blob name property which identifies the file to be deleted can be a dynamic expression as well and it is mandatory.")
@Component(service = DeleteBlob.class, scope = PROTOTYPE)
public class DeleteBlob implements ProcessorSync {

    @DialogTitle("Azure Storage Configuration")
    @Property("Configuration")
    @Mandatory
    @Description("The Azure Storage configuration to be used by this component.")
    private AzureStorageConfiguration configuration;

    @Property("Container Name")
    @Mandatory
    @Example("my-container")
    @Hint("my-container")
    @Description("The name of the container the blob should be removed from.")
    private DynamicString container;

    @Property("Blob Name")
    @Mandatory
    @Example("my-blob")
    @Hint("my-blob")
    @Description("The name of the blob to be removed from the container.")
    private DynamicString blobName;

    @Reference
    ScriptEngineService scriptService;

    @Override
    public void initialize() {
        Validator.validate(DeleteBlob.class, configuration);
        requireNotNullOrBlank(DeleteBlob.class, container, "Container must not be empty.");
        requireNotNullOrBlank(DeleteBlob.class, blobName, "Blob Name must not be empty.");
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

        Response<Void> voidResponse = client
                .deleteWithResponse(
                        null,
                        null,
                        Duration.ofSeconds(Default.DELETE_TIMEOUT_SECONDS),
                        Context.NONE);
        HttpHeaders headers = voidResponse.getHeaders();

        DeleteBlobAttributes attributes = new DeleteBlobAttributes(headers);
        return MessageBuilder.get(DeleteBlob.class)
                .withTypedContent(message.content())
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
