package integration.test_utils.templates.endpoints;

import integration.test_utils.templates.methods.GetITTemplate;

public interface GetByExternalIdAndSourceITTemplate extends GetITTemplate {

    void getByExternalIdAndSource_Exists_Returns200();

    void getByExternalIdAndSource_DoesntExists_Returns404();
}
