package pl.edu.agh.mpso.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bson.Document;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import pl.edu.agh.mpso.output.SimulationResult;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import static pl.edu.agh.mpso.Simulation.NUMBER_OF_DIMENSIONS;
import static pl.edu.agh.mpso.Simulation.NUMBER_OF_ITERATIONS;
import static pl.edu.agh.mpso.Simulation.NUMBER_OF_PARTICLES;

public class SimulationResultDAO {
//	private static final String COLLECTION_NAME = "speciesShareFinal";
//	private static final String COLLECTION_NAME = "speciesShareSN7";
	private static final String COLLECTION_NAME = "combinationsN7";
//	private static final String COLLECTION_NAME = "transitionsFinal";
	
	private static final String DB_PROPERTIES_FILE = "db.properties";

    private static final String DB_URI = "db_uri";

    private static final String DB_NAME = "db_name";

    private static SimulationResultDAO simulationResultDAO;

    private final MongoClient mongoClient;

    private final MongoDatabase mongoDatabase;

    private SimulationResultDAO(MongoClient mongoClient, MongoDatabase mongoDatabase) {
        this.mongoClient = mongoClient;
        this.mongoDatabase = mongoDatabase;
    }

    public void writeResult(SimulationResult result) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.valueToTree(result);
        ((ObjectNode)jsonNode).put("timestamp", new Timestamp(System.currentTimeMillis()).toString());
        mongoDatabase.getCollection(COLLECTION_NAME)
                                       .insertOne(Document.parse(jsonNode.toString()));
    }
    
    public List<SimulationResult> getResults(String fitnessFunction, int dimensions, int iterations, int totalParticles){
    	return getResults(fitnessFunction, dimensions, iterations, totalParticles, -1);
    }
    
    @SuppressWarnings("unchecked")
	public List<SimulationResult> getResults(String fitnessFunction, int dimensions, int iterations, int totalParticles, int limit){
    	BasicDBObject query = new BasicDBObject("fitnessFunction", fitnessFunction)
    		.append("dimensions", dimensions).append("iterations", iterations).append("totalParticles", totalParticles);
    	
    	FindIterable<Document> find = mongoDatabase.getCollection(COLLECTION_NAME).find(query);
    	if(limit > 0) find = find.limit(limit);
    	List<SimulationResult> results = new ArrayList<SimulationResult>();
    	
    	MongoCursor<Document> iterator = find.iterator();
    	
		while(iterator.hasNext() && limit != 0){
    		Document next = iterator.next();
            SimulationResult.SimulationResultBuilder builder = new SimulationResult.SimulationResultBuilder();
            SimulationResult result = builder.setFitnessFunction(fitnessFunction)
                    .setIterations(iterations)
                    .setDimensions(dimensions)
                    .setPartial((List<Double>) next.get("partial"))
                    .setBestFitness(next.getDouble("bestFitness"))
                    .setTotalParticles(totalParticles)
                    .setSpecies1(next.getInteger("species1"))
                    .setSpecies2(next.getInteger("species2"))
                    .setSpecies3(next.getInteger("species3"))
                    .setSpecies4(next.getInteger("species4"))
                    .setSpecies5(next.getInteger("species5"))
                    .setSpecies6(next.getInteger("species6"))
                    .setSpecies7(next.getInteger("species7"))
                    .setSpecies8(next.getInteger("species8"))
                    .setOrderFunction(next.getString("orderFunction"))
                    .setShiftFunction(next.getString("shiftFunction"))
                    .build();


    		//TODO - best velocity
    		
    		results.add(result);
    		limit--;
    	}
    	
    	return results;
    }

    public void close() throws IOException {
        if (mongoClient != null) {
            mongoClient.close();
            simulationResultDAO = null;
        }
    }

    public static SimulationResultDAO getInstance() throws IOException {
        if (simulationResultDAO == null) {
            simulationResultDAO = createSimulationResultDAO();
        }
        return simulationResultDAO;
    }

    private static SimulationResultDAO createSimulationResultDAO() throws IOException {
        Properties props = new Properties();
        InputStream input = SimulationResultDAO.class.getResourceAsStream("/" + DB_PROPERTIES_FILE);

        props.load(input);

        String dbUri = props.getProperty(DB_URI);
        String dbName = props.getProperty(DB_NAME);

        MongoClient mongoClient = new MongoClient(new MongoClientURI(dbUri));
        MongoDatabase mongoDatabase = mongoClient.getDatabase(dbName);
        return new SimulationResultDAO(mongoClient, mongoDatabase);
    }
}
