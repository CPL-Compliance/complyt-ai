package testUtils.integration_test.templates.endpoints;

public interface GetAllITTemplate {

    void getAll_Exists_Returns200();

    void getAll_QueryParamInvalid_Returns400();

    void getByAll_DoesntExists_Returns200EmptyList();

    void getByAll_QueryParamInvalid_Returns400();

    void getAll_GetByParamSize_ReturnsExpectedSize();

    void getAll_GetByParamPage_ReturnsExpectedPage();

    void getAll_GetByDefaultsSizeAndPage_ReturnsExpectedEntries();


}
