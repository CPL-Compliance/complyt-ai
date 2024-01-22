package testUtils.integration_test.templates.endpoints;

public interface GetAllBySourceTTemplate {

    void getAllBySource_Exists_Returns200();

    void getAllBySource_QueryParamInvalid_Returns400();

    void getAllBySource_PathVariableInvalid_Returns400();

    void getAllBySource_DoesntExists_Returns200EmptyList();
}
