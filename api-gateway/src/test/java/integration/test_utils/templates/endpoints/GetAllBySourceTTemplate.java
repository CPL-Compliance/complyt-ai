package integration.test_utils.templates.endpoints;

public interface GetAllBySourceTTemplate {

    void getAllBySource_Exists_Returns200();

    void getAllBySource_DoesntExists_Returns200EmptyList();
}
