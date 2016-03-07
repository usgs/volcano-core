package gov.usgs.volcanoes.core.hypo71;


import java.util.LinkedList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Data structure used to persist all the input for a Hypo71 run.
 * 
 * @author Chirag Patel
 */
@XmlRootElement
public class HypoArchiveOutput {

	private LinkedList<Station> stations = new LinkedList<Station>();
	private ControlCard controlCard;
	private LinkedList<PhaseRecord> phaseRecords = new LinkedList<PhaseRecord>();
	private LinkedList<CrustalModel> crustalModel = new LinkedList<CrustalModel>();
	
	@XmlElements(value = { @XmlElement })
	public LinkedList<Station> getStations() {
		return stations;
	}
	public void setStations(LinkedList<Station> stations) {
		this.stations = stations;
	}
	
	@XmlElement
	public ControlCard getControlCard() {
		return controlCard;
	}
	public void setControlCard(ControlCard controlCard) {
		this.controlCard = controlCard;
	}
	
	@XmlElements(value = { @XmlElement })
	public LinkedList<PhaseRecord> getPhaseRecords() {
		return phaseRecords;
	}
	public void setPhaseRecords(LinkedList<PhaseRecord> phasRecords) {
		this.phaseRecords = phasRecords;
	}
	
	@XmlElements(value = { @XmlElement })
	public LinkedList<CrustalModel> getCrustalModel() {
		return crustalModel;
	}
	public void setCrustalModel(LinkedList<CrustalModel> crustalModel) {
		this.crustalModel = crustalModel;
	}
}
