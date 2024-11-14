package com.example.java.tests.integration;


import com.example.java.TestUtils.TestConstants;
import com.example.java.domain.BatteryUser;
import com.example.java.domain.BatteryUserPrincipal;
import com.example.java.domain.Tenant;
import com.example.java.domain.UserTenants;
import com.example.java.domain.UserTenantsPrimaryKey;
import com.example.java.dto.TenantDTO;
import com.example.java.repository.BatteryUserRepository;
import com.example.java.repository.TenantRepository;
import com.example.java.repository.UserTenantsRepository;
import com.example.java.utils.JwtTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Profile("test")
@ActiveProfiles("test")
public class UserTenantsControllerIntegrationTest {

    /**
     * Context.
     */
    @Autowired
    private WebApplicationContext context;
    /**
     * MockMvc used to send requests.
     */
    private MockMvc mockMvc;
    /**
     * Repository used for tests.
     */
    @Autowired
    private UserTenantsRepository userTenantsRepository;
    /**
     * Repository used for tests.
     */
    @Autowired
    private BatteryUserRepository batteryUserRepository;
    /**
     * Repository used for tests.
     */
    @Autowired
    private TenantRepository tenantRepository;
    /**
     * Mocked tenant id.
     */
    private static final String TENANT_ID_MOCK = TestConstants.TENANT_ID_MOCK;
    /**
     * Url where the requests will be sent.
     */
    private static final String BASE_URL = "/api/v1/tenants";
    /**
     * Mapper used to write objects as json.
     */
    private ObjectMapper ow;
    /**
     * Mocked tenant.
     */
    private Tenant tenantMock;
    /**
     * Mocked battery user.
     */
    private BatteryUser batteryUserMock;
    /**
     * Mocked user tenant object.
     */
    private UserTenants userTenantsMock;
    /**
     * Token utils used for testing.
     */
    @Autowired
    private JwtTokenUtils tokenUtils;

    /**
     * Setup made before each request.
     */
    @BeforeEach
    public void setup() {
        ow = new ObjectMapper();
        // we are setting the context before each call
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        batteryUserMock = new BatteryUser();
        batteryUserMock.setPassword("test3");
        batteryUserMock.setEmail("test3");
        batteryUserRepository.save(batteryUserMock);

        tenantMock = new Tenant(TENANT_ID_MOCK);
        tenantRepository.save(tenantMock);

        Optional<BatteryUser> batteryUserFromMockedDb = batteryUserRepository.findByEmail("test3");
        UserTenantsPrimaryKey primaryKey =
                new UserTenantsPrimaryKey(batteryUserFromMockedDb.get().getId(), tenantMock.getTenantId());
        userTenantsMock = new UserTenants(primaryKey, batteryUserFromMockedDb.get(), tenantMock);

        userTenantsRepository.save(userTenantsMock);

    }

    /**
     * Cleanup made after each request.
     */
    @AfterEach
    public void cleanup() {
        userTenantsRepository.delete(userTenantsMock);
        batteryUserRepository.delete(batteryUserMock);
        tenantRepository.delete(tenantMock);
    }

    /**
     * Successfully test case for getAllTenants.
     *
     * @throws Exception - exception.,
     */
    @Test
    public void getAllTenantsTestSuccesfull() throws Exception {
        Pageable pageable = PageRequest.of(0, TestConstants.GOOD_PAGE_SIZE);
        List<TenantDTO> tenantsMock = new ArrayList<>();
        tenantsMock.add(new TenantDTO(tenantMock.getTenantId()));
        Page<TenantDTO> tenantsPageMock = new PageImpl<>(tenantsMock, pageable, 1);

        String tokenMock = tokenUtils.generateToken(new BatteryUserPrincipal(batteryUserMock));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "?page=0&size=20" + "&tenant=" + TENANT_ID_MOCK)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenMock))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(ow.writeValueAsString(tenantsPageMock)));

    }

    /**
     * Failure test case for getAllTenants.
     *
     * @throws Exception - exception.
     */
    @Test
    public void getAllTenantsTestFailure() throws Exception {

        String tokenMock = tokenUtils.generateToken(new BatteryUserPrincipal(batteryUserMock));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "?page=asfasfas&size=-20" + "&tenant=" + TENANT_ID_MOCK)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenMock))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

    }
}
