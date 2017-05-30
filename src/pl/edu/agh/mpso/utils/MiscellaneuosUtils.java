package pl.edu.agh.mpso.utils;

import com.google.gson.Gson;
import pl.edu.agh.mpso.dao.SimulationResultDAO;
import pl.edu.agh.mpso.output.SimulationOutput;
import pl.edu.agh.mpso.output.SimulationOutputError;
import pl.edu.agh.mpso.output.SimulationOutputOk;
import pl.edu.agh.mpso.output.SimulationResult;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;

/**
 * Created by Zuzanna on 4/5/2017.
 */
public class MiscellaneuosUtils {

    public static void generateOutputFile(SimulationResult result) throws IOException {
        SimulationOutput output = null;
        try {
            output = new SimulationOutputOk(result);
            SimulationResultDAO.getInstance().writeResult(result);
            SimulationResultDAO.getInstance().close();
        } catch (Throwable e) {
            output = new SimulationOutputError(e.toString() + ": " + e.getMessage());
        } finally {
            Writer writer = new FileWriter("output.json");
            Gson gson = new Gson();
            gson.toJson(output, writer);
            writer.close();
        }
    }

    public static boolean meetsCriteria(SimulationResult result, int speciesId, int speciesCnt){
        try {
            Field speciesField = SimulationResult.class.getDeclaredField("species" + speciesId);
            int speciesFieldValue = (Integer) speciesField.get(result);
            return
//                    result.getTotalParticles() == NUMBER_OF_PARTICLES &&
                    speciesFieldValue == speciesCnt;
        } catch (Exception e) {
            return false;
        }
    }
}
