package testUtils.templates.endpoints;

public interface GetAllITTest {
    void getAll_Exists_Returns200();

    void getByAll_DoesntExists_Returns200EmptyList();
}
