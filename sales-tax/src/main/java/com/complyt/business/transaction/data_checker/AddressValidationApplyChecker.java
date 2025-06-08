package com.complyt.business.transaction.data_checker;

import com.complyt.business.address.CountryIsUsaChecker;
import com.complyt.business.tax.sales_tax.checker.SalesTaxApplyCheck;
import com.complyt.domain.decorator.SalesTaxTrackingWithNexusInfo;
import com.complyt.domain.transaction.Transaction;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AddressValidationApplyChecker {

    @Deprecated
    public boolean shouldValidateAddress(@NonNull Transaction transaction, @NonNull SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo) {
        SalesTaxApplyCheck salesTaxApplyCheck = new SalesTaxApplyCheck(transaction);
        boolean isHasNexus = salesTaxTrackingWithNexusInfo.isHasNexus();
        boolean isApplied = salesTaxApplyCheck.check(salesTaxTrackingWithNexusInfo.getSalesTaxTracking());
        boolean shouldCallAddressValidation = !isHasNexus || !isApplied;
        return CountryIsUsaChecker.isCountryUsa(transaction.getShippingAddress()) && shouldCallAddressValidation;
    }
}
