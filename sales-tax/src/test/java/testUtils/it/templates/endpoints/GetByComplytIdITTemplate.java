package testUtils.it.templates.endpoints;

public interface GetByComplytIdITTemplate {

    void getByComplytId_Exists_Returns200();

    void getByComplytId_DoesntExists_Returns404();

    void getByComplytId_complytIdDoesntParse_Returns500();
}
