package testUtils.integration_test.templates.endpoints;

public interface GetByExternalIdAndSourceITTemplate {

    void getByExternalIdAndSource_Exists_Returns200();

    void getByExternalIdAndSource_DoesntExists_Returns404();
}
