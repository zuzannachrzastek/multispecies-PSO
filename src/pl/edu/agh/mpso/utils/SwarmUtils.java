package pl.edu.agh.mpso.utils;

import net.sourceforge.jswarm_pso.FitnessFunction;
import net.sourceforge.jswarm_pso.Neighborhood;
import net.sourceforge.jswarm_pso.Neighborhood1D;
import pl.edu.agh.mpso.dao.SwarmInfoEntity;
import pl.edu.agh.mpso.species.SpeciesType;
import pl.edu.agh.mpso.swarm.MultiSwarm;
import pl.edu.agh.mpso.swarm.SwarmInformation;

import java.security.Policy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pl.edu.agh.mpso.utils.ExecutionParameters.*;

/**
 * Created by Zuzanna on 3/31/2017.
 */
public class SwarmUtils {

    public static MultiSwarm createSwarm(FitnessFunction fitnessFunction) {
        final int[] particleArray = new int[]{6, 0, 0, 0, 0, 0, 0, 1};

        List<SwarmInformation> swarmInformations = createSwarmInfoList(particleArray);
        MultiSwarm multiSwarm = new MultiSwarm(swarmInformations, fitnessFunction);
        setMultiSwarmParameters(multiSwarm, 1, 0.95, 5);
        multiSwarm.init();

        return multiSwarm;
    }

    public static void setMultiSwarmParameters(MultiSwarm multiSwarm, int neighbourhoodSize, double inertia, double searchSpaceSize) {
        Neighborhood neighbourhood = new Neighborhood1D(neighbourhoodSize, true);
        multiSwarm.setNeighborhood(neighbourhood);

        multiSwarm.setInertia(inertia);
        multiSwarm.setNeighborhoodIncrement(NEIGHBOURHOOD_INCREMENT);
        multiSwarm.setParticleIncrement(PARTICLE_INCREMENT);
        multiSwarm.setGlobalIncrement(GLOBAL_INCREMENT);

        multiSwarm.setMaxPosition(searchSpaceSize);
        multiSwarm.setMinPosition(-searchSpaceSize);
    }

    public static List<SwarmInformation> createSwarmInfoList(int[] particles) {
        List<SwarmInformation> swarmInformations = new ArrayList<SwarmInformation>();

        for (int i = 0; i < particles.length; i++) {
            if (particles[i] != 0) {
                SpeciesType type = SpeciesType.values()[i];
                SwarmInformation swarmInformation = new SwarmInformation(particles[i], type);
                swarmInformations.add(swarmInformation);
            }
        }

        return swarmInformations;
    }

    public static List<SwarmInformation> createSwarmInfoListWithCounter(int[] particles, int cnt) {
        List<SwarmInformation> swarmInformations = new ArrayList<SwarmInformation>();

        for(int i = 0; i < particles.length; i++){
            if(particles[i] != 0){
                cnt += particles[i];

                SpeciesType type = SpeciesType.values()[i];
                SwarmInformation swarmInformation = new SwarmInformation(particles[i], type);

                swarmInformations.add(swarmInformation);
            }
        }

        return swarmInformations;
    }

    public static List<SwarmInfoEntity> getSwarmEntityList(List<SwarmInformation> swarmInformations) {
        List<SwarmInfoEntity> result = new ArrayList<>();
        //the original list is grouped by type and aggregated by number of particles
        swarmInformations.stream()
                .collect(
                        Collectors.groupingBy(swarmInformation -> swarmInformation.getType().getType(),
                                Collectors.summingInt(SwarmInformation::getNumberOfParticles)))
                .forEach(((type, numberOfParticles) -> result.add(new SwarmInfoEntity(numberOfParticles, type))));
        return result;
    }
}
