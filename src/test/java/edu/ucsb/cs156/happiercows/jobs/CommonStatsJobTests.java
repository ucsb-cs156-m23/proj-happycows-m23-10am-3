package edu.ucsb.cs156.happiercows.jobs;

import edu.ucsb.cs156.happiercows.entities.Commons;
import edu.ucsb.cs156.happiercows.entities.User;
import edu.ucsb.cs156.happiercows.entities.UserCommons;
import edu.ucsb.cs156.happiercows.entities.CommonStats;
import edu.ucsb.cs156.happiercows.entities.jobs.Job;
import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.ProfitRepository;
import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.UserRepository;
import edu.ucsb.cs156.happiercows.repositories.CommonStatsRepository;
import edu.ucsb.cs156.happiercows.services.jobs.JobContext;
import edu.ucsb.cs156.happiercows.services.AverageCowHealthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import java.time.LocalDateTime;


@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class CommonStatsJobTests {
    @Mock
    CommonsRepository commonsRepository;

    @Mock
    UserCommonsRepository userCommonsRepository;

    @Mock
    CommonStatsRepository commonStatsRepository;

    @Mock
    AverageCowHealthService averageCowHealthService;


    private User user = User
            .builder()
            .id(1L)
            .fullName("Chris Gaucho")
            .email("cgaucho@example.org")
            .build();

    private Commons testCommons = Commons
            .builder()
            .name("test commons")
            .cowPrice(10)
            .milkPrice(2)
            .startingBalance(300)
            .startingDate(LocalDateTime.now())
            .carryingCapacity(100)
            .degradationRate(0.01)
            .build();
     
    private UserCommons uc_1 = UserCommons
        .builder()
        .user(user)
        .username("Chris Gaucho")
        .commons(testCommons)
        .totalWealth(300)
        .numOfCows(123)
        .cowHealth(10)
        .cowsBought(78)
        .cowsSold(23)
        .cowDeaths(6)
        .build();

    private CommonStats expectedCommonStats = CommonStats.builder()
                .commonsId(testCommons.getId())
                .numCows(123)
                .avgHealth(10.0)
                .build();



    @Test
    void test_common_stats() throws Exception{
        // Arrange

        Job jobStarted = Job.builder().build();
        JobContext ctx = new JobContext(null, jobStarted);

        when(commonsRepository.findAll()).thenReturn(Arrays.asList(testCommons));
        when(userCommonsRepository.findByCommonsId(testCommons.getId())).thenReturn(Arrays.asList(uc_1));
        when(averageCowHealthService.getAverageCowHealth(testCommons.getId())).thenReturn(10.0);

        



        // Act
        CommonStatsJob commonStatsJob = new CommonStatsJob(averageCowHealthService, commonStatsRepository, userCommonsRepository, commonsRepository);

        commonStatsJob.accept(ctx);

        // Assert
        ArgumentCaptor<CommonStats> captor = ArgumentCaptor.forClass(CommonStats.class);
        verify(commonStatsRepository).save(captor.capture());
        CommonStats savedCommonStats = captor.getValue();


        assertEquals(expectedCommonStats.getNumCows(), savedCommonStats.getNumCows());
        assertEquals(expectedCommonStats.getAvgHealth(), savedCommonStats.getAvgHealth()); 
        assertEquals(expectedCommonStats.getCommonsId(), savedCommonStats.getCommonsId());

        assertThat(savedCommonStats.getTimestamp()).isCloseTo(LocalDateTime.now(), within(5L, ChronoUnit.MINUTES));

        String expected = """
                Starts to record the common stats
                Starts to record the common stats for common: test commons
                Saved stats for common: test commons""";

        assertEquals(expected, jobStarted.getLog());
    }
}
