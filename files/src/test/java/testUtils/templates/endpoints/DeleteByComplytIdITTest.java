package testUtils.templates.endpoints;

public interface DeleteByComplytIdITTest {
    void deleteByComplytId_ValidMetdata_Returns200();

    void deleteByComplytId_InvalidMetadata_Returns404();
}
