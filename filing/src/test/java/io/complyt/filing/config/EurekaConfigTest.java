package io.complyt.filing.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class EurekaConfigTest {

    @InjectMocks
    EurekaConfig eurekaConfig;

    @Mock
    private Environment environment;

    @Mock
    InetUtils inetUtils;

    @BeforeEach
    void setUp() {

    }

    @Test
    void eurekaInstanceConfig() throws UnknownHostException {
        // Given
        String expectedIp = "1.1.1.1";
        String expectedPort = "1234";

        try (MockedStatic<InetAddress> mocked = mockStatic(InetAddress.class)){
            // When
            InetAddress mockInetAddress = mock(InetAddress.class);
            mocked.when(InetAddress::getLocalHost).thenReturn(mockInetAddress);

            // Mocking InetAddress
            when(mockInetAddress.getHostAddress()).thenReturn(expectedIp);
            when(environment.getProperty("server.port")).thenReturn(expectedPort);

            // Mocking InetUtils
            InetUtils.HostInfo hostInfo = new InetUtils.HostInfo();
            hostInfo.setIpAddress(expectedIp);
            when(inetUtils.findFirstNonLoopbackHostInfo()).thenReturn(hostInfo);

            // Then
            EurekaInstanceConfigBean eurekaInstanceConfigBean = eurekaConfig.eurekaInstanceConfig(inetUtils);

            String actualIp = eurekaInstanceConfigBean.getIpAddress();
            int actualPort = eurekaInstanceConfigBean.getNonSecurePort();

            assertEquals(expectedIp, actualIp);
            assertEquals(Integer.parseInt(expectedPort), actualPort);
        }
    }
}