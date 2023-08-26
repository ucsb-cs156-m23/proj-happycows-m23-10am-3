package edu.ucsb.cs156.happiercows.entities;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ucsb.cs156.happiercows.strategies.CowHealthUpdateStrategies;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.ucsb.cs156.happiercows.entities.UserCommons;
import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;
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

@ExtendWith(SpringExtension.class)
public class CommonsTest {

    @MockBean
    CommonsRepository commonsRepository;

    User user1 = User
            .builder()
            .id(42L)
            .fullName("Chris Gaucho")
            .email("cgaucho@example.org")
            .build();

    
        UserCommons uc_1 = UserCommons
            .builder()
            .user(user1)
            .username("Chris Gaucho")
            .totalWealth(300)
            .numOfCows(123)
            .cowHealth(10)
            .cowsBought(78)
            .cowsSold(23)
            .cowDeaths(6)
            .build();

        Commons commons_1 = Commons
            .builder()
            .id(17L)
            .name("test commons 1")
            .cowPrice(10)
            .milkPrice(2)
            .startingBalance(300)
            .startingDate(LocalDateTime.parse("2022-03-05T15:50:10"))
            .showLeaderboard(true)
            .carryingCapacity(100)
            .degradationRate(0.01)
            .belowCapacityHealthUpdateStrategy(CowHealthUpdateStrategies.Linear)
            .aboveCapacityHealthUpdateStrategy(CowHealthUpdateStrategies.Linear)
            .joinedUsers(Arrays.asList(uc_1))
            .build();

    @Test
    void testComputeEffectiveCapacity_nousers(){
        when(commonsRepository.getNumUsers(17L)).thenReturn(Optional.empty());

        int res = Commons.computeEffectiveCapacity(commons_1, commonsRepository);

        assertEquals(100, res);
         
      
    }

    @Test
    void testComputeEffectiveCapacity(){
         when(commonsRepository.getNumUsers(17L)).thenReturn(Optional.of(5));
         int res = Commons.computeEffectiveCapacity(commons_1, commonsRepository);
         assertEquals(100, res);
    }
    
}
