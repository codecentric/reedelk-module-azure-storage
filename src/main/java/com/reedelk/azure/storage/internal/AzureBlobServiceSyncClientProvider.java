package com.reedelk.azure.storage.internal;

import com.azure.core.http.HttpClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.reedelk.azure.storage.component.AzureStorageConfiguration;

public class AzureBlobServiceSyncClientProvider {

    public static BlobServiceClient from(AzureStorageConfiguration configuration) {
        HttpClient httpClient = NettyClientProvider.get();
        return new BlobServiceClientBuilder()
                .connectionString(configuration.getConnectionString())
                .httpClient(httpClient)
                .buildClient();
    }

    public static synchronized void shutdown() {
        NettyClientProvider.release();
    }
}
