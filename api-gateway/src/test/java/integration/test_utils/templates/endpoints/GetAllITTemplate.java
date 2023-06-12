package integration.test_utils.templates.endpoints;

public interface GetAllITTemplate {

    void getAll_Exists_Returns200();

    void getByAll_DoesntExists_Returns200EmptyList();
}
