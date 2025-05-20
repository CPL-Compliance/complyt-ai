package com.complyt.domain.mappers;

import com.complyt.domain.Address;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import com.complyt.domain.internal_rates.FilingMetaData;
import com.complyt.domain.internal_rates.InternalSalesTaxRatesMetaData;
import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.*;

class CommonSalesTaxRatesToSalesTaxRatesMapperTest {

    private final CommonSalesTaxRatesToSalesTaxRatesMapper mapper = CommonSalesTaxRatesToSalesTaxRatesMapper.INSTANCE;
    private final InternalSalesTaxRatesMetaData internalSalesTaxRatesMetaData = TestUtilities.createStubInternalSalesTaxRatesMetaData();
    private CommonSalesTaxRates commonSalesTaxRates = TestUtilities.createExternalCommonSalesTaxRates();

    @Test
    void toFilingMetaData_nullInput_returnsNull() {
        assertNull(mapper.toFilingMetaData(null));
    }

    @Test
    void toFilingMetaData_nullMetaData_returnsNull() {
        commonSalesTaxRates = commonSalesTaxRates.withSalesTaxRates(null);
        assertNull(mapper.toFilingMetaData(commonSalesTaxRates));
    }

    @Test
    void toFilingMetaData_nullSalesTaxRates_returnsNull() {
        commonSalesTaxRates = commonSalesTaxRates
                .withRatesMetaData(null);

        assertNull(mapper.toFilingMetaData(commonSalesTaxRates));
    }

    @Test
    void toFilingMetaData_nullAddress_returnsNull() {
        commonSalesTaxRates = commonSalesTaxRates
                .withRatesMetaData(internalSalesTaxRatesMetaData)
                .withAddress(null);

        assertNull(mapper.toFilingMetaData(commonSalesTaxRates));
    }

    @Test
    void toFilingMetaData_validInput_returnsCorrectFilingMetaData() {
        commonSalesTaxRates = commonSalesTaxRates.withRatesMetaData(internalSalesTaxRatesMetaData);
        FilingMetaData result = mapper.toFilingMetaData(commonSalesTaxRates);

        assertNotNull(result);
        assertEquals(commonSalesTaxRates.address().city(), result.getCity());
        assertEquals(commonSalesTaxRates.address().county(), result.getCounty());
        assertEquals(commonSalesTaxRates.ratesMetaData().getOther1Rate(), result.getOther1Rate());
        assertEquals(commonSalesTaxRates.ratesMetaData().getOther2Rate(), result.getOther2Rate());
        assertEquals(commonSalesTaxRates.ratesMetaData().getOther3Rate(), result.getOther3Rate());
        assertEquals(commonSalesTaxRates.ratesMetaData().getOther4Rate(), result.getOther4Rate());
        assertEquals(commonSalesTaxRates.ratesMetaData().getCountyRptCode(), result.getCountyRptCode());
    }
}
