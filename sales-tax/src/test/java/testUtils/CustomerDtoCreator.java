package testUtils;

import com.complyt.v1.models.AddressDto;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.CustomerTypeDto;
import com.complyt.v1.models.timestamps.ComplytTimestampDto;
import com.complyt.v1.models.timestamps.TimestampsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class CustomerDtoCreator {
    public static CustomerDto create() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        String name = "Existing Customer";
        AddressDto address = new AddressDto("City", "Country", "County", "State", "Street", "Zip");
        ComplytTimestampDto complytTimestampDto = new ComplytTimestampDto(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        TimestampsDto timestampsDto = new TimestampsDto(complytTimestampDto, complytTimestampDto);

        return new CustomerDto(id, externalId, name, address, CustomerTypeDto.RETAIL, timestampsDto, timestampsDto);
    }
}
