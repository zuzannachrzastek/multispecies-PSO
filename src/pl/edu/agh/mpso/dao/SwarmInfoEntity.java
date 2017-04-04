package pl.edu.agh.mpso.dao;

import java.io.Serializable;

/**
 * Created by Roksana on 2017-03-31.
 */
public class SwarmInfoEntity implements Serializable {
    private int numberOfParticles;
    private int type;

    public SwarmInfoEntity() {
    }

    public SwarmInfoEntity(int numberOfParticles, int type) {
        this.numberOfParticles = numberOfParticles;
        this.type = type;
    }

    public int getNumberOfParticles() {
        return numberOfParticles;
    }

    public void setNumberOfParticles(int numberOfParticles) {
        this.numberOfParticles = numberOfParticles;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
