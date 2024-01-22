package integration.test_utils.templates.endpoints;

import integration.test_utils.templates.methods.GetITTemplate;

public interface GetAllBySourceTTemplate extends GetITTemplate {

    void getAllBySource_QueryParamInvalid_Returns400();

    void getAllBySource_PathVariableInvalid_Returns400();

    void getAllBySource_Exists_Returns200();

    void getAllBySource_DoesntExists_Returns200EmptyList();
}
