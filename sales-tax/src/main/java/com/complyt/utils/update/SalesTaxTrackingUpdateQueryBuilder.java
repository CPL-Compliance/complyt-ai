package com.complyt.utils.update;

import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.utils.update.UpdateBuilder;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SalesTaxTrackingUpdateQueryBuilder implements UpdateBuilder<SalesTaxTracking> {

    @Override
    public Update build(@NonNull SalesTaxTracking salesTaxTracking) {
        Update update = new Update();

        update.set("economicNexusTracker", salesTaxTracking.getEconomicNexusTracker());
        update.set("appliedDate", salesTaxTracking.getAppliedDate());

        if (!salesTaxTracking.getNexusStateRule().timeFrame().equals(TimeFrame.PREVIOUS_TWELVE_MONTHS)) {
            update.set("transactionNexusSummaries", salesTaxTracking.getTransactionNexusSummaries());

            Map<String, NexusCalculationSummary> stringKeysSummaries = salesTaxTracking.getNexusCalculationSummaries().entrySet().stream()
                    .collect(Collectors.toMap(entry -> entry.getKey().format(DateTimeFormatter.ISO_LOCAL_DATE), Map.Entry::getValue));
            update.set("nexusCalculationSummaries", stringKeysSummaries);
        }

        return update;
    }

}