package edu.ucsb.cs156.happiercows.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;
import edu.ucsb.cs156.happiercows.strategies.CowHealthUpdateStrategies;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "commons")
public class Commons {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private double cowPrice;
    private double milkPrice;
    private double startingBalance;
    private LocalDateTime startingDate;
    private boolean showLeaderboard;

    private int carryingCapacity;
    private int capacityPerUser;
    private double degradationRate;

    // these defaults match old behavior
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CowHealthUpdateStrategies belowCapacityHealthUpdateStrategy = CowHealthUpdateStrategies.DEFAULT_BELOW_CAPACITY;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CowHealthUpdateStrategies aboveCapacityHealthUpdateStrategy = CowHealthUpdateStrategies.DEFAULT_ABOVE_CAPACITY;


    @OneToMany(mappedBy = "commons", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<UserCommons> joinedUsers;


    public static int computeEffectiveCapacity(Commons commons, CommonsRepository commonsRepository) {
        int numUsers = (commonsRepository.getNumUsers(commons.getId())).orElse(0);
        return Math.max(commons.getCapacityPerUser() * numUsers, commons.getCarryingCapacity());
    }

}
