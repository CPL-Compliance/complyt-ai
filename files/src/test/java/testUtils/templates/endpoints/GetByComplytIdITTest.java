package testUtils.templates.endpoints;

public interface GetByComplytIdITTest {
    void getByComplytId_Exists_Returns200();

    void getByComplytId_DoesntExists_Returns404();
}
