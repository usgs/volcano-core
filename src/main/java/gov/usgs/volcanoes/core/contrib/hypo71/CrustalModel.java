package gov.usgs.volcanoes.core.contrib.hypo71;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Data structure used in Hypo71 algorithm.
 * 
 * @author Oleg Shepelev
 */
@XmlRootElement
public class CrustalModel {
	private double V;
	private double D;

	public CrustalModel() {
	}
	
	public CrustalModel(double v, double d) {
		V = v;
		D = d;
	}

	@XmlElement
	public double getV() {
		return V;
	}

	public void setV(double v) {
		V = v;
	}

	@XmlElement
	public double getD() {
		return D;
	}

	public void setD(double d) {
		D = d;
	}
}