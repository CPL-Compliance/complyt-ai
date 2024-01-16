package integration.test_utils.templates.endpoints;

import integration.test_utils.templates.methods.GetITTemplate;

public interface GetByComplytIdITTemplate extends GetITTemplate {

    void getByComplytId_Exists_Returns200();

    void getByComplytId_PathVariableInvalid_Returns400();

    void getByComplytId_DoesntExists_Returns404();

    void getByComplytId_complytIdDoesntParse_Returns500();
}
