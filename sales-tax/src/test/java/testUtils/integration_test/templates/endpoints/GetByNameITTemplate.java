package testUtils.integration_test.templates.endpoints;

public interface GetByNameITTemplate {

    void getByName_Exists_Returns200();

    void getByName_DoesntExists_Returns200EmptyList();
}
