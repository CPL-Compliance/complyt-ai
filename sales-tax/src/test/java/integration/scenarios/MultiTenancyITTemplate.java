package integration.scenarios;

public interface MultiTenancyITTemplate {

    void getCustomer_ExistsInOtherTenant_Returns404();

    void getTransaction_ExistsInOtherTenant_Returns404();

    void getSalesTaxTracking_ExistsInOtherTenant_Returns404();

    void putCustomer_WithComplytIdAndExistsInOtherTenant_Returns400DataConflict();

    void putTransaction_WithComplytIdAndExistsInOtherTenant_Returns400DataConflict();

    void putSalesTaxTracking_WithComplytIdAndExistsInOtherTenant_Returns400DataConflict();

    void putCustomer_ExistsInOtherTenant_Returns200WithoutDataLeak();

    void putTransaction_ExistsInOtherTenant_Returns200WithoutDataLeak();

    void putSalesTaxTracking_ExistsInOtherTenant_Returns200WithoutDataLeak();
}
