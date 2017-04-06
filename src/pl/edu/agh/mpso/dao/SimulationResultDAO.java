package pl.edu.agh.mpso.dao;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import pl.edu.agh.mpso.output.SimulationResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


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

    private final DB mongoDatabase;

    private SimulationResultDAO(MongoClient mongoClient, DB mongoDatabase) {
        this.mongoClient = mongoClient;
        this.mongoDatabase = mongoDatabase;
    }

    public void writeResult(SimulationResult result) {
        DBObject object = createDBObject(result);
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode jsonNode = mapper.valueToTree(result);
//        ((ObjectNode)jsonNode).put("timestamp", new Timestamp(System.currentTimeMillis()).toString());
        mongoDatabase.getCollection(COLLECTION_NAME).insert(object);
    }

    public List<SimulationResult> getResults(String fitnessFunction, int dimensions, int iterations, int totalParticles) {
        return getResults(fitnessFunction, dimensions, iterations, totalParticles, -1);
    }

    @SuppressWarnings("unchecked")
    public List<SimulationResult> getResults(String fitnessFunction, int dimensions, int iterations, int totalParticles, int limit) {
        BasicDBObject query = new BasicDBObject("fitnessFunction", fitnessFunction)
                .append("dimensions", dimensions).append("iterations", iterations).append("totalParticles", totalParticles);

        System.out.println("query: " + query.toString());

        DBCursor cursor = mongoDatabase.getCollection(COLLECTION_NAME).find(query);
        List<SimulationResult> results = new ArrayList<SimulationResult>();

        System.out.println("AAAAAAAAAAAA");
        while (cursor.hasNext()){
            DBObject next = cursor.next();
            BasicDBList documents = (BasicDBList) next.get("swarmInformations");
            List<SwarmInfoEntity> swarmInfos = new ArrayList<>();
            System.out.println(documents+"size"+documents.size());
            for (int i = 0; i < documents.size(); i++) {
                if (documents.get(i) != null){
                    BasicDBObject s = (BasicDBObject) documents.get(i);
                    swarmInfos.add(new SwarmInfoEntity((int)s.get("numberOfParticles"), (int)s.get("type")));
                }
            }
            SimulationResult.SimulationResultBuilder builder = new SimulationResult.SimulationResultBuilder();
            SimulationResult result = builder.setFitnessFunction(fitnessFunction)
                    .setIterations(iterations)
                    .setDimensions(dimensions)
                    .setPartial((List<Double>) next.get("partial"))
                    .setBestFitness((Double) next.get("bestFitness"))
                    .setTotalParticles(totalParticles)
                    .setSwarmInformations(swarmInfos)
                    .setOrderFunction((String) next.get("orderFunction"))
                    .setShiftFunction((String) next.get("shiftFunction"))
                    .build();
            results.add(result);
        }

//    	FindIterable<Document> find = mongoDatabase.getCollection(COLLECTION_NAME).find(query);
//    	if(limit > 0) find = find.limit(limit);
//    	List<SimulationResult> results = new ArrayList<SimulationResult>();
//
//    	MongoCursor<Document> iterator = find.iterator();
//
//		while(iterator.hasNext() && limit != 0){
//    		Document next = iterator.next();
//            System.out.println(next.toString());
//            TODO check if works
//            SimulationResult.SimulationResultBuilder builder = new SimulationResult.SimulationResultBuilder();
//            SimulationResult result = builder.setFitnessFunction(fitnessFunction)
//                    .setIterations(iterations)
//                    .setDimensions(dimensions)
//                    .setPartial((List<Double>) next.get("partial"))
//                    .setBestFitness(next.getDouble("bestFitness"))
//                    .setTotalParticles(totalParticles)
//                    .setSwarmInformations((List<SwarmInfoEntity>) next.get("swarmInformations"))
//                    .setOrderFunction(next.getString("orderFunction"))
//                    .setShiftFunction(next.getString("shiftFunction"))
//                    .build();

        //TODO - best velocity

//    		results.add(result);
//    		limit--;
//    	}

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

        //TODO load properties from file
        MongoClient mongoClient = new MongoClient(new MongoClientURI(dbUri));
        DB db = mongoClient.getDB(dbName);
//        MongoDatabase mongoDatabase = mongoClient.getDatabase(dbName);
        return new SimulationResultDAO(mongoClient, db);
    }

    private static DBObject createDBObject(SimulationResult result) {
        BasicDBObjectBuilder docBuilder = BasicDBObjectBuilder.start();

        docBuilder.append("fitnessFunction", result.getFitnessFunction());
        docBuilder.append("bestFitness", result.getBestFitness());
        docBuilder.append("dimensions", result.getDimensions());
        docBuilder.append("iterations", result.getIterations());
        docBuilder.append("partial", result.getPartial());
        docBuilder.append("totalParticles", result.getTotalParticles());

        Document[] documents = new Document[result.getSwarmInformations().size()];
        for (int i = 0; i < result.getSwarmInformations().size(); i++) {
            Document swarmInfos = new Document();
            swarmInfos.append("numberOfParticles", result.getSwarmInformations().get(i).getNumberOfParticles());
            swarmInfos.append("type", result.getSwarmInformations().get(i).getType());
            documents[i++] = swarmInfos;
        }
        System.out.println(documents);
        docBuilder.append("swarmInformations", documents);
        docBuilder.append("initialVelocity", result.getInitialVelocity());
        docBuilder.append("finalVelocity", result.getFinalVelocity());
        docBuilder.append("orderFunction", result.getOrderFunction());
        docBuilder.append("shiftFunction", result.getShiftFunction());
        return docBuilder.get();
    }
}
