package io.complyt.config.web_clients;

import io.complyt.business.webclients.addressvalidations.FastTaxGetBestMatchWebClientWrapper;
import io.complyt.business.webclients.addressvalidations.StubFastTaxWebClientWrapper;
import org.javatuples.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FastTaxWebClientWrapperConfigTest {

    @InjectMocks
    FastTaxWebClientWrapperConfig salesTaxWebClientWrapperConfig;

    @Mock
    WebClient webClient;

    @Mock
    WebClientWrapperProperties fastTaxGetBestMatchWebClientWrapperProperties;

    @Test
    void fastTaxGetBestMatchWebClientWrapper_SetInstance_ReturnInstance() {
        when(fastTaxGetBestMatchWebClientWrapperProperties.getScheme()).thenReturn("scheme");
        when(fastTaxGetBestMatchWebClientWrapperProperties.getHost()).thenReturn("host");
        when(fastTaxGetBestMatchWebClientWrapperProperties.getPath()).thenReturn("path");
        when(fastTaxGetBestMatchWebClientWrapperProperties.getKey()).thenReturn(new Pair<>("key", "test-value"));


        FastTaxGetBestMatchWebClientWrapper expectedFastTaxGetBestMatchWebClientWrapper = new FastTaxGetBestMatchWebClientWrapper(webClient,
                fastTaxGetBestMatchWebClientWrapperProperties.getScheme(),
                fastTaxGetBestMatchWebClientWrapperProperties.getScheme(),
                fastTaxGetBestMatchWebClientWrapperProperties.getPath(),
                fastTaxGetBestMatchWebClientWrapperProperties.getKey());

        FastTaxGetBestMatchWebClientWrapper actualFastTaxGetBestMatchWebClientWrapper =
                salesTaxWebClientWrapperConfig.fastTaxGetBestMatchWebClientWrapper(webClient, fastTaxGetBestMatchWebClientWrapperProperties);

        assertEquals(expectedFastTaxGetBestMatchWebClientWrapper, actualFastTaxGetBestMatchWebClientWrapper);
    }

    @Test
    void stubFastTaxWebClientWrapper_SetInstance_ReturnInstance() {
        StubFastTaxWebClientWrapper expectedStubFastTaxWebClientWrapper = new StubFastTaxWebClientWrapper();

        StubFastTaxWebClientWrapper actualStubFastTaxWebClientWrapper =
                salesTaxWebClientWrapperConfig.stubFastTaxWebClientWrapper(webClient);

        assertEquals(expectedStubFastTaxWebClientWrapper, actualStubFastTaxWebClientWrapper);
    }
}
