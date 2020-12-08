package com.reedelk.azure.storage;

import com.reedelk.azure.storage.internal.AzureBlobServiceAsyncClientProvider;
import com.reedelk.azure.storage.internal.AzureBlobServiceSyncClientProvider;
import com.reedelk.runtime.api.annotation.Module;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import static org.osgi.service.component.annotations.ServiceScope.SINGLETON;

@Module("Azure Storage Module")
@Component(service = ModuleActivator.class, scope = SINGLETON, immediate = true)
public class ModuleActivator {

    @Deactivate
    public void deactivate() {
        AzureBlobServiceAsyncClientProvider.shutdown();
        AzureBlobServiceSyncClientProvider.shutdown();
    }
}