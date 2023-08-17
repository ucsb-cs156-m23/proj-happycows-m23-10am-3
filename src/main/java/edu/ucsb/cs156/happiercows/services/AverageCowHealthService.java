package edu.ucsb.cs156.happiercows.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ucsb.cs156.happiercows.entities.UserCommons;

import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;

public class AverageCowHealthService {

    @Autowired
    UserCommonsRepository userCommonsRepository;

    public Double getAverageCowHealth(Long commonsId) {
        Iterable<UserCommons> userCommonsList = userCommonsRepository.findByCommonsId(commonsId);
        Double totalHealth = 0.0;
        int count = 0;

        for(UserCommons userCommons: userCommonsList){
            totalHealth += userCommons.getCowHealth();
            count+=1;
        }

        if(count == 0){
            throw new IllegalArgumentException("Unable to get average cow health");
        }

        return totalHealth / count;


    }

    
}
