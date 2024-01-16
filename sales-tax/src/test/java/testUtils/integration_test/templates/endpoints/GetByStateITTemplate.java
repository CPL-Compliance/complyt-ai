package testUtils.integration_test.templates.endpoints;

public interface GetByStateITTemplate {

    void getByStateName_Exists_Returns200();

    void getByStateName_PathVariableInvalid_Returns400();

    void getByStateAbbreviation_Exists_Returns200();

    void getByStateAbbreviation_DoesntExists_Returns404();

    void getByStateName_DoesntExists_Returns404();
}
