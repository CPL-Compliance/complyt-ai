package integration.test_utils.templates.endpoints;

import integration.test_utils.templates.methods.DeleteITTemplate;

public interface DeleteByExternalIdAndSourceITTemplate extends DeleteITTemplate {
    void deleteByExternalIdAndSource_Exists_Returns204();

    void get_checkDeletion_Returns200();

    void deleteByExternalIdAndSource_DoesntExists_Returns404();

}

