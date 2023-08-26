package edu.ucsb.cs156.happiercows.strategies;

import edu.ucsb.cs156.happiercows.entities.Commons;
import edu.ucsb.cs156.happiercows.entities.UserCommons;
import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;

public interface CowHealthUpdateStrategy {

    public double calculateNewCowHealth(
            Commons commons,
            UserCommons user,
            int totalCows,
            CommonsRepository commonsRepository
    );

    public String getDisplayName();
    public String getDescription();
}
