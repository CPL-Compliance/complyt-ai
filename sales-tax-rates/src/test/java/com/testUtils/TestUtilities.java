package com.testUtils;

import com.complyt.domain.Address;
import com.complyt.domain.AddressWithSalesTaxRates;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.fast_tax.FastTaxData;
import com.complyt.domain.fast_tax.TaxInfoItem;
import com.complyt.domain.zip_tax.Result;

import java.util.UUID;

import com.complyt.v1.model.AddressDto;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.List;

public class TestUtilities {

    public static Address createAddressInNewYork() {
        return new Address("New York", "US", null, "NY", "160 Broadway", "10038");
    }

    public static SalesTaxRates createNewYorkSalesTaxRates() {
        return new SalesTaxRates(0.0f, 0.045f, 0.00375f, 0.0f, 0.04f, 0.08875f);
    }

    public static AddressWithSalesTaxRates createNewYorkAddressWithSalesTaxRates() {
        Address address = createAddressInNewYork();
        SalesTaxRates salesTaxRates = createNewYorkSalesTaxRates();
        LocalDateTime now = LocalDateTime.now();
        return new AddressWithSalesTaxRates(UUID.randomUUID().toString(), address, salesTaxRates, now, now.plusMinutes(1));
    }

    public static Address createAddressInCalifornia() {
        return new Address("Fresno", "US", null, "CA", "7498 N Remington Ave", "93711-5508");
    }

    public static AddressDto createAddressDtoInCalifornia() {
        return new AddressDto("Fresno", "US", null, "CA", "7498 N Remington Ave", "93711-5508");
    }

    public static SalesTaxRates createCaliforniaSalesTaxRates() {
        return new SalesTaxRates(0.00375f, 0.0f, 0.00725f, 0.0125f, 0.06f, 0.0835f);
    }

    public static AddressWithSalesTaxRates createCaliforniaAddressWithSalesTaxRates() {
        Address address = createAddressInCalifornia();
        SalesTaxRates salesTaxRates = createCaliforniaSalesTaxRates();
        LocalDateTime now = LocalDateTime.now();
        return new AddressWithSalesTaxRates(UUID.randomUUID().toString(), address, salesTaxRates, now, now.plusMinutes(1));
    }

    public static Query createAddressSearchQuery(Address address) {
        return Query.query(Criteria
                .where("address.city").is(address.getCity())
                .and("address.street").is(address.getStreet())
                .and("address.zip").is(address.getZip()));
    }

    public static FastTaxData createFastTaxData() {
        String matchLevel = "Address";
        TaxInfoItem taxInfoItem = new TaxInfoItem("Fresno", "0.00375", "0", "Fresno", "0.00725", "0.0125", null, "", "", "0", "CA", "California", "0.06", "0.0835", "LABOR/FREIGHT/SERVICES", "93711-5508");
        List<TaxInfoItem> taxInfoItems = List.of(taxInfoItem);
        return new FastTaxData(matchLevel, taxInfoItems);
    }

    public static Result createResult() {
        return new Result("", "", "injectedCounty", "", 0f, 0f, "", "",
                0f, 0f, 0f, 0f, "", 0f, 0, "",
                0f, 0f, "", 0, 0, "", 0,
                0, "", 0, 0, "", 0, 0, "",
                0, 0, "");
    }
}

