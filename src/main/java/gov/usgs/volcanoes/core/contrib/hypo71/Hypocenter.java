package gov.usgs.volcanoes.core.contrib.hypo71;

import java.io.Serializable;

/**
 * Data structure used in Hypo71 algorithm.
 * 
 * @author Oleg Shepelev
 */
public class Hypocenter implements Serializable {
  private int KDATE; // Year, month, and day of new calibration
  private char RMKO;
  private int KHR; // Hour of new calibration
  private int KMIN; // Minute of new calibration
  private double SEC;
  private int LAT1; // Degree portion of hypocenter latitude.
  private double LAT2; // Minute portion of hypocenter latitude.
  private int LON1; // Degree portion of hypocenter longitude.
  private double LON2; // Minute portion of hypocenter longitude.
  private double Z; // Focal depth of hypocenter in km.
  private char RMK2;
  private String MAGOUT; // Magnitude of the earthquake.
  private int NO; // Number of station readings used in locating the earthquake.
  private int IDMIN;
  private int IGAP;
  private int KNO;
  private double RMS; // Root mean square of time residuals in seconds.
  private String ERHOUT; // Standard error of the epicenter in km.
  private String SE3OUT;
  private char Q; // Solution quality of the hypocenter.
  private char QS; // QS rating
  private char QD; // QD rating
  private double ADJ; // Last adjustment of hypocenter in km. Normally 0 or less than 0.05.
  private int JNST; // Instruction code?
  private int NR; // Number of station readings available. This includes readings which are not used
                  // in determining hypocenter.
  private double AVR; // Average of time residuals in seconds. Normally 0.
  private double AAR; // Average of the absolute time residuals in sec.
  private int NM; // Number of station readings available for computing maximum amplitude magnitude
                  // (XMAG).
  private double AVXM; // Average of XMAG of available stations.
  private double SDXM; // Standard deviation of XMAG of available stations.
  private int NF; // Number of station readings available for computing F-P magnitude (FMAG).
  private double AVFM; // Average of FMAG of available stations.
  private double SDFM; // Standard deviation of FMAG of available stations.
  private int NI; // Number of iterations to reach the final hypocenter.
  private double DMIN; // Epicentral distance in km to the nearest station

  public int getLON1() {
    return LON1;
  }

  public void setLON1(int lON1) {
    LON1 = lON1;
  }

  public double getDMIN() {
    return DMIN;
  }

  public void setDMIN(double dMIN) {
    DMIN = dMIN;
  }

  /*
   * Contains the values of three chars from the algorithm (QRMK[0], Q, SYM3).
   */
  private String QQS;

  public Hypocenter(int kDATE, char rMKO, int kHR, int kMIN, double sEC, int lAT1, double lAT2,
      int lON1, double lON2, double z, char rMK2, String mAGOUT, int nO, double dmin, int iGAP,
      int kNO, double rMS, String eRHOUT, String sE3OUT, char q, char qS, char qD, double aDJ,
      int jNST, int nR, double aVR, double aAR, int nM, double aVXM, double sDXM, int nF,
      double aVFM, double sDFM, int nI) {
    KDATE = kDATE;
    RMKO = rMKO;
    KHR = kHR;
    KMIN = kMIN;
    SEC = sEC;
    LAT1 = lAT1;
    LAT2 = lAT2;
    LON1 = lON1;
    LON2 = lON2;
    Z = z;
    RMK2 = rMK2;
    MAGOUT = mAGOUT;
    NO = nO;
    DMIN = dmin;
    IGAP = iGAP;
    KNO = kNO;
    RMS = rMS;
    ERHOUT = eRHOUT;
    SE3OUT = sE3OUT;
    Q = q;
    QS = qS;
    QD = qD;
    ADJ = aDJ;
    JNST = jNST;
    NR = nR;
    AVR = aVR;
    AAR = aAR;
    NM = nM;
    AVXM = aVXM;
    SDXM = sDXM;
    NF = nF;
    AVFM = aVFM;
    SDFM = sDFM;
    NI = nI;
  }

  public Hypocenter(int kDATE, int kHR, int kMIN, double sEC, int lAT1, double lAT2, int lON1,
      double lON2, double z, char rMK2, String mAGOUT, int nO, int iGAP, double dMIN, double rMS,
      String eRHOUT, String sE3OUT, String qQS) {
    KDATE = kDATE;
    KHR = kHR;
    KMIN = kMIN;
    SEC = sEC;
    LAT1 = lAT1;
    LAT2 = lAT2;
    LON1 = lON1;
    LON2 = lON2;
    Z = z;
    RMK2 = rMK2;
    MAGOUT = mAGOUT;
    NO = nO;
    DMIN = dMIN;
    IGAP = iGAP;
    RMS = rMS;
    ERHOUT = eRHOUT;
    SE3OUT = sE3OUT;
    setQQS(qQS);
  }

  public int getKDATE() {
    return KDATE;
  }

  public void setKDATE(int kDATE) {
    KDATE = kDATE;
  }

  public char getRMKO() {
    return RMKO;
  }

  public void setRMKO(char rMKO) {
    RMKO = rMKO;
  }

  public int getKHR() {
    return KHR;
  }

  public void setKHR(int kHR) {
    KHR = kHR;
  }

  public int getKMIN() {
    return KMIN;
  }

  public void setKMIN(int kMIN) {
    KMIN = kMIN;
  }

  public double getSEC() {
    return SEC;
  }

  public void setSEC(double sEC) {
    SEC = sEC;
  }

  public int getLAT1() {
    return LAT1;
  }

  public void setLAT1(int lAT1) {
    LAT1 = lAT1;
  }

  public double getLAT2() {
    return LAT2;
  }

  public void setLAT2(double lAT2) {
    LAT2 = lAT2;
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

  public String getMAGOUT() {
    return MAGOUT;
  }

  public void setMAGOUT(String mAGOUT) {
    MAGOUT = mAGOUT;
  }

  public int getNO() {
    return NO;
  }

  public void setNO(int nO) {
    NO = nO;
  }

  public int getIDMIN() {
    return IDMIN;
  }

  public void setIDMIN(int iDMIN) {
    IDMIN = iDMIN;
  }

  public int getIGAP() {
    return IGAP;
  }

  public void setIGAP(int iGAP) {
    IGAP = iGAP;
  }

  public int getKNO() {
    return KNO;
  }

  public void setKNO(int kNO) {
    KNO = kNO;
  }

  public double getRMS() {
    return RMS;
  }

  public void setRMS(double rMS) {
    RMS = rMS;
  }

  public String getERHOUT() {
    return ERHOUT;
  }

  public void setERHOUT(String eRHOUT) {
    ERHOUT = eRHOUT;
  }

  public String getSE3OUT() {
    return SE3OUT;
  }

  public void setSE3OUT(String sE3OUT) {
    SE3OUT = sE3OUT;
  }

  public char getQ() {
    return Q;
  }

  public void setQ(char q) {
    Q = q;
  }

  public char getQS() {
    return QS;
  }

  public void setQS(char qS) {
    QS = qS;
  }

  public char getQD() {
    return QD;
  }

  public void setQD(char qD) {
    QD = qD;
  }

  public double getADJ() {
    return ADJ;
  }

  public void setADJ(double aDJ) {
    ADJ = aDJ;
  }

  public int getJNST() {
    return JNST;
  }

  public void setJNST(int jNST) {
    JNST = jNST;
  }

  public int getNR() {
    return NR;
  }

  public void setNR(int nR) {
    NR = nR;
  }

  public double getAVR() {
    return AVR;
  }

  public void setAVR(double aVR) {
    AVR = aVR;
  }

  public double getAAR() {
    return AAR;
  }

  public void setAAR(double aAR) {
    AAR = aAR;
  }

  public int getNM() {
    return NM;
  }

  public void setNM(int nM) {
    NM = nM;
  }

  public double getAVXM() {
    return AVXM;
  }

  public void setAVXM(double aVXM) {
    AVXM = aVXM;
  }

  public double getSDXM() {
    return SDXM;
  }

  public void setSDXM(double sDXM) {
    SDXM = sDXM;
  }

  public int getNF() {
    return NF;
  }

  public void setNF(int nF) {
    NF = nF;
  }

  public double getAVFM() {
    return AVFM;
  }

  public void setAVFM(double aVFM) {
    AVFM = aVFM;
  }

  public double getSDFM() {
    return SDFM;
  }

  public void setSDFM(double sDFM) {
    SDFM = sDFM;
  }

  public int getNI() {
    return NI;
  }

  public void setNI(int nI) {
    NI = nI;
  }

  public String getQQS() {
    return QQS;
  }

  public void setQQS(String qQS) {
    QQS = qQS;
  }

  public double getLatitude() {
    return LAT1 + (LAT2 / 60d);
  }

  public double getLongitude() {
    return LON1 + (LON2 / 60d);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(String.format("%.2f", getLatitude()));
    sb.append(" " + String.format("%.2f", getLongitude()));
    sb.append(" " + String.format("%.2f", Z) + "km");
    return sb.toString();
  }
}
