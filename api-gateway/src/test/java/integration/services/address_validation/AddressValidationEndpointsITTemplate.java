package integration.services.address_validation;

public interface AddressValidationEndpointsITTemplate {
    void getAddress_ValidAndInCache_Returns200();
    void getAddress_ValidButNotCached_Returns200();
    void getAddress_NotValidAddress_Returns400();
}
