package testUtils.integration_test.templates.endpoints;

public interface DeleteByExternalIdAndSourceITTemplate {
    void deleteByExternalIdAndSource_Exists_Returns204();

    void get_checkDeletion_Returns200();

    void deleteByExternalIdAndSource_DoesntExists_Returns404();

}

