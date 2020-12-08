package com.reedelk.azure.storage.component;

import com.azure.core.http.rest.PagedResponse;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.reedelk.azure.storage.internal.AzureBlobServiceSyncClientProvider;
import com.reedelk.azure.storage.internal.attribute.ListBlobAttributes;
import com.reedelk.azure.storage.internal.commons.Default;
import com.reedelk.azure.storage.internal.commons.Validator;
import com.reedelk.azure.storage.internal.exception.ListBlobsException;
import com.reedelk.azure.storage.internal.type.Blob;
import com.reedelk.azure.storage.internal.type.ListOfBlobs;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicInteger;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.reedelk.azure.storage.internal.commons.Messages.ListBlobs.CONTAINER_EMPTY;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNullOrBlank;
import static com.reedelk.runtime.api.commons.DynamicValueUtils.isNotNullOrBlank;
import static java.util.stream.Collectors.toList;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("Azure Storage List Blobs")
@ComponentOutput(
        attributes = ListBlobAttributes.class,
        payload = ListOfBlobs.class,
        description = "A list of blob object infos fetched from the provided container.")
@ComponentInput(
        payload = Object.class,
        description = "The component input is used to evaluate the dynamic value provided for the container name and continuation token.")
@Description("The List Blobs Component returns a list of blob information about the blobs in the specified container. " +
        "Because containers can contain a virtually unlimited number of blobs, the complete results of a list query can be extremely large. " +
        "For this reason you can use pagination by specifying the 'maxResults' property and the 'continuationToken'. " +
        "The next continuation token is an output attribute of this component.")
@Component(service = ListBlobs.class, scope = PROTOTYPE)
public class ListBlobs implements ProcessorSync {

    @DialogTitle("Azure Storage Configuration")
    @Property("Configuration")
    @Mandatory
    @Description("The Azure Storage configuration to be used by this component.")
    private AzureStorageConfiguration configuration;

    @Property("Container Name")
    @Mandatory
    @Example("my-container")
    @Hint("my-container")
    @Description("The name of the container from which the blobs should be listed from.")
    private DynamicString container;

    @Group("Advanced")
    @Property("Max Results")
    @Hint("20")
    @Example("50")
    @Description("Sets the maximum number of blobs to return.")
    private DynamicInteger maxResults;

    @Group("Advanced")
    @Property("Continuation Token")
    @Hint("my-continuation-token")
    @Example("my-continuation-token")
    @Description("Sets the optional continuation token. " +
            "The continuation token is returned as attribute in the first call to this component when max results is set.")
    private DynamicString continuationToken;

    @Reference
    ScriptEngineService scriptService;

    @Override
    public void initialize() {
        Validator.validate(ListBlobs.class, configuration);
        requireNotNullOrBlank(ListBlobs.class, container, "Container must not be empty.");
    }

    @Override
    public Message apply(FlowContext flowContext, Message message) {
        BlobServiceClient client = AzureBlobServiceSyncClientProvider.from(configuration);

        String evaluatedContainer = scriptService.evaluate(this.container, flowContext, message)
                .orElseThrow(() -> new ListBlobsException(CONTAINER_EMPTY.format(container.value())));

        String evaluatedContinuationToken = null;
        if (isNotNullOrBlank(continuationToken)) {
            evaluatedContinuationToken =
                    scriptService.evaluate(this.continuationToken, flowContext, message).orElse(null);
        }

        ListBlobsOptions options = new ListBlobsOptions();
        scriptService.evaluate(this.maxResults, flowContext, message).ifPresent(options::setMaxResultsPerPage);

        BlobContainerClient blobContainerClient = client.getBlobContainerClient(evaluatedContainer);
        PagedResponseResult result = listBlobs(blobContainerClient, options, evaluatedContinuationToken);
        List<Blob> blobs = result.blobItems.stream().map(Blob::new).collect(toList());

        ListBlobAttributes attributes = new ListBlobAttributes(result.continuationToken);
        return MessageBuilder.get(ListBlobs.class)
                .attributes(attributes)
                .withJavaObject(blobs)
                .build();
    }

    public void setConfiguration(AzureStorageConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setContainer(DynamicString container) {
        this.container = container;
    }

    public void setMaxResults(DynamicInteger maxResults) {
        this.maxResults = maxResults;
    }

    public void setContinuationToken(DynamicString continuationToken) {
        this.continuationToken = continuationToken;
    }

    private PagedResponseResult listBlobs(BlobContainerClient client, ListBlobsOptions options, String evaluatedContinuationToken) {
        Iterator<PagedResponse<BlobItem>> response = client.listBlobs(
                options,
                evaluatedContinuationToken,
                Duration.ofSeconds(Default.LIST_TIMEOUT_SECONDS))
                .iterableByPage()
                .iterator();

        if (response.hasNext()) {
            PagedResponse<BlobItem> pagedResponse = response.next();
            String nextContinuationToken = pagedResponse.getContinuationToken();
            List<BlobItem> blobItems = pagedResponse.getValue();
            return new PagedResponseResult(blobItems, nextContinuationToken);
        }
        return new PagedResponseResult();
    }

    static class PagedResponseResult {

        private final String continuationToken;
        private final List<BlobItem> blobItems;

        PagedResponseResult() {
            this.continuationToken = null;
            this.blobItems = new ArrayList<>();
        }

        PagedResponseResult(List<BlobItem> blobItems, String continuationToken) {
            this.blobItems = blobItems;
            this.continuationToken = continuationToken;
        }
    }
}
