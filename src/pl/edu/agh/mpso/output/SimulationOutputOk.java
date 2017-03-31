package pl.edu.agh.mpso.output;

public class SimulationOutputOk implements SimulationOutput{
	private String status = "success";
	private SimulationResult results;

	public SimulationOutputOk(SimulationResult result) {
		this.results = result;
	}

	@Override
	public String getStatus() {
		return status;
	}

	public SimulationResult getSimulationResults() {
		return results;
	}
}
