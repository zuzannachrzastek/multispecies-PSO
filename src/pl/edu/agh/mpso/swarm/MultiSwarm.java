package pl.edu.agh.mpso.swarm;

import java.util.List;

import net.sourceforge.jswarm_pso.FitnessFunction;
import net.sourceforge.jswarm_pso.Particle;
import net.sourceforge.jswarm_pso.ParticleUpdate;
import net.sourceforge.jswarm_pso.Swarm;
import net.sourceforge.jswarm_pso.VariablesUpdate;
import pl.edu.agh.mpso.species.SpeciesParticle;
import pl.edu.agh.mpso.transition.order.OrderFunction;
import pl.edu.agh.mpso.transition.shift.ShiftFunction;
import pl.edu.agh.mpso.velocity.VelocityFunction;

public class MultiSwarm extends Swarm {

    private List<SwarmInformation> swarmInfos;
    private VelocityFunction velocityFunction;
    private OrderFunction orderFunction;
    private ShiftFunction shiftFunction;
    private long evolveCnt = 0L;


    public MultiSwarm(List<SwarmInformation> swarmInfos, FitnessFunction fitnessFunction) {
        super(swarmInfos.stream().mapToInt(SwarmInformation::getNumberOfParticles).sum(), swarmInfos.get(0).getSampleParticle(), fitnessFunction);
        this.swarmInfos = swarmInfos;
    }

    public void setAbsMaxVelocity(double velocity) {
        int dim = sampleParticle.getDimension();

        if (maxVelocity == null || maxVelocity.length != dim) {
            maxVelocity = new double[dim];
        }
        if (minVelocity == null || minVelocity.length != dim) {
            minVelocity = new double[dim];
        }

        for (int i = 0; i < dim; i++) {
            maxVelocity[i] = velocity;
            minVelocity[i] = -velocity;
        }
    }

    public void setVelocityFunction(VelocityFunction function) {
        this.velocityFunction = function;
    }

    public VelocityFunction getVelocityFunction() {
        return velocityFunction;
    }

    public void setOrderFunction(OrderFunction function) {
        this.orderFunction = function;
    }

    public OrderFunction getOrderFunction() {
        return orderFunction;
    }

    public void setShiftFunction(ShiftFunction function) {
        this.shiftFunction = function;
    }

    public ShiftFunction getShiftFunction() {
        return shiftFunction;
    }

    public List<SwarmInformation> getSwarmInfos() {
        return swarmInfos;
    }

    @Override
    public void init() {
        // Init particles
        setParticles(new SpeciesParticle[this.numberOfParticles]);

        // Check constraints (they will be used to initialize particles)
        if (maxPosition == null) throw new RuntimeException("maxPosition array is null!");
        if (minPosition == null) throw new RuntimeException("maxPosition array is null!");

        if (maxVelocity == null) {
            // Default maxVelocity[]
            int dim = sampleParticle.getDimension();
            maxVelocity = new double[dim];
            for (int i = 0; i < dim; i++)
                maxVelocity[i] = (maxPosition[i] - minPosition[i]) / 2.0;
        }
        if (minVelocity == null) {
            // Default minVelocity[]
            int dim = sampleParticle.getDimension();
            minVelocity = new double[dim];
            for (int i = 0; i < dim; i++)
                minVelocity[i] = -maxVelocity[i];
        }

        // Init each particle
        int particleOffset = 0;
        for (SwarmInformation swarmInfo : swarmInfos) {
            for (int i = 0; i < swarmInfo.getNumberOfParticles(); ++i) {
                SpeciesParticle particle = new SpeciesParticle(swarmInfo.getType(), swarmInfo.getSampleParticle().getDimension());
                swarmInfo.getParticles().add(particle);
                particles[i + particleOffset] = particle;
                particles[i + particleOffset].init(maxPosition, minPosition, maxVelocity, minVelocity);
            }
            particleOffset += swarmInfo.getNumberOfParticles();
        }

        // Init neighborhood
        if (neighborhood != null) neighborhood.init(this);
    }

    @Override
    public void update() {
        // For each particle...
        for (SwarmInformation swarmInfo : swarmInfos) {
            for (Particle particle : swarmInfo.getParticles()) {
                ParticleUpdate particleUpdate = swarmInfo.getParticleUpdate();
                particleUpdate.begin(this);

                particleUpdate.update(this, particle);

                // Apply position and velocity constraints
                particle.applyConstraints(minPosition, maxPosition, minVelocity, maxVelocity);
                particleUpdate.end(this);
            }
        }
    }

    @Override
    public void evolve() {
        // Initialize (if not already done)
        super.evolve();
        evolveCnt++;

        if (velocityFunction != null && evolveCnt % velocityFunction.getUpdatesInterval() == 0) {
            setAbsMaxVelocity(velocityFunction.getNext());
        }

        if (orderFunction != null && evolveCnt % orderFunction.getUpdatesInterval() == 0) {
            orderFunction.calculate((SpeciesParticle[]) particles);
        }

        if (shiftFunction != null && orderFunction != null && evolveCnt % shiftFunction.getUpdatesInterval() == 0) {
            shiftFunction.shift(swarmInfos, orderFunction.getOrder());
        }
    }

    //TODO ugly
//    public int[] getSpecies() {
//        int[] result = new int[SpeciesType.values().length];
//        for (int i = 0; i < swarmInfos.length; i++) {
//            SpeciesType type = SpeciesType.values()[i];
//            int index = Arrays.asList(SpeciesType.values()).indexOf(type);
//            result[index] = swarmInfos[i].getNumberOfParticles();
//        }
//        return result;
//    }

}
