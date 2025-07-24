package com.myslotify.slotify.filter;

import com.myslotify.slotify.service.JwtService;
import com.myslotify.slotify.util.TenantContext;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantFilterTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private TenantFilter tenantFilter;

    @BeforeEach
    void clear() {
        TenantContext.clear();
    }

    @Test
    void setsTenantFromTokenWhenHeaderMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractTenant("token")).thenReturn("tenant1");

        FilterChain chain = (req, res) -> assertEquals("tenant1", TenantContext.getCurrentTenant());

        tenantFilter.doFilterInternal(request, response, chain);

        assertNull(TenantContext.getCurrentTenant(), "Tenant context should be cleared");
    }
}
