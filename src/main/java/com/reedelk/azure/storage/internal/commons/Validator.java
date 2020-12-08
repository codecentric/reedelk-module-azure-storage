package com.reedelk.azure.storage.internal.commons;

import com.reedelk.azure.storage.component.AzureStorageConfiguration;
import com.reedelk.runtime.api.component.ProcessorSync;

import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotBlank;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNull;

public class Validator {

    public static void validate(Class<? extends ProcessorSync> component, AzureStorageConfiguration configuration) {
        requireNotNull(component, configuration, "Azure Storage Configuration must not be empty.");
        requireNotBlank(component, configuration.getConnectionString(),
                "Azure Storage Configuration 'Connection String' property must not be empty.");
    }
}
