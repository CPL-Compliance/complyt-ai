package testUtils.integration_test.templates.endpoints;

public interface GetAllPaginationITTemplate {
    void getAll_PaginationSortedByDateDesc_ReturnsSortedList();
    void getAll_PaginationSortedByDateAsc_ReturnsSortedList();
    void getAll_InvalidSortOrderSent_Throws400();
}
