package testUtils.it.templates;

public interface GetByExternalIdAndSourceITTemplate {

    void getByExternalIdAndSource_Exists_Returns200();

    void getByExternalIdAndSource_DoesntExists_Returns404();
}
