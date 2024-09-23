package io.complyt.files.business.storage;

import io.complyt.files.business.storage.wrappers.DeleteFileStorageWrapper;
import io.complyt.files.business.storage.wrappers.GetSignedLinkStorageWrapper;
import io.complyt.files.business.storage.wrappers.ListFilesStorageWrapper;
import io.complyt.files.business.storage.wrappers.SaveFileStorageWrapper;

public interface StorageWrapper extends GetSignedLinkStorageWrapper, ListFilesStorageWrapper,
        SaveFileStorageWrapper, DeleteFileStorageWrapper {

}
