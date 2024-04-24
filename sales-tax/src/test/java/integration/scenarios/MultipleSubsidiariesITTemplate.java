package integration.scenarios;

public interface MultipleSubsidiariesITTemplate {

    void getOneSalesTaxTracking_SubsidiaryB_EstablishedByIsNullAndEconomicNexusEstablishedIsFalse();

    void getOneSalesTaxTracking_NullSubsidiary_EstablishedByIsNullAndEconomicNexusEstablishedIsFalse();

    void upsert_FirstTransactionToNullSubsidiary_AddsCalculationToNullSubsidiary_DidNotPassNexus();

    void upsert_SecondTransactionWithNonExistingSubsidiary_AddsCalculationToNullSubsidiary_DidNotPassNexus();

    void upsert_FirstTransactionToSubsidiaryA_AddsCalculationToSubsidiaryA_DidNotPassNexus();

    void upsert_FirstTransactionToSubsidiaryB_AddsCalculationToSubsidiaryB_DidNotPassNexus();

    void upsert_FirstTransactionToSubsidiaryC_AddsCalculationToSubsidiaryC_DidNotPassNexus();

    void upsert_SecondTransactionToSubsidiaryC_AddsCalculationToSubsidiaryC_DidNotPassNexus();

    void upsert_SecondTransactionToSubsidiaryB_AddsCalculationToSubsidiaryB_PassedNexus();

    void upsert_ThirdTransactionToNullSubsidiaryB_TransactionReturnedWithSalesTax();

    void upsert_SecondTransactionToSubsidiaryA_TransactionReturnedWithSalesTax();

    void upsert_SecondTransactionToNullSubsidiary_TransactionReturnedWithSalesTax();

    void upsert_ThirdTransactionToNullSubsidiaryC_TransactionReturnedWithSalesTax();

    void upsert_TransactionWithNonExistingSubsidiary_TransactionReturnedWithSalesTax();

    void getOneSalesTaxTracking_SubsidiaryA_EstablishedBySubsidiaryB();

    void getOneSalesTaxTracking_SubsidiaryB_EstablishedBySubsidiaryB();

    void getOneSalesTaxTracking_SubsidiaryC_EstablishedBySubsidiaryB();

    void getOneSalesTaxTracking_NullSubsidiary_EstablishedBySubsidiaryB();
}
