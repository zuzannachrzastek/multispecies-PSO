package pl.edu.agh.mpso.utils;

import net.sourceforge.jswarm_pso.FitnessFunction;
import net.sourceforge.jswarm_pso.Neighborhood;
import net.sourceforge.jswarm_pso.Neighborhood1D;
import pl.edu.agh.mpso.species.SpeciesType;
import pl.edu.agh.mpso.swarm.MultiSwarm;
import pl.edu.agh.mpso.swarm.SwarmInformation;

import java.util.ArrayList;
import java.util.List;

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

    public static void setMultiSwarmParameters(MultiSwarm multiSwarm, int size, double inertia, double searchSpaceSize) {
        Neighborhood neighbourhood = new Neighborhood1D(size, true);
        multiSwarm.setNeighborhood(neighbourhood);

        multiSwarm.setInertia(inertia);
        multiSwarm.setNeighborhoodIncrement(0.9);
        multiSwarm.setParticleIncrement(0.9);
        multiSwarm.setGlobalIncrement(0.9);

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
}
