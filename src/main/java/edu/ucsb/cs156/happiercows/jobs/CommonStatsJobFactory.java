package edu.ucsb.cs156.happiercows.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.CommonStatsRepository;
import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.UserRepository;
import edu.ucsb.cs156.happiercows.services.AverageCowHealthService;
import edu.ucsb.cs156.happiercows.services.jobs.JobContextConsumer;

@Service
public class CommonStatsJobFactory {

    @Autowired
    private AverageCowHealthService averageCowHealthService;

    @Autowired
    private CommonStatsRepository commonStatsRepository;

    @Autowired
    private UserCommonsRepository userCommonsRepository;

    @Autowired
    private CommonsRepository commonsRepository;

    public JobContextConsumer create() {
        return new CommonStatsJob(averageCowHealthService, commonStatsRepository, userCommonsRepository, commonsRepository);
    }
    
}
