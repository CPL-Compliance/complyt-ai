package integration.test_utils.templates.endpoints;

public interface UpsertByExternalIdAndSourceWithExemptionTemplate {
    void upsertByExternalIdAndSource_CustomerIsExemptByStateAndDate_ReturnsNonTaxableTransaction();
    void upsertByExternalIdAndSource_CustomerIsNotExemptByStateAndDate_ReturnsTaxableTransaction();
    void upsertByExternalIdAndSource_CustomerIsNotExemptBecauseExemptionIsCancelled_ReturnsTaxableTransaction();
    void upsertByExternalIdAndSource_CustomerIsFullyExemptAndPartiallyExemption_Exempt();
    void upsertByExternalIdAndSource_CustomerIsPartiallyExempt_NotExempted();
    void upsertByExternalIdAndSource_CustomerIsNotNoExempt_NoExempted();
    void upsertByExternalIdAndSource_NotActiveExemptionAndFullyExempt_Exempted();
    void upsertByExternalIdAndSource_NotActiveExemption_NoExempted();
}
