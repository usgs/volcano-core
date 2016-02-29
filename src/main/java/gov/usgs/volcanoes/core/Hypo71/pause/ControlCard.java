package gov.usgs.volcanoes.core.Hypo71.pause;

/**
 * Represents a Hypo71 control card
 * 
 * @author Tom Parker
 */
public class ControlCard {

  private final int ksing;
  private final int kpaper;

  /** Trial focal depth in km */
  public final double ztr;

  /** Distance in km from epicenter where the distance weighting is 1. */
  public final double xnear;

  /** Distance in km from epicenter beyond which the distance weighting is 0. */
  public final double xfar;

  /** Ration of P-velocity to S-velocity. */
  public final double pos;

  /** Quality class of earthquake to be included in the summary residuals. */
  public final int iq;

  /** Indicator to check missing data. */
  public final int kms;

  /** Minimum number of first-motion readings required before it is plotted. */
  public final int kfm;

  /** Indicator for punched cards. */
  public final int ipun;

  /** Method of selecting eqathquake magnitude (MAG). */
  public final int imag;

  /** Number of new system response curves to be read in. */
  public final int ir;

  /** Indicator for printed output. */
  public final int iprn;

  /** If ktest=1 then auxiliary RMS values are calculated at ten points on a sphere centered at hypocenter. */
  public final int ktest;

  /** If KAZ=1, then azimuthal weighting of stations is applied. */
  public final int kaz;

  /** If ksort = 1 then the stations are sorted by distance in the output. */
  public final int ksort;

  /** If ksel=1 then the printed output for each earthquake will start at a new page. */
  public final int ksel;

  /** Degree portion of the trial hypocenter latitude. */
  public final int lat1;

  /** Minute portion of the trial hypocenter latitude. */
  public final int lat2;

  /** Degree portion of the trial hypocenter longitude. */
  public final double lon1;

  /** Minute portion of the trial hypocenter latitude. */
  public final double lon2;

  public ControlCard(Builder builder) {
    ksing = builder.ksing;
    ztr = builder.ztr;
    xnear = builder.xnear;
    xfar = builder.xfar;
    pos = builder.pos;
    iq = builder.iq;
    kms = builder.kms;
    kfm = builder.kfm;
    ipun = builder.ipun;
    imag = builder.imag;
    ir = builder.ir;
    iprn = builder.iprn;
    kpaper = builder.kpaper;
    ktest = builder.ktest;
    kaz = builder.kaz;
    ksort = builder.ksort;
    ksel = builder.ksel;
    lat1 = builder.lat1;
    lat2 = builder.lat2;
    lon1 = builder.lon1;
    lon2 = builder.lon2;
  }

  public static class Builder {
    private int ksing;
    private double ztr;
    private double xnear;
    private double xfar;
    private double pos;
    private int iq;
    private int kms;
    private int kfm;
    private int ipun;
    private int imag;
    private int ir;
    private int iprn;
    private int kpaper;
    private int ktest;
    private int kaz;
    private int ksort;
    private int ksel;
    private int lat1;
    private int lat2;
    private double lon1;
    private double lon2;

    public Builder ksing(int ksing) {
      this.ksing = ksing;
      return this;
    }

    public Builder ztr(double ztr) {
      this.ztr = ztr;
      return this;
 }

    public Builder xnear(double xnear) {
      this.xnear = xnear;
      return this;
 }

    public Builder xfar(double xfar) {
      this.xfar = xfar;
      return this;
   }

    public Builder pos(double pos) {
      this.pos = pos;
      return this;
    }

    public Builder iq(int iq) {
      this.iq = iq;
      return this;
   }

    public Builder kms(int kms) {
      this.kms = kms;
      return this;
    }

    public Builder kfm(int kfm) {
      this.kfm = kfm;
      return this;
  }

    public Builder ipun(int ipun) {
      this.ipun = ipun;
      return this;
   }

    public Builder imag(int imag) {
      this.imag = imag;
      return this;
   }

    public Builder ir(int ir) {
      this.ir = ir;
      return this;
 }

    public Builder iprn(int iprn) {
      this.iprn = iprn;
      return this;
    }

    public Builder kpaper(int kpaper) {
      this.kpaper = kpaper;
      return this;
    }

    public Builder ktest(int ktest) {
      this.ktest = ktest;
      return this;
   }

    public Builder setKAZ(int kaz) {
      this.kaz = kaz;
      return this;
    }

    public Builder ksort(int ksort) {
      this.ksort = ksort;
      return this;
    }

    public Builder setKSEL(int ksel) {
      this.ksel = ksel;
      return this;
    }

    public Builder lat1(int lat1) {
      this.lat1 = lat1;
      return this;
  }

    public Builder lat2(int lat2) {
      this.lat2 = lat2;
      return this;
 }

    public Builder lon1(double lon1) {
      this.lon1 = lon1;
      return this;
  }

    public Builder lon2(double lon2) {
      this.lon2 = lon2;
      return this;
   }

    public ControlCard build() {
      return new ControlCard(this);
    }

  }

}
