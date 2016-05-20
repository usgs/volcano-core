package gov.usgs.volcanoes.core.contrib.hypo71;

import java.io.Serializable;

/**
 * Data structure used in Hypo71 algorithm.
 * 
 * @author Oleg Shepelev
 */
public class Stats  implements Serializable{
	private double numInA;
	private double numInB;
	private double numInC;
	private double numInD;
	private double numTotal;
	private double persentageInA;
	private double persentageInB;
	private double persentageInC;
	private double persentageInD;

	public Stats(double numInA, double numInB, double numInC, double numInD, double numTotal, double persentageInA, double persentageInB, double persentageInC, double persentageInD) {
		super();
		this.numInA = numInA;
		this.numInB = numInB;
		this.numInC = numInC;
		this.numInD = numInD;
		this.numTotal = numTotal;
		this.persentageInA = persentageInA;
		this.persentageInB = persentageInB;
		this.persentageInC = persentageInC;
		this.persentageInD = persentageInD;
	}

	public double getNumInA() {
		return numInA;
	}

	public void setNumInA(double numInA) {
		this.numInA = numInA;
	}

	public double getNumInB() {
		return numInB;
	}

	public void setNumInB(double numInB) {
		this.numInB = numInB;
	}

	public double getNumInC() {
		return numInC;
	}

	public void setNumInC(double numInC) {
		this.numInC = numInC;
	}

	public double getNumInD() {
		return numInD;
	}

	public void setNumInD(double numInD) {
		this.numInD = numInD;
	}

	public double getNumTotal() {
		return numTotal;
	}

	public void setNumTotal(double numTotal) {
		this.numTotal = numTotal;
	}

	public double getPersentageInA() {
		return persentageInA;
	}

	public void setPersentageInA(double persentageInA) {
		this.persentageInA = persentageInA;
	}

	public double getPersentageInB() {
		return persentageInB;
	}

	public void setPersentageInB(double persentageInB) {
		this.persentageInB = persentageInB;
	}

	public double getPersentageInC() {
		return persentageInC;
	}

	public void setPersentageInC(double persentageInC) {
		this.persentageInC = persentageInC;
	}

	public double getPersentageInD() {
		return persentageInD;
	}

	public void setPersentageInD(double persentageInD) {
		this.persentageInD = persentageInD;
	}

}
