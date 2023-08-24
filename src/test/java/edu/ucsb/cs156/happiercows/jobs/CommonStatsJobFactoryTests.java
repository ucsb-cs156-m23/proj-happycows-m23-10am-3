package edu.ucsb.cs156.happiercows.jobs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.ProfitRepository;
import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.UserRepository;
import edu.ucsb.cs156.happiercows.repositories.CommonStatsRepository;
import edu.ucsb.cs156.happiercows.services.AverageCowHealthService;

@RestClientTest(CommonStatsJobFactory.class)
@AutoConfigureDataJpa
public class CommonStatsJobFactoryTests {

    @MockBean
    CommonsRepository commonsRepository;

    @MockBean
    UserCommonsRepository userCommonsRepository;

    @MockBean
    CommonStatsRepository commonStatsRepository;

    @MockBean
    AverageCowHealthService averageCowHealthService;

    @Autowired
    CommonStatsJobFactory commonStatsJobFactory;

    @Test
    void test_create() throws Exception {

        // Act
        CommonStatsJob commonStatsJob = (CommonStatsJob) commonStatsJobFactory.create();

        // Assert
        assertEquals(commonsRepository,commonStatsJob.getCommonsRepository());
        assertEquals(userCommonsRepository,commonStatsJob.getUserCommonsRepository());
        assertEquals(commonStatsRepository,commonStatsJob.getCommonStatsRepository());
        assertEquals(averageCowHealthService,commonStatsJob.getAverageCowHealthService());

    }
}
