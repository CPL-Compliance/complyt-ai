package io.complyt.domain.fast_tax;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.complyt.domain.AddressData;
import io.complyt.utils.exceptions.types.fastTax.FastTaxError;
import lombok.*;

import java.util.List;

@Getter
@EqualsAndHashCode
@With
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class FastTaxGetBestMatchData implements AddressData {
    String matchLevel;
    List<TaxInfoItem> taxInfoItems;
    FastTaxError error;
}