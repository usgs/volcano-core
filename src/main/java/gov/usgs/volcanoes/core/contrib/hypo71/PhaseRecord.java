package gov.usgs.volcanoes.core.contrib.hypo71;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Data structure used in Hypo71 algorithm.
 * 
 * @author Oleg Shepelev
 */
@XmlRootElement
public class PhaseRecord {
  /**
   * Station name.
   */
  private String MSTA;
  private String PRMK;
  /**
   * Year, month and day of P-arrival
   */
  private float W;
  /**
   * Time.
   */
  private int JTIME;
  /**
   * 
   */
  private int JMIN;
  private float P;
  private float S;
  private String SRMK;
  private float WS;
  /** 
   * Maximum peak-to-peak amplitude in mm.
   */
  private float AMX;
  /**
   * Period of the maximum amplitude in sec. 
   */
  private float PRX;
  /** 
   * Normally not used except as noted in next item.
   */
  private float CALP;
  /** 
   * Peak-to-peak amplitude of 10mv calibration signal in mm.
   */
  private float CALX;
  /** 
   * Remark for this phase card.
   */
  private String RMK;
  /**
   * Time correction in sec.
   */
  private float DT;
  /**
   * F-P time in sec. This is the duration time of earthquake.
   */
  private float FMP;
  private String AZRES;
  private char SYM;
  private String AS;
  private String ICARD;
  private char QRMK;
  /**
   * Normally blank.
   */
  private String IPRO;

  public PhaseRecord() {

  }

  public PhaseRecord(String mSTA, String pRMK, float w, int jTIME, int jMIN, float p, float s,
      String sRMK, float wS, float aMX, float pRX, float cALP, float cALX, String rMK, float dT,
      float fMP, String aZRES, char sYM, String aS, String iCARD, char qRMK, String iPRO) {
    MSTA = mSTA;
    PRMK = pRMK;
    W = w;
    JTIME = jTIME;
    JMIN = jMIN;
    P = p;
    S = s;
    SRMK = sRMK;
    WS = wS;
    AMX = aMX;
    PRX = pRX;
    CALP = cALP;
    CALX = cALX;
    RMK = rMK;
    DT = dT;
    FMP = fMP;
    AZRES = aZRES;
    SYM = sYM;
    AS = aS;
    ICARD = iCARD;
    QRMK = qRMK;
    IPRO = iPRO;
  }

  public PhaseRecord(String mSTA, String pRMK, double w, int jTIME, int jMIN, double p, double s,
      String sRMK, double wS, double aMX, double pRX, double cALP, double cALX, String rMK,
      double dT, double fMP, String aZRES, char sYM, String aS, String iCARD, char qRMK,
      String iPRO) {
    MSTA = mSTA;
    PRMK = pRMK;
    W = (float) w;
    JTIME = jTIME;
    JMIN = jMIN;
    P = (float) p;
    S = (float) s;
    SRMK = sRMK;
    WS = (float) wS;
    AMX = (float) aMX;
    PRX = (float) pRX;
    CALP = (float) cALP;
    CALX = (float) cALX;
    RMK = rMK;
    DT = (float) dT;
    FMP = (float) fMP;
    AZRES = aZRES;
    SYM = sYM;
    AS = aS;
    ICARD = iCARD;
    QRMK = qRMK;
    IPRO = iPRO;
  }

  @XmlElement
  public String getMSTA() {
    return MSTA;
  }

  public void setMSTA(String mSTA) {
    MSTA = mSTA;
  }

  @XmlElement
  public String getPRMK() {
    return PRMK;
  }

  public void setPRMK(String pRMK) {
    PRMK = pRMK;
  }

  @XmlElement
  public float getW() {
    return W;
  }

  public void setW(float w) {
    W = w;
  }

  @XmlElement
  public int getJTIME() {
    return JTIME;
  }

  public void setJTIME(int jTIME) {
    JTIME = jTIME;
  }

  @XmlElement
  public int getJMIN() {
    return JMIN;
  }

  public void setJMIN(int jMIN) {
    JMIN = jMIN;
  }

  @XmlElement
  public float getP() {
    return P;
  }

  public void setP(float p) {
    P = p;
  }

  @XmlElement
  public float getS() {
    return S;
  }

  public void setS(float s) {
    S = s;
  }

  @XmlElement
  public String getSRMK() {
    return SRMK;
  }

  public void setSRMK(String sRMK) {
    SRMK = sRMK;
  }

  @XmlElement
  public float getWS() {
    return WS;
  }

  public void setWS(float wS) {
    WS = wS;
  }

  @XmlElement
  public float getAMX() {
    return AMX;
  }

  public void setAMX(float aMX) {
    AMX = aMX;
  }

  @XmlElement
  public float getPRX() {
    return PRX;
  }

  public void setPRX(float pRX) {
    PRX = pRX;
  }

  @XmlElement
  public float getCALP() {
    return CALP;
  }

  public void setCALP(float cALP) {
    CALP = cALP;
  }

  @XmlElement
  public float getCALX() {
    return CALX;
  }

  public void setCALX(float cALX) {
    CALX = cALX;
  }

  @XmlElement
  public String getRMK() {
    return RMK;
  }

  public void setRMK(String rMK) {
    RMK = rMK;
  }

  @XmlElement
  public float getDT() {
    return DT;
  }

  public void setDT(float dT) {
    DT = dT;
  }

  @XmlElement
  public float getFMP() {
    return FMP;
  }

  public void setFMP(float fMP) {
    FMP = fMP;
  }

  @XmlElement
  public String getAZRES() {
    return AZRES;
  }

  public void setAZRES(String aZRES) {
    AZRES = aZRES;
  }

  @XmlElement
  public char getSYM() {
    return SYM;
  }

  public void setSYM(char sYM) {
    SYM = sYM;
  }

  @XmlElement
  public String getAS() {
    return AS;
  }

  public void setAS(String aS) {
    AS = aS;
  }

  @XmlElement
  public String getICARD() {
    return ICARD;
  }

  public void setICARD(String iCARD) {
    ICARD = iCARD;
  }

  @XmlElement
  public char getQRMK() {
    return QRMK;
  }

  public void setQRMK(char qRMK) {
    QRMK = qRMK;
  }

  @XmlElement
  public String getIPRO() {
    return IPRO;
  }

  public void setIPRO(String iPRO) {
    IPRO = iPRO;
  }


}
