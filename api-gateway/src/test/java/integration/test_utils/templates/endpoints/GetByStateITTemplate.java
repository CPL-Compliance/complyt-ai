package integration.test_utils.templates.endpoints;

import integration.test_utils.templates.methods.GetITTemplate;

public interface GetByStateITTemplate extends GetITTemplate {

    void getByStateName_Exists_Returns200();

    void getByStateAbbreviation_Exists_Returns200();

    void getByStateAbbreviation_DoesntExists_Returns404();

    void getByStateName_DoesntExists_Returns404();
}
