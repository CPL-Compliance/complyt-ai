package testUtils.ut.templates.validations;

public interface ShippingFeeValidationTestTemplate {

    void upsert_NegativeManualSalesRateTaxInShippingFee_Returns400ValidationError();

    void upsert_NegativeTotalPriceInShippingFee_Returns400ValidationError();

    void upsert_NullTaxCodeInShippingFee_Returns400ValidationError();

    void upsert_BlankTaxCodeInShippingFee_Returns400ValidationError();

    void upsert_LengthGreaterThan256TaxCodeInShippingFee_Returns400ValidationError();

}
