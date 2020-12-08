package com.reedelk.azure.storage.component;

import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.Implementor;
import org.osgi.service.component.annotations.Component;

import java.util.Objects;

import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@Shared
@Component(service = AzureStorageConfiguration.class, scope = PROTOTYPE)
public class AzureStorageConfiguration implements Implementor {

    @Property("id")
    @Hidden
    private String id;

    @Property("Connection String")
    @Example("DefaultEndpointsProtocol=https; AccountName=11111; AccountKey=11111111111==; EndpointSuffix=core.windows.net1")
    @Hint("DefaultEndpointsProtocol=https;AccountName=11111;AccountKey=11111111111==;EndpointSuffix=core.windows.net1")
    @Description("The connection string of the Azure Storage Account.")
    private String connectionString;

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AzureStorageConfiguration that = (AzureStorageConfiguration) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AzureStorageConfiguration{" +
                "id='" + id + '\'' +
                ", connectionString='" + connectionString + '\'' +
                '}';
    }
}
