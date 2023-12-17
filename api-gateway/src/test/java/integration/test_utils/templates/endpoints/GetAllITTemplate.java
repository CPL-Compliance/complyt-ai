package integration.test_utils.templates.endpoints;

import integration.test_utils.templates.methods.GetITTemplate;

public interface GetAllITTemplate extends GetITTemplate {

    void getAll_Exists_Returns200();

    void getByAll_DoesntExists_Returns200EmptyList();

//    void getAll_getCustomersByParamSize_ReturnsExpectedSize();
//
//    void getAll_getCustomersByDefaultOffset_ReturnsFirstEntry();
//
//    void getAll_GetSkippedCustomersByOffset_ReturnsExpectedEntryByOffset();
}
