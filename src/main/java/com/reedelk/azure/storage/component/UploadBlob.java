package com.reedelk.azure.storage.component;

import com.reedelk.azure.storage.internal.attribute.UploadBlobAttributes;
import com.reedelk.azure.storage.internal.commons.Validator;
import com.reedelk.azure.storage.internal.exception.UploadBlobException;
import com.reedelk.azure.storage.internal.upload.UploadStrategyDefault;
import com.reedelk.azure.storage.internal.upload.UploadStrategyStream;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static com.reedelk.azure.storage.internal.commons.Messages.UploadBlob.BLOB_NAME_EMPTY;
import static com.reedelk.azure.storage.internal.commons.Messages.UploadBlob.CONTAINER_EMPTY;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNullOrBlank;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("Azure Storage Upload Blob")
@ComponentOutput(
        attributes = UploadBlobAttributes.class,
        payload = { ComponentOutput.PreviousComponent.class },
        description = "The Azure Storage Upload Blob Component output is the original input message if upload strategy is default. " +
                "If upload strategy is 'Streaming' the output is empty.")
@ComponentInput(
        payload = { String.class, byte[].class },
        description = "The expected input is a string or a byte array of the data to be uploaded on the Azure Storage Container.")
@Description("The Azure Storage Upload Blob Component allows to upload a blob on a specified Azure Storage Container. " +
        "The container name can be a dynamic expression and it is mandatory. " +
        "The blob name property which identifies the name of the file to be stored on the container can be a dynamic expression as well and it is mandatory. " +
        "The Azure Storage Upload Blob Component output is the original input message if upload strategy is 'Default', " +
        "if upload strategy is 'Streaming' the output is empty.")
@Component(service = UploadBlob.class, scope = PROTOTYPE)
public class UploadBlob implements ProcessorSync {

    @DialogTitle("Azure Storage Configuration")
    @Property("Configuration")
    @Mandatory
    @Description("The Azure Storage configuration to be used by this component.")
    private AzureStorageConfiguration configuration;

    @Property("Container Name")
    @Mandatory
    @Example("my-container")
    @Hint("my-container")
    @Description("The name of the container on which the blobs should be uploaded to.")
    private DynamicString container;

    @Property("Blob Name")
    @Mandatory
    @Example("my-blob")
    @Hint("my-blob")
    @Description("The name of the new blob to be uploaded.")
    private DynamicString blobName;

    @Property("Upload Strategy")
    @Group("Advanced")
    @Example("STREAMING")
    @DefaultValue("DEFAULT")
    @Description("Determines the upload strategy. " +
            "If streaming the message payload stream is uploaded as stream and the output of " +
            "the component will be empty.")
    private UploadStrategy uploadStrategy = UploadStrategy.DEFAULT;

    @Reference
    ConverterService converterService;
    @Reference
    ScriptEngineService scriptService;

    private com.reedelk.azure.storage.internal.upload.UploadStrategy strategy;

    @Override
    public void initialize() {
        Validator.validate(UploadBlob.class, configuration);
        requireNotNullOrBlank(UploadBlob.class, container, "Container must not be empty.");
        requireNotNullOrBlank(UploadBlob.class, blobName, "Blob name must not be empty.");
        strategy = uploadStrategy == UploadStrategy.STREAMING ?
                new UploadStrategyStream(converterService) :
                new UploadStrategyDefault(converterService);
    }

    @Override
    public Message apply(FlowContext flowContext, Message message) {

        String evaluatedContainer = scriptService.evaluate(this.container, flowContext, message)
                .orElseThrow(() -> new UploadBlobException(CONTAINER_EMPTY.format(container.value())));

        String evaluatedBlobName = scriptService.evaluate(this.blobName, flowContext, message)
                .orElseThrow(() -> new UploadBlobException(BLOB_NAME_EMPTY.format(container.value())));

        return strategy.upload(this, configuration, message, evaluatedContainer, evaluatedBlobName);
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

    public void setUploadStrategy(UploadStrategy uploadStrategy) {
        this.uploadStrategy = uploadStrategy;
    }
}
