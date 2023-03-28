package testUtils.it.templates;

public interface GetByComplytIdITTemplate {

    void getByExternalIdAndSource_Exists_Returns200();

    void getByExternalIdAndSource_DoesntExists_Returns404();
}
