package integration.services.files;

public interface ComplytFileMetadataEndpointsITTemplate {
    void deleteByComplytId_ValidMetdata_Returns200();

    void deleteByComplytId_InvalidMetadata_Returns404();

    void getAll_Exists_Returns200();

    void getByAll_DeletedFiles_Returns200EmptyList();
}
