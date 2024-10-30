package integration.endpoints;

import testUtils.integration_test.templates.endpoints.*;

public interface CustomerEndpointsITITTemplate extends
        UpsertByExternalIdAndSourceITTemplate,
        GetByExternalIdAndSourceITTemplate,
        GetByComplytIdITTemplate,
        GetAllITTemplate,
        GetAllBySourceTTemplate,
        PatchITTemplate,
        GetAllPaginationITTemplate {

    void getAll_PaginationSortedByNameDesc_ReturnsSortedCustomers();

    void getAll_PaginationSortedByNameAsc_ReturnsSortedCustomer();

    void getAll_PaginationFilteredByMarketPlaceCustomerType_ReturnsCustomers();

    void getAll_PaginationFilteredByRetailCustomerType_ReturnsCustomers();

    void getAll_PaginationFilteredByStateType_ReturnsCustomers();

    void getAll_PaginationFilteredByCustomerTypeAndState_ReturnsCustomers();

}