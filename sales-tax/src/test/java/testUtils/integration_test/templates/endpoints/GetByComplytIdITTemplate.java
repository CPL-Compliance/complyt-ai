package testUtils.integration_test.templates.endpoints;

public interface GetByComplytIdITTemplate {

    void getByComplytId_Exists_Returns200();

    void getByComplytId_PathVariableInvalid_Returns400();

    void getByComplytId_DoesntExists_Returns404();

}
