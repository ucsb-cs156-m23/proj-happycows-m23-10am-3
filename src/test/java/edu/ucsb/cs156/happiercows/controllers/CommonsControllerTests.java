package edu.ucsb.cs156.happiercows.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ucsb.cs156.happiercows.ControllerTestCase;
import edu.ucsb.cs156.happiercows.entities.CommonStats;
import edu.ucsb.cs156.happiercows.entities.Commons;
import edu.ucsb.cs156.happiercows.entities.CommonsPlus;
import edu.ucsb.cs156.happiercows.entities.User;
import edu.ucsb.cs156.happiercows.entities.UserCommons;
import edu.ucsb.cs156.happiercows.models.CreateCommonsParams;
import edu.ucsb.cs156.happiercows.models.HealthUpdateStrategyList;
import edu.ucsb.cs156.happiercows.repositories.CommonStatsRepository;
import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.UserRepository;
import edu.ucsb.cs156.happiercows.strategies.CowHealthUpdateStrategies;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommonsController.class)
@AutoConfigureDataJpa
public class CommonsControllerTests extends ControllerTestCase {

    @MockBean
    UserCommonsRepository userCommonsRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    CommonsRepository commonsRepository;

    @MockBean
    CommonStatsRepository commonStatsRepository;


    @Autowired
    private ObjectMapper objectMapper;

    private User user = User
                        .builder()
                        .id(42L)
                        .fullName("Chris Gaucho")
                        .email("cgaucho@example.org")
                        .build();

        private Commons commons = Commons
                        .builder()
                        .id(17L)
                        .name("test commons")
                        .cowPrice(10)
                        .milkPrice(2)
                        .startingBalance(300)
                        .startingDate(LocalDateTime.parse("2022-03-05T15:50:10"))
                        .showLeaderboard(true)
                        .carryingCapacity(100)
                        .degradationRate(0.01)
                        .belowCapacityHealthUpdateStrategy(CowHealthUpdateStrategies.Linear)
                        .aboveCapacityHealthUpdateStrategy(CowHealthUpdateStrategies.Linear)
                        .build();

        UserCommons userCommons = UserCommons
                        .builder()
                        .user(user)
                        .username("Chris Gaucho")
                        .commons(commons)
                        .totalWealth(300)
                        .numOfCows(123)
                        .cowHealth(10)
                        .cowsBought(78)
                        .cowsSold(23)
                        .cowDeaths(6)
                        .build();
        
        CommonStats expectedCommonStats = CommonStats
                        .builder()
                        .commonsId(17L)
                        .numCows(123)
                        .avgHealth(10.0)
                        .timestamp(LocalDateTime.now())
                        .build();

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void createCommonsTest() throws Exception {
        LocalDateTime someTime = LocalDateTime.parse("2022-03-05T15:50:10");

        Commons commons = Commons.builder()
                .name("Jackson's Commons")
                .cowPrice(500.99)
                .milkPrice(8.99)
                .startingBalance(1020.10)
                .startingDate(someTime)
                .degradationRate(50.0)
                .showLeaderboard(false)
                .carryingCapacity(100)
                .capacityPerUser(50)
                .aboveCapacityHealthUpdateStrategy(CowHealthUpdateStrategies.Constant)
                .belowCapacityHealthUpdateStrategy(CowHealthUpdateStrategies.Linear)
                .build();

        CreateCommonsParams parameters = CreateCommonsParams.builder()
                .name("Jackson's Commons")
                .cowPrice(500.99)
                .milkPrice(8.99)
                .startingBalance(1020.10)
                .startingDate(someTime)
                .degradationRate(50.0)
                .showLeaderboard(false)
                .carryingCapacity(100)
                .capacityPerUser(50)
                .aboveCapacityHealthUpdateStrategy(CowHealthUpdateStrategies.Constant.name())
                .belowCapacityHealthUpdateStrategy(CowHealthUpdateStrategies.Linear.name())
                .build();

        String requestBody = objectMapper.writeValueAsString(parameters);
        String expectedResponse = objectMapper.writeValueAsString(commons);

        when(commonsRepository.save(commons))
                .thenReturn(commons);

        MvcResult response = mockMvc
                .perform(post("/api/commons/new").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        verify(commonsRepository, times(1)).save(commons);

        String actualResponse = response.getResponse().getContentAsString();
        assertEquals(expectedResponse, actualResponse);
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void createCommonsTest_withNoCowHealthUpdateStrategies() throws Exception {
        LocalDateTime someTime = LocalDateTime.parse("2022-03-05T15:50:10");

        Commons commons = Commons.builder()
                .name("Jackson's Commons")
                .cowPrice(500.99)
                .milkPrice(8.99)
                .startingBalance(1020.10)
                .startingDate(someTime)
                .degradationRate(50.0)
                .showLeaderboard(false)
                .carryingCapacity(100)
                .capacityPerUser(50)
                .build();

        CreateCommonsParams parameters = CreateCommonsParams.builder()
                .name("Jackson's Commons")
                .cowPrice(500.99)
                .milkPrice(8.99)
                .startingBalance(1020.10)
                .startingDate(someTime)
                .degradationRate(50.0)
                .showLeaderboard(false)
                .carryingCapacity(100)
                .capacityPerUser(50)
                .build();

        // don't include null values to simulate old frontend
        var mapperWithoutNulls = objectMapper.copy().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String requestBody = mapperWithoutNulls.writeValueAsString(parameters);

        String expectedResponse = objectMapper.writeValueAsString(commons);

        when(commonsRepository.save(commons))
                .thenReturn(commons);

        MvcResult response = mockMvc
                .perform(post("/api/commons/new").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        verify(commonsRepository, times(1)).save(commons);

        String actualResponse = response.getResponse().getContentAsString();
        assertEquals(expectedResponse, actualResponse);
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void createCommonsTest_zeroDegradation() throws Exception {
        LocalDateTime someTime = LocalDateTime.parse("2022-03-05T15:50:10");

        Commons commons = Commons.builder()
                .name("Jackson's Commons")
                .cowPrice(500.99)
                .milkPrice(8.99)
                .startingBalance(1020.10)
                .startingDate(someTime)
                .degradationRate(0)
                .showLeaderboard(false)
                .carryingCapacity(100)
                .capacityPerUser(50)
                .build();

        CreateCommonsParams parameters = CreateCommonsParams.builder()
                .name("Jackson's Commons")
                .cowPrice(500.99)
                .milkPrice(8.99)
                .startingBalance(1020.10)
                .startingDate(someTime)
                .degradationRate(0)
                .showLeaderboard(false)
                .carryingCapacity(100)
                .capacityPerUser(50)
                .build();

        String requestBody = objectMapper.writeValueAsString(parameters);
        String expectedResponse = objectMapper.writeValueAsString(commons);

        when(commonsRepository.save(commons))
                .thenReturn(commons);

        MvcResult response = mockMvc
                .perform(post("/api/commons/new").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        verify(commonsRepository, times(1)).save(commons);

        String actualResponse = response.getResponse().getContentAsString();
        assertEquals(expectedResponse, actualResponse);
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void createCommonsTest_withIllegalDegradationRate() throws Exception {
        LocalDateTime someTime = LocalDateTime.parse("2022-03-05T15:50:10");

        Commons commons = Commons.builder()
                .name("Jackson's Commons")
                .cowPrice(500.99)
                .milkPrice(8.99)
                .startingBalance(1020.10)
                .startingDate(someTime)
                .degradationRate(-8.49)
                .carryingCapacity(100)
                .capacityPerUser(50)
                .build();

        CreateCommonsParams parameters = CreateCommonsParams.builder()
                .name("Jackson's Commons")
                .cowPrice(500.99)
                .milkPrice(8.99)
                .startingBalance(1020.10)
                .startingDate(someTime)
                .degradationRate(-8.49)
                .carryingCapacity(100)
                .capacityPerUser(50)
                .build();

        String requestBody = objectMapper.writeValueAsString(parameters);

        when(commonsRepository.save(commons))
                .thenReturn(commons);

        MvcResult response = mockMvc
                .perform(post("/api/commons/new").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody))
                .andExpect(status().isBadRequest()).andReturn();

        assertInstanceOf(IllegalArgumentException.class, response.getResolvedException());
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void getCommonsTest() throws Exception {
        List<Commons> expectedCommons = new ArrayList<Commons>();
        Commons Commons1 = Commons.builder().name("TestCommons1").build();

        expectedCommons.add(Commons1);
        when(commonsRepository.findAll()).thenReturn(expectedCommons);
        MvcResult response = mockMvc.perform(get("/api/commons/all").contentType("application/json"))
                .andExpect(status().isOk()).andReturn();

        verify(commonsRepository, times(1)).findAll();

        String responseString = response.getResponse().getContentAsString();
        List<Commons> actualCommons = objectMapper.readValue(responseString, new TypeReference<List<Commons>>() {
        });
        assertEquals(actualCommons, expectedCommons);
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void updateCommonsTest() throws Exception {
        LocalDateTime someTime = LocalDateTime.parse("2022-03-05T15:50:10");

        CreateCommonsParams parameters = CreateCommonsParams.builder()
                .name("Jackson's Commons")
                .cowPrice(500.99)
                .milkPrice(8.99)
                .startingBalance(1020.10)
                .startingDate(someTime)
                .degradationRate(50.0)
                .showLeaderboard(true)
                .carryingCapacity(100)
                .capacityPerUser(50)
                .aboveCapacityHealthUpdateStrategy(CowHealthUpdateStrategies.Constant.name())
                .belowCapacityHealthUpdateStrategy(CowHealthUpdateStrategies.Linear.name())
                .build();

        Commons commons = Commons.builder()
                .name("Jackson's Commons")
                .cowPrice(500.99)
                .milkPrice(8.99)
                .startingBalance(1020.10)
                .startingDate(someTime)
                .degradationRate(50.0)
                .showLeaderboard(true)
                .carryingCapacity(100)
                .capacityPerUser(50)
                .aboveCapacityHealthUpdateStrategy(CowHealthUpdateStrategies.Constant)
                .belowCapacityHealthUpdateStrategy(CowHealthUpdateStrategies.Linear)
                .build();

        String requestBody = objectMapper.writeValueAsString(parameters);

        when(commonsRepository.save(commons))
                .thenReturn(commons);

        mockMvc
                .perform(put("/api/commons/update?id=0").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(commonsRepository, times(1)).save(commons);

        parameters.setMilkPrice(parameters.getMilkPrice() + 3.00);
        commons.setMilkPrice(parameters.getMilkPrice());
        parameters.setDegradationRate(parameters.getDegradationRate() + 1.00);
        commons.setDegradationRate(parameters.getDegradationRate());
        parameters.setShowLeaderboard(false);
        commons.setShowLeaderboard(parameters.getShowLeaderboard());
        parameters.setCarryingCapacity(123);
        commons.setCarryingCapacity(parameters.getCarryingCapacity());
        parameters.setCapacityPerUser(100);
        commons.setCapacityPerUser(parameters.getCapacityPerUser());
        parameters.setAboveCapacityHealthUpdateStrategy(CowHealthUpdateStrategies.Linear.name());
        commons.setAboveCapacityHealthUpdateStrategy(CowHealthUpdateStrategies.Linear);
        parameters.setBelowCapacityHealthUpdateStrategy(CowHealthUpdateStrategies.Noop.name());
        commons.setBelowCapacityHealthUpdateStrategy(CowHealthUpdateStrategies.Noop);

        requestBody = objectMapper.writeValueAsString(parameters);

        when(commonsRepository.findById(0L))
                .thenReturn(Optional.of(commons));

        when(commonsRepository.save(commons))
                .thenReturn(commons);

        mockMvc
                .perform(put("/api/commons/update?id=0").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody))
                .andExpect(status().isNoContent());

        verify(commonsRepository, times(1)).save(commons);
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void updateCommonsTest_withNoCowHealthUpdateStrategy() throws Exception {
        LocalDateTime someTime = LocalDateTime.parse("2022-03-05T15:50:10");

        CreateCommonsParams parameters = CreateCommonsParams.builder()
                .name("Jackson's Commons")
                .cowPrice(500.99)
                .milkPrice(8.99)
                .startingBalance(1020.10)
                .startingDate(someTime)
                .degradationRate(50.0)
                .showLeaderboard(true)
                .carryingCapacity(100)
                .capacityPerUser(50)
                .build();

        Commons commons = Commons.builder()
                .name("Jackson's Commons")
                .cowPrice(500.99)
                .milkPrice(8.99)
                .startingBalance(1020.10)
                .startingDate(someTime)
                .degradationRate(50.0)
                .showLeaderboard(true)
                .carryingCapacity(100)
                .capacityPerUser(50)
                .aboveCapacityHealthUpdateStrategy(CowHealthUpdateStrategies.Constant)
                .belowCapacityHealthUpdateStrategy(CowHealthUpdateStrategies.Linear)
                .build();

        var objectMapperWithoutNulls = objectMapper.copy()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String requestBody = objectMapperWithoutNulls.writeValueAsString(parameters);

        when(commonsRepository.save(commons))
                .thenReturn(commons);
        when(commonsRepository.findById(0L))
                .thenReturn(Optional.of(commons));

        mockMvc
                .perform(put("/api/commons/update?id=0").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody))
                .andExpect(status().isNoContent());

        verify(commonsRepository, times(1)).save(commons);

        assertEquals(CowHealthUpdateStrategies.Constant, commons.getAboveCapacityHealthUpdateStrategy());
        assertEquals(CowHealthUpdateStrategies.Linear, commons.getBelowCapacityHealthUpdateStrategy());
    }


    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void updateCommonsTest_withDegradationRate_Zero() throws Exception {
        LocalDateTime someTime = LocalDateTime.parse("2022-03-05T15:50:10");

        CreateCommonsParams parameters = CreateCommonsParams.builder()
                .name("Jackson's Commons")
                .cowPrice(500.99)
                .milkPrice(8.99)
                .startingBalance(1020.10)
                .startingDate(someTime)
                .degradationRate(8.49)
                .showLeaderboard(false)
                .carryingCapacity(100)
                .capacityPerUser(50)
                .build();

        Commons commons = Commons.builder()
                .name("Jackson's Commons")
                .cowPrice(500.99)
                .milkPrice(8.99)
                .startingBalance(1020.10)
                .startingDate(someTime)
                .degradationRate(8.49)
                .showLeaderboard(false)
                .carryingCapacity(100)
                .capacityPerUser(50)
                .build();

        String requestBody = objectMapper.writeValueAsString(parameters);

        when(commonsRepository.save(commons))
                .thenReturn(commons);

        mockMvc
                .perform(put("/api/commons/update?id=0").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(commonsRepository, times(1)).save(commons);

        parameters.setMilkPrice(parameters.getMilkPrice() + 3.00);
        commons.setMilkPrice(parameters.getMilkPrice());
        parameters.setDegradationRate(0);
        commons.setDegradationRate(parameters.getDegradationRate());
        parameters.setCarryingCapacity(123);
        commons.setCarryingCapacity(parameters.getCarryingCapacity());
        parameters.setCapacityPerUser(100);
        commons.setCapacityPerUser(parameters.getCapacityPerUser());

        requestBody = objectMapper.writeValueAsString(parameters);

        when(commonsRepository.findById(0L))
                .thenReturn(Optional.of(commons));

        when(commonsRepository.save(commons))
                .thenReturn(commons);

        mockMvc
                .perform(put("/api/commons/update?id=0").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody))
                .andExpect(status().isNoContent());

        verify(commonsRepository, times(1)).save(commons);
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void updateCommonsTest_withIllegalDegradationRate() throws Exception {
        LocalDateTime someTime = LocalDateTime.parse("2022-03-05T15:50:10");

        CreateCommonsParams parameters = CreateCommonsParams.builder()
                .name("Jackson's Commons")
                .cowPrice(500.99)
                .milkPrice(8.99)
                .startingBalance(1020.10)
                .startingDate(someTime)
                .degradationRate(8.49)
                .showLeaderboard(false)
                .carryingCapacity(100)
                .capacityPerUser(50)
                .build();

        Commons commons = Commons.builder()
                .name("Jackson's Commons")
                .cowPrice(500.99)
                .milkPrice(8.99)
                .startingBalance(1020.10)
                .startingDate(someTime)
                .degradationRate(8.49)
                .showLeaderboard(false)
                .carryingCapacity(100)
                .capacityPerUser(50)
                .build();

        String requestBody = objectMapper.writeValueAsString(parameters);

        when(commonsRepository.save(commons))
                .thenReturn(commons);

        mockMvc
                .perform(put("/api/commons/update?id=0").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(commonsRepository, times(1)).save(commons);

        parameters.setDegradationRate(-10);
        commons.setDegradationRate(parameters.getDegradationRate());

        requestBody = objectMapper.writeValueAsString(parameters);

        when(commonsRepository.findById(0L))
                .thenReturn(Optional.of(commons));

        when(commonsRepository.save(commons))
                .thenReturn(commons);

        MvcResult response = mockMvc
                .perform(put("/api/commons/update?id=0").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody))
                .andExpect(status().isBadRequest()).andReturn();

        assertInstanceOf(IllegalArgumentException.class, response.getResolvedException());
    }

    // This common SHOULD be in the repository
    @WithMockUser(roles = {"USER"})
    @Test
    public void getCommonsByIdTest_valid() throws Exception {
        Commons Commons1 = Commons.builder()
                .name("TestCommons2")
                .id(18L)
                .build();

        when(commonsRepository.findById(eq(18L))).thenReturn(Optional.of(Commons1));

        MvcResult response = mockMvc.perform(get("/api/commons?id=18"))
                .andExpect(status().isOk()).andReturn();

        verify(commonsRepository, times(1)).findById(eq(18L));
        String expectedJson = mapper.writeValueAsString(Commons1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    // This commons SHOULD be in the repository
    @WithMockUser(roles = {"USER"})
    @Test
    public void getCommonsPlusByIdTest_valid() throws Exception {

        Commons Commons1 = Commons.builder()
                .name("TestCommons2")
                .id(18L)
                .carryingCapacity(100)
                .build();
        CommonsPlus commonsPlus = CommonsPlus.builder()
                .commons(Commons1)
                .totalCows(5)
                .totalUsers(2)
                .effectiveCapacity(100)
                .build();
                
        when(commonsRepository.findById(eq(18L))).thenReturn(Optional.of(Commons1));
        when(commonsRepository.getNumCows(18L)).thenReturn(Optional.of(5));
        when(commonsRepository.getNumUsers(18L)).thenReturn(Optional.of(2));

        MvcResult response = mockMvc.perform(get("/api/commons/plus?id=18"))
                .andExpect(status().isOk()).andReturn();

        verify(commonsRepository, times(1)).findById(eq(18L));
        String expectedJson = mapper.writeValueAsString(commonsPlus);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    // This common SHOULD NOT be in the repository
    @WithMockUser(roles = {"USER"})
    @Test
    public void getCommonsByIdTest_invalid() throws Exception {

        when(commonsRepository.findById(eq(18L))).thenReturn(Optional.empty());

        MvcResult response = mockMvc.perform(get("/api/commons?id=18"))
                .andExpect(status().is(404)).andReturn();

        verify(commonsRepository, times(1)).findById(eq(18L));

        Map<String, Object> responseMap = responseToJson(response);

        assertEquals(responseMap.get("message"), "Commons with id 18 not found");
        assertEquals(responseMap.get("type"), "EntityNotFoundException");
    }

    // This commons SHOULD NOT be in the repository
    @WithMockUser(roles = {"USER"})
    @Test
    public void getCommonsPlusByIdTest_invalid() throws Exception {                
        when(commonsRepository.findById(eq(18L))).thenReturn(Optional.empty());

        MvcResult response = mockMvc.perform(get("/api/commons/plus?id=18"))
                .andExpect(status().is(404)).andReturn();

        verify(commonsRepository, times(1)).findById(eq(18L));

        Map<String, Object> responseMap = responseToJson(response);

        assertEquals(responseMap.get("message"), "Commons with id 18 not found");
        assertEquals(responseMap.get("type"), "EntityNotFoundException");
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void getHealthUpdateStrategiesTest() throws Exception {
        var response = mockMvc.perform(
                get("/api/commons/all-health-update-strategies")
        ).andExpect(status().isOk()).andReturn();

        var expected = HealthUpdateStrategyList.create();
        var actual = mapper.readValue(response.getResponse().getContentAsString(), HealthUpdateStrategyList.class);
        assertEquals(expected, actual);

    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void joinCommonsTest() throws Exception {

        Commons c = Commons.builder()
                .id(2L)
                .name("Example Commons")
                .build();

        UserCommons uc = UserCommons.builder()
                .user(currentUserService.getUser())
                .commons(c)
                .username("Fake user")
                .totalWealth(0)
                .numOfCows(0)
                .cowHealth(100)
                .build();


        when(userCommonsRepository.findByCommonsIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(userCommonsRepository.save(eq(uc))).thenReturn(uc);
        when(commonsRepository.findById(eq(2L))).thenReturn(Optional.of(c));

        MvcResult response = mockMvc
                .perform(post("/api/commons/join?commonsId=2").with(csrf()))
                .andExpect(status().isOk()).andReturn();

        verify(userCommonsRepository, times(1)).findByCommonsIdAndUserId(2L, 1L);
        verify(userCommonsRepository, times(1)).save(uc);
        
        String responseString = response.getResponse().getContentAsString();
        String cAsJson = mapper.writeValueAsString(c);

        assertEquals(responseString, cAsJson);
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void already_joined_common_test() throws Exception {

        Commons c = Commons.builder()
                .id(2L)
                .name("Example Commons")
                .build();

        UserCommons uc = UserCommons.builder()
                .user(currentUserService.getUser())
                .commons(c)
                .username("1L")
                .totalWealth(0)
                .numOfCows(1)
                .build();

        String requestBody = mapper.writeValueAsString(uc);

        // Instead of returning empty, we instead say that it already exists. We
        // shouldn't create a new entry.
        when(userCommonsRepository.findByCommonsIdAndUserId(2L, 1L)).thenReturn(Optional.of(uc));
        when(userCommonsRepository.save(eq(uc))).thenReturn(uc);

        when(commonsRepository.findById(eq(2L))).thenReturn(Optional.of(c));

        MvcResult response = mockMvc
                .perform(post("/api/commons/join?commonsId=2").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8").content(requestBody))
                .andExpect(status().isOk()).andReturn();

        verify(userCommonsRepository, times(1)).findByCommonsIdAndUserId(2L, 1L);

        String responseString = response.getResponse().getContentAsString();
        String cAsJson = mapper.writeValueAsString(c);

        assertEquals(responseString, cAsJson);
    }


    @WithMockUser(roles = {"USER"})
    @Test
    public void join_when_commons_with_id_does_not_exist() throws Exception {

        when(commonsRepository.findById(eq(2L))).thenReturn(Optional.empty());

        MvcResult response = mockMvc
                .perform(post("/api/commons/join?commonsId=2").with(csrf()))
                .andExpect(status().is(404)).andReturn();

        verify(commonsRepository, times(1)).findById(eq(2L));

        Map<String, Object> responseMap = responseToJson(response);

        assertEquals(responseMap.get("message"), "Commons with id 2 not found");
        assertEquals(responseMap.get("type"), "EntityNotFoundException");
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void deleteCommons_test_admin_exists() throws Exception {
        LocalDateTime someTime = LocalDateTime.parse("2022-03-05T15:50:10");

        Commons c = Commons.builder()
                .name("Jackson's Commons")
                .cowPrice(500.99)
                .milkPrice(8.99)
                .startingBalance(1020.10)
                .startingDate(someTime)
                .degradationRate(50.0)
                .showLeaderboard(false)
                .carryingCapacity(100)
                .build();

        when(commonsRepository.findById(eq(2L))).thenReturn(Optional.of(c));
        doNothing().when(commonsRepository).deleteById(2L);

        MvcResult response = mockMvc.perform(
                        delete("/api/commons?id=2")
                                .with(csrf()))
                .andExpect(status().is(200)).andReturn();

        verify(commonsRepository, times(1)).findById(2L);
        verify(commonsRepository, times(1)).deleteById(2L);

        String responseString = response.getResponse().getContentAsString();

        String expectedString = "{\"message\":\"commons with id 2 deleted\"}";

        assertEquals(expectedString, responseString);
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void deleteCommons_test_admin_nonexists() throws Exception {

        when(commonsRepository.findById(eq(2L))).thenReturn(Optional.empty());

        MvcResult response = mockMvc.perform(
                        delete("/api/commons?id=2")
                                .with(csrf()))
                .andExpect(status().is(404)).andReturn();
        verify(commonsRepository, times(1)).findById(2L);


        String expectedString = "{\"message\":\"Commons with id 2 not found\",\"type\":\"EntityNotFoundException\"}";

        Map<String, Object> expectedJson = mapper.readValue(expectedString, new TypeReference<Map<String, Object>>() {
        });
        Map<String, Object> jsonResponse = responseToJson(response);
        assertEquals(expectedJson, jsonResponse);
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void deleteUserFromCommonsTest() throws Exception {
        Commons c = Commons.builder()
        .id(2L)
        .name("Example Commons")
        .build();

        UserCommons uc = UserCommons.builder()
                .user(currentUserService.getUser())
                .commons(Commons.builder().id(1).build())
                .username("1L")
                .totalWealth(0)
                .numOfCows(1)
                .build();

        String requestBody = mapper.writeValueAsString(uc);

        when(userCommonsRepository.findByCommonsIdAndUserId(2L, 1L)).thenReturn(Optional.of(uc));
        when(commonsRepository.findById(2L)).thenReturn(Optional.of(c));
        when(commonsRepository.getNumUsers(2L)).thenReturn(Optional.of(0));

        MvcResult response = mockMvc
                .perform(delete("/api/commons/2/users/1").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8").content(requestBody))
                .andExpect(status().is(200)).andReturn();

        verify(userCommonsRepository, times(1)).findByCommonsIdAndUserId(2L, 1L);
        verify(userCommonsRepository, times(1)).delete(uc);

        String responseString = response.getResponse().getContentAsString();
        String expectedString = "{\"message\":\"user with id 1 deleted from commons with id 2, 0 users remain\"}";

        assertEquals(responseString, expectedString);
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void deleteUserFromCommons_when_not_joined() throws Exception {


        when(userCommonsRepository.findByCommonsIdAndUserId(2L, 1L)).thenReturn(Optional.empty());

        mockMvc
                .perform(delete("/api/commons/2/users/1").with(csrf()))
                .andExpect(status().is(404)).andReturn();
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void getCommonsPlusTest() throws Exception {
        List<Commons> expectedCommons = new ArrayList<>();

        Commons Commons1 = Commons.builder().name("TestCommons1").id(1L).carryingCapacity(100).build();
        expectedCommons.add(Commons1);

        List<CommonsPlus> expectedCommonsPlus = new ArrayList<>();
        CommonsPlus CommonsPlus1 = CommonsPlus.builder()
                .commons(Commons1)
                .totalCows(50)
                .totalUsers(20)
                .effectiveCapacity(100)
                .build();



        expectedCommonsPlus.add(CommonsPlus1);
        when(commonsRepository.findAll()).thenReturn(expectedCommons);
        when(commonsRepository.getNumCows(1L)).thenReturn(Optional.of(50));
        when(commonsRepository.getNumUsers(1L)).thenReturn(Optional.of(20));

        MvcResult response = mockMvc.perform(get("/api/commons/allplus").contentType("application/json"))
                .andExpect(status().isOk()).andReturn();

        verify(commonsRepository, times(1)).findAll();

        String responseString = response.getResponse().getContentAsString();
        List<CommonsPlus> actualCommonsPlus = objectMapper.readValue(responseString,
                new TypeReference<List<CommonsPlus>>() {
                });
        assertEquals(actualCommonsPlus, expectedCommonsPlus);
    }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void test_get_csv() throws Exception {
                when(commonStatsRepository.findAllByCommonsId(commons.getId())).thenReturn(List.of(expectedCommonStats));
               
                MvcResult response = mockMvc.perform(get("/api/commons/17L/download?commonsId=17")).andDo(print())
                                .andExpect(status().isOk()).andReturn();

                verify(commonStatsRepository, times(1)).findAllByCommonsId(eq(17L));
                String responseString = response.getResponse().getContentAsString();

                assertEquals("application/csv", response.getResponse().getContentType());

                String[] lines = responseString.split("\\r?\\n");

                assertEquals("id,commonsId,numCows,avgHealth,timestamp", lines[0]);

                String[] fields = lines[1].split(",");

                assertEquals("17", fields[1]);
                assertEquals("123", fields[2]);
                assertEquals("10.0", fields[3]);

        }

}
