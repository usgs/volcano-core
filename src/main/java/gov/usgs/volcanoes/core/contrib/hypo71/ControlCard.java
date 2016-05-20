package gov.usgs.volcanoes.core.contrib.hypo71;

import java.util.Properties;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Data structure used in Hypo71 algorithm.
 * 
 * @author Oleg Shepelev
 */
@XmlRootElement
public class ControlCard {
	private int KSING;
	/**
	 * Trial focal depth in km.
	 */
	private double ZTR;
	/**
	 * Distance in km from epicenter where the distance weighting is 1.
	 */
	private double XNEAR;
	/**
	 * Distance in km from epicenter beyond which the distance weighting is 0.
	 */
	private double XFAR;
	/**
	 * Ration of P-velocity to S-velocity.
	 */
	private double POS;
	/**
	 * Quality class of earthquake to be included in the summary residuals.
	 */
	private int IQ;
	/**
	 * Indicator to check missing data.
	 */
	private int KMS;
	/**
	 * Minimum number of first-motion readings required before it is plotted.
	 */
	private int KFM;
	/**
	 * Indicator for punched cards.
	 */
	private int IPUN;
	/**
	 * Method of selecting eqathquake magnitude (MAG).
	 */
	private int IMAG;
	/**
	 * Number of new system response curves to be read in.
	 */
	private int IR;
	/**
	 * Indicator for printed output.
	 */
	private int IPRN;

	private int KPAPER;
	/**
	 * If ktest=1 then auxiliary RMS values are calculated at ten points on a
	 * sphere centered at hypocenter.
	 */
	private int KTEST;
	/**
	 * If KAZ=1, then azimuthal weighting of stations is applied.
	 */
	private int KAZ;
	/**
	 * If ksort = 1 then the stations are sorted by distance in the output.
	 */
	private int KSORT;
	/**
	 * If ksel=1 then the printed output for each earthquake will start at a new
	 * page.
	 */
	private int KSEL;
	/**
	 * Degree portion of the trial hypocenter latitude.
	 */
	private int LAT1;
	/**
	 * Minute portion of the trial hypocenter latitude.
	 */
	private int LAT2;
	/**
	 * Degree portion of the trial hypocenter longitude.
	 */
	private double LON1;
	/**
	 * Minute portion of the trial hypocenter latitude.
	 */
	private double LON2;

	public ControlCard(){
		
	}
	public ControlCard(int kSING, double zTR, double xNEAR, double xFAR,
			double pOS, int iQ, int kMS, int kFM, int iPUN, int iMAG, int iR,
			int iPRN, int kPAPER, int kTEST, int kAZ, int kSORT, int kSEL,
			int lAT1, int lAT2, double lON1, double lON2) {
		KSING = kSING;
		ZTR = zTR;
		XNEAR = xNEAR;
		XFAR = xFAR;
		POS = pOS;
		IQ = iQ;
		KMS = kMS;
		KFM = kFM;
		IPUN = iPUN;
		IMAG = iMAG;
		IR = iR;
		IPRN = iPRN;
		KPAPER = kPAPER;
		KTEST = kTEST;
		KAZ = kAZ;
		KSORT = kSORT;
		KSEL = kSEL;
		LAT1 = lAT1;
		LAT2 = lAT2;
		LON1 = lON1;
		LON2 = lON2;
	}

	public ControlCard(Properties properties) {
		this(Integer.parseInt((String) properties.get("control.KSING")), Double
				.parseDouble((String) properties.get("control.ZTR")), Double
				.parseDouble((String) properties.get("control.XNEAR")), Double
				.parseDouble((String) properties.get("control.XFAR")), Double
				.parseDouble((String) properties.get("control.POS")), Integer
				.parseInt((String) properties.get("control.IQ")), Integer
				.parseInt((String) properties.get("control.KMS")), Integer
				.parseInt((String) properties.get("control.KFM")), Integer
				.parseInt((String) properties.get("control.IPUN")), Integer
				.parseInt((String) properties.get("control.IMAG")), Integer
				.parseInt((String) properties.get("control.IR")), Integer
				.parseInt((String) properties.get("control.IPRN")), Integer
				.parseInt((String) properties.get("control.KPAPER")), Integer
				.parseInt((String) properties.get("control.KTEST")), Integer
				.parseInt((String) properties.get("control.KAZ")), Integer
				.parseInt((String) properties.get("control.KSORT")), Integer
				.parseInt((String) properties.get("control.KSEL")), Integer
				.parseInt((String) properties.get("control.LAT1")), Integer
				.parseInt((String) properties.get("control.LAT2")), Double
				.parseDouble((String) properties.get("control.LON1")), Double
				.parseDouble((String) properties.get("control.LON2")));
	}
	@XmlElement
	public int getKSING() {
		return KSING;
	}
	public void setKSING(int kSING) {
		KSING = kSING;
	}
	@XmlElement
	public double getZTR() {
		return ZTR;
	}
	public void setZTR(double zTR) {
		ZTR = zTR;
	}
	@XmlElement
	public double getXNEAR() {
		return XNEAR;
	}
	public void setXNEAR(double xNEAR) {
		XNEAR = xNEAR;
	}
	@XmlElement
	public double getXFAR() {
		return XFAR;
	}
	public void setXFAR(double xFAR) {
		XFAR = xFAR;
	}
	@XmlElement
	public double getPOS() {
		return POS;
	}
	public void setPOS(double pOS) {
		POS = pOS;
	}
	@XmlElement
	public int getIQ() {
		return IQ;
	}
	public void setIQ(int iQ) {
		IQ = iQ;
	}
	@XmlElement
	public int getKMS() {
		return KMS;
	}
	public void setKMS(int kMS) {
		KMS = kMS;
	}
	@XmlElement
	public int getKFM() {
		return KFM;
	}
	public void setKFM(int kFM) {
		KFM = kFM;
	}
	@XmlElement
	public int getIPUN() {
		return IPUN;
	}
	public void setIPUN(int iPUN) {
		IPUN = iPUN;
	}
	@XmlElement
	public int getIMAG() {
		return IMAG;
	}
	public void setIMAG(int iMAG) {
		IMAG = iMAG;
	}
	@XmlElement
	public int getIR() {
		return IR;
	}
	public void setIR(int iR) {
		IR = iR;
	}
	@XmlElement
	public int getIPRN() {
		return IPRN;
	}
	public void setIPRN(int iPRN) {
		IPRN = iPRN;
	}
	@XmlElement
	public int getKPAPER() {
		return KPAPER;
	}
	
	public void setKPAPER(int kPAPER) {
		KPAPER = kPAPER;
	}
	@XmlElement
	public int getKTEST() {
		return KTEST;
	}
	public void setKTEST(int kTEST) {
		KTEST = kTEST;
	}
	@XmlElement
	public int getKAZ() {
		return KAZ;
	}
	
	public void setKAZ(int kAZ) {
		KAZ = kAZ;
	}
	@XmlElement
	public int getKSORT() {
		return KSORT;
	}
	public void setKSORT(int kSORT) {
		KSORT = kSORT;
	}
	@XmlElement
	public int getKSEL() {
		return KSEL;
	}
	public void setKSEL(int kSEL) {
		KSEL = kSEL;
	}
	@XmlElement
	public int getLAT1() {
		return LAT1;
	}
	public void setLAT1(int lAT1) {
		LAT1 = lAT1;
	}
	@XmlElement
	public int getLAT2() {
		return LAT2;
	}
	public void setLAT2(int lAT2) {
		LAT2 = lAT2;
	}
	@XmlElement
	public double getLON1() {
		return LON1;
	}
	public void setLON1(double lON1) {
		LON1 = lON1;
	}
	@XmlElement
	public double getLON2() {
		return LON2;
	}
	public void setLON2(double lON2) {
		LON2 = lON2;
	}


}