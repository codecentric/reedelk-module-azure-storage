package com.reedelk.azure.storage.internal;

import com.azure.core.http.HttpClient;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
import io.netty.channel.nio.NioEventLoopGroup;
import reactor.netty.http.HttpResources;

public class NettyClientProvider {

    private static HttpClient httpClient;

    public static HttpClient get() {
        if (httpClient == null) {
            synchronized (AzureBlobServiceSyncClientProvider.class) {
                if (httpClient == null) {
                    NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
                    NettyAsyncHttpClientBuilder clientBuilder = new NettyAsyncHttpClientBuilder();
                    clientBuilder.eventLoopGroup(eventLoopGroup);
                    httpClient = clientBuilder.build();
                }
            }
        }
        return httpClient;
    }

    public static void release() {
        synchronized (AzureBlobServiceSyncClientProvider.class) {
            httpClient = null;
            HttpResources.disposeLoopsAndConnections();
        }
    }
}
