package com.reedelk.azure.storage.internal.type;

import com.azure.storage.blob.models.BlobItem;
import com.reedelk.runtime.api.annotation.Type;

import java.util.ArrayList;
import java.util.List;

@Type(listItemType = Blob.class)
public class ListOfBlobs extends ArrayList<Blob> {

    public ListOfBlobs(List<BlobItem> blobItems) {
        if (blobItems != null) {
            blobItems.stream().map(Blob::new).forEach(this::add);
        }
    }
}
