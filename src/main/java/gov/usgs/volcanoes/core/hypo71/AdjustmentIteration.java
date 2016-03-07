package gov.usgs.volcanoes.core.hypo71;

import java.io.Serializable;

/**
 * Data structure used in Hypo71 algorithm.
 * 
 * @author Oleg Shepelev
 */
public class AdjustmentIteration  implements Serializable{
	private int NI;
	private double SEC;
	private int LALT1;
	private double LAT2;
	private int LON1;
	private double LON2;
	private double Z;
	private char RMK2;
	private int IDMIN;
	private double RMS;
	private double AVRPS;
	private char QS;
	private int KF;
	private char QD;
	private double FLIM;
	private double B1;
	private double B0;
	private double B2;
	private double AF1;
	private double AF0;
	private double AF2;
	private double SE1;
	private double SE0;
	private double SE2;
	private double Y1;
	private double Y0;
	private double Y2;

	public AdjustmentIteration(int nI, double sEC, int lALT1, double lAT2, int lON1,
			double lON2, double z, char rMK2, int iDMIN, double rMS,
			double aVRPS, char qS, int kF, char qD, double fLIM, double b1,
			double b0, double b2, double aF1, double aF0, double aF2,
			double sE1, double sE0, double sE2, double y1, double y0,
			double y2) {
		NI = nI;
		SEC = sEC;
		LALT1 = lALT1;
		LAT2 = lAT2;
		LON1 = lON1;
		LON2 = lON2;
		Z = z;
		RMK2 = rMK2;
		IDMIN = iDMIN;
		RMS = rMS;
		AVRPS = aVRPS;
		QS = qS;
		KF = kF;
		QD = qD;
		FLIM = fLIM;
		B1 = b1;
		B0 = b0;
		B2 = b2;
		AF1 = aF1;
		AF0 = aF0;
		AF2 = aF2;
		SE1 = sE1;
		SE0 = sE0;
		SE2 = sE2;
		Y1 = y1;
		Y0 = y0;
		Y2 = y2;
	}

	public int getNI() {
		return NI;
	}

	public void setNI(int nI) {
		NI = nI;
	}

	public double getSEC() {
		return SEC;
	}

	public void setSEC(double sEC) {
		SEC = sEC;
	}

	public int getLALT1() {
		return LALT1;
	}

	public void setLALT1(int lALT1) {
		LALT1 = lALT1;
	}

	public double getLAT2() {
		return LAT2;
	}

	public void setLAT2(double lAT2) {
		LAT2 = lAT2;
	}

	public int getLON1() {
		return LON1;
	}

	public void setLON1(int lON1) {
		LON1 = lON1;
	}

	public double getLON2() {
		return LON2;
	}

	public void setLON2(double lON2) {
		LON2 = lON2;
	}

	public double getZ() {
		return Z;
	}

	public void setZ(double z) {
		Z = z;
	}

	public char getRMK2() {
		return RMK2;
	}

	public void setRMK2(char rMK2) {
		RMK2 = rMK2;
	}

	public int getIDMIN() {
		return IDMIN;
	}

	public void setIDMIN(int iDMIN) {
		IDMIN = iDMIN;
	}

	public double getRMS() {
		return RMS;
	}

	public void setRMS(double rMS) {
		RMS = rMS;
	}

	public double getAVRPS() {
		return AVRPS;
	}

	public void setAVRPS(double aVRPS) {
		AVRPS = aVRPS;
	}

	public char getQS() {
		return QS;
	}

	public void setQS(char qS) {
		QS = qS;
	}

	public int getKF() {
		return KF;
	}

	public void setKF(int kF) {
		KF = kF;
	}

	public char getQD() {
		return QD;
	}

	public void setQD(char qD) {
		QD = qD;
	}

	public double getFLIM() {
		return FLIM;
	}

	public void setFLIM(double fLIM) {
		FLIM = fLIM;
	}

	public double getB1() {
		return B1;
	}

	public void setB1(double b1) {
		B1 = b1;
	}

	public double getB0() {
		return B0;
	}

	public void setB0(double b0) {
		B0 = b0;
	}

	public double getB2() {
		return B2;
	}

	public void setB2(double b2) {
		B2 = b2;
	}

	public double getAF1() {
		return AF1;
	}

	public void setAF1(double aF1) {
		AF1 = aF1;
	}

	public double getAF0() {
		return AF0;
	}

	public void setAF0(double aF0) {
		AF0 = aF0;
	}

	public double getAF2() {
		return AF2;
	}

	public void setAF2(double aF2) {
		AF2 = aF2;
	}

	public double getSE1() {
		return SE1;
	}

	public void setSE1(double sE1) {
		SE1 = sE1;
	}

	public double getSE0() {
		return SE0;
	}

	public void setSE0(double sE0) {
		SE0 = sE0;
	}

	public double getSE2() {
		return SE2;
	}

	public void setSE2(double sE2) {
		SE2 = sE2;
	}

	public double getY1() {
		return Y1;
	}

	public void setY1(double y1) {
		Y1 = y1;
	}

	public double getY0() {
		return Y0;
	}

	public void setY0(double y0) {
		Y0 = y0;
	}

	public double getY2() {
		return Y2;
	}

	public void setY2(double y2) {
		Y2 = y2;
	}
}
