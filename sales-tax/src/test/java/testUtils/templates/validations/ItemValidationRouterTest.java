package testUtils.templates.validations;

public interface ItemValidationRouterTest {

    void upsert_NegativeUnitPriceInItem_Returns400ValidationError();

    void upsert_NegativeQuantityInItem_Returns400ValidationError();

    void upsert_NegativeTotalPriceInItem_Returns400ValidationError();

    void upsert_NullDescriptionInItem_Returns400ValidationError();

    void upsert_BlankDescriptionInItem_Returns400ValidationError();

    void upsert_LengthGreaterThen256DescriptionInItem_Returns400ValidationError();

    void upsert_NullNameInItem_Returns400ValidationError();

    void upsert_BlankNameInItem_Returns400ValidationError();

    void upsert_LengthGreaterThen256NameInItem_Returns400ValidationError();

    void upsert_NullTaxCodeInItem_Returns400ValidationError();

    void upsert_BlankTaxCodeInItem_Returns400ValidationError();

    void upsert_LengthGreaterThen256TaxCodeInItem_Returns400ValidationError();

    void upsert_NegativeManualSalesTaxRateInItem_Returns400ValidationError();

    void upsert_LargerThanMaxManualSalesTaxRateInItem_Returns400ValidationError();

}
