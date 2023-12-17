package integration.test_utils.templates.endpoints;

import integration.test_utils.templates.methods.GetITTemplate;

public interface GetAllITTemplate extends GetITTemplate {

    void getAll_Exists_Returns200();

    void getByAll_DoesntExists_Returns200EmptyList();
    void getAll_GetByParamSize_ReturnsExpectedSize();
    void getAll_GetByParamPage_ReturnsExpectedPage();

    void getAll_GetByDefaultsSizeAndPage_ReturnsExpectedEntries();

}
