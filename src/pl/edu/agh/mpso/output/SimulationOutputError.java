package pl.edu.agh.mpso.output;

public class SimulationOutputError implements SimulationOutput {
	private String status = "error";
	private String reason;

	public SimulationOutputError(String reason) {
		this.reason = reason;
	}

	@Override
	public String getStatus() {
		return status;
	}

	public String getReason() {
		return reason;
	}
}
