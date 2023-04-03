package testUtils.it.templates.endpoints;

public interface GetByStateITTemplate {

    void getByStateName_Exists_Returns200();

    void getByStateAbbreviation_Exists_Returns200();

    void getByStateAbbreviation_DoesntExists_Returns201();

    void getByStateName_DoesntExists_Returns404();
}
