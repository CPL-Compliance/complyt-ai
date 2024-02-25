package integration.test_utils.templates.endpoints;

public interface GetByTenantIdITTemplate {
    void getByTenantId_Exists_Returns200();
    void getByTenantId_NotExists_Returns404();
    void getByTenantId_PathVariableInvalid_Returns400();
}
