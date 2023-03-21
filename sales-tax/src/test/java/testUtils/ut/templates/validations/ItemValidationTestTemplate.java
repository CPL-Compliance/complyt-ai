package testUtils.ut.templates.validations;

public interface ItemValidationTestTemplate {

    void upsert_NegativeUnitPriceInItem_Returns400ValidationError();

    void upsert_NegativeQuantityInItem_Returns400ValidationError();

    void upsert_NegativeTotalPriceInItem_Returns400ValidationError();

    void upsert_NullNameInItem_Returns400ValidationError();

    void upsert_BlankNameInItem_Returns400ValidationError();

    void upsert_LengthGreaterThen256NameInItem_Returns400ValidationError();

    void upsert_NullTaxCodeInItem_Returns400ValidationError();

    void upsert_BlankTaxCodeInItem_Returns400ValidationError();

    void upsert_LengthGreaterThen256TaxCodeInItem_Returns400ValidationError();

    void upsert_NegativeManualSalesTaxRateInItem_Returns400ValidationError();

    void upsert_LargerThanMaxManualSalesTaxRateInItem_Returns400ValidationError();

}
