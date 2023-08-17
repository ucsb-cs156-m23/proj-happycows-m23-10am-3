package edu.ucsb.cs156.happiercows.services;

import edu.ucsb.cs156.happiercows.entities.UserCommons;
import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import edu.ucsb.cs156.happiercows.entities.Commons;
import edu.ucsb.cs156.happiercows.entities.User;
import edu.ucsb.cs156.happiercows.entities.UserCommonsKey;
import edu.ucsb.cs156.happiercows.strategies.CowHealthUpdateStrategies;


@ExtendWith(SpringExtension.class)
@Import(AverageCowHealthService.class)
@ContextConfiguration
public class AverageCowHealthServiceTest {
    @Autowired
    AverageCowHealthService averageCowHealthService;

    @MockBean 
    UserCommonsRepository userCommonsRepository;

    private User user1 = User
      .builder()
      .id(42L)
      .fullName("Chris Gaucho")
      .email("cgaucho@example.org")
      .build();
    
    private User user2 = User
      .builder()
      .id(43L)
      .fullName("watermelon")
      .email("watermelon@example.org")
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

  UserCommons uc_1 = UserCommons
      .builder()
      .user(user1)
      .username("Chris Gaucho")
      .commons(commons)
      .totalWealth(300)
      .numOfCows(123)
      .cowHealth(10)
      .cowsBought(78)
      .cowsSold(23)
      .cowDeaths(6)
      .build();

    UserCommons uc_2 = UserCommons
      .builder()
      .user(user2)
      .username("watermelon")
      .commons(commons)
      .totalWealth(100)
      .numOfCows(70)
      .cowHealth(8)
      .cowsBought(30)
      .cowsSold(10)
      .cowDeaths(7)
      .build();
    
    @BeforeEach
    void setup() {
        uc_1.setId(new UserCommonsKey(user1.getId(), commons.getId()));
        uc_2.setId(new UserCommonsKey(user2.getId(), commons.getId()));
    }

    @Test
    void testGetAverageCowHealth(){
        when(userCommonsRepository.findByCommonsId(17L)).thenReturn(Arrays.asList(uc_1, uc_2));

        Double avgHealth = averageCowHealthService.getAverageCowHealth(17L);
        
        Assertions.assertEquals(9.0, avgHealth);

    }

    @Test
    void testGetAverageCowHealth_throwException(){
        when(userCommonsRepository.findByCommonsId(17L)).thenReturn(Arrays.asList());

        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> {
            averageCowHealthService.getAverageCowHealth(17L);
        }, "RuntimeException was expected");

        String message = thrown.getMessage();
        String expectedMessage = "Unable to get average cow health";
        Assertions.assertTrue(message.contains(expectedMessage), String.format("Expected message to contain \"%s\" but was \"%s\"", expectedMessage, message));
    }
}
