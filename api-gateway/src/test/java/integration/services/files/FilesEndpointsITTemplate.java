package integration.services.files;

import integration.test_utils.templates.endpoints.GetAllITTemplate;
import integration.test_utils.templates.methods.GetITTemplate;

public interface FilesEndpointsITTemplate extends GetITTemplate {
    void getAll_Exists_Returns200();

    void getByAll_DoesntExists_Returns200EmptyList();
}
