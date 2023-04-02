package testUtils.it.templates.endpoints;

public interface GetByComplytIdITTemplate {

    void getByExternalIdAndSource_Exists_Returns200();

    void getByExternalIdAndSource_DoesntExists_Returns404();
}
