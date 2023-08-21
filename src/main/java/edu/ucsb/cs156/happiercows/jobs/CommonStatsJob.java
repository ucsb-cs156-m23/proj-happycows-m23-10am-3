package edu.ucsb.cs156.happiercows.jobs;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.ucsb.cs156.happiercows.entities.Commons;
import edu.ucsb.cs156.happiercows.entities.UserCommons;
import edu.ucsb.cs156.happiercows.entities.CommonStats;
import edu.ucsb.cs156.happiercows.repositories.CommonStatsRepository;
import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;
import edu.ucsb.cs156.happiercows.services.AverageCowHealthService;
import edu.ucsb.cs156.happiercows.services.jobs.JobContext;
import edu.ucsb.cs156.happiercows.services.jobs.JobContextConsumer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Service
@AllArgsConstructor
public class CommonStatsJob implements JobContextConsumer {
    
    @Getter
    AverageCowHealthService averageCowHealthService;

    @Getter
    CommonStatsRepository commonStatsRepository;

    @Getter
    UserCommonsRepository userCommonsRepository;

    @Getter
    CommonsRepository commonsRepository;


    @Override
    public void accept (JobContext ctx) throws Exception{

        ctx.log("Starts to record the common stats");
        Iterable<Commons> allCommons = commonsRepository.findAll();

        for(Commons commons: allCommons){
            Long commonId = commons.getId();
            ctx.log("Starts to record the common stats for common: " + commons.getName());
            Double avgHealth = averageCowHealthService.getAverageCowHealth(commonId);
            Iterable<UserCommons> userCommonsList = userCommonsRepository.findByCommonsId(commonId);

            Integer numCows = 0;
            for(UserCommons userCommons: userCommonsList){
                numCows += userCommons.getNumOfCows();
            }

            CommonStats stats = CommonStats.builder()
            .commonsId(commonId)
            .numCows(numCows)
            .avgHealth(avgHealth)
            .timestamp(LocalDateTime.now())
            .build();

            commonStatsRepository.save(stats);
            ctx.log("Saved stats for common: " + commons.getName());
        }


    }

}
