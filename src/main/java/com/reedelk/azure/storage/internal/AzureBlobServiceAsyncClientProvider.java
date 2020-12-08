package com.reedelk.azure.storage.internal;

import com.azure.core.http.HttpClient;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.reedelk.azure.storage.component.AzureStorageConfiguration;

public class AzureBlobServiceAsyncClientProvider {

    public static BlobServiceAsyncClient from(AzureStorageConfiguration configuration) {
        HttpClient httpClient = NettyClientProvider.get();
        return new BlobServiceClientBuilder()
                .connectionString(configuration.getConnectionString())
                .httpClient(httpClient)
                .buildAsyncClient();
    }

    public static synchronized void shutdown() {
        NettyClientProvider.release();
    }
}
