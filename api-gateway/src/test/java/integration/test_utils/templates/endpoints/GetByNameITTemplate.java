package integration.test_utils.templates.endpoints;

import integration.test_utils.templates.methods.GetITTemplate;

public interface GetByNameITTemplate extends GetITTemplate {

    void getByName_Exists_Returns200();

    void getByName_DoesntExists_Returns200EmptyList();
}
