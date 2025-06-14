package com.myslotify.slotify.controller;

import com.myslotify.slotify.entity.Tenant;
import com.myslotify.slotify.service.TenantService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TenantController.class)
class TenantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TenantService tenantService;

    @Test
    void getTenantByKeyReturnsOk() throws Exception {
        Mockito.when(tenantService.getTenantByKey("tenant1")).thenReturn(new Tenant());

        mockMvc.perform(get("/api/tenants/key/tenant1"))
                .andExpect(status().isOk());
    }
}
