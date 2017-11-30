package gov.usgs.volcanoes.core.legacy.plot.render.wave;

import gov.usgs.volcanoes.core.data.SliceWave;
import gov.usgs.volcanoes.core.data.Spectrogram;
import gov.usgs.volcanoes.core.legacy.plot.color.Jet2;
import gov.usgs.volcanoes.core.legacy.plot.color.Spectrum;
import gov.usgs.volcanoes.core.legacy.plot.decorate.DefaultFrameDecorator;
import gov.usgs.volcanoes.core.legacy.plot.decorate.FrameDecorator;
import gov.usgs.volcanoes.core.legacy.plot.render.AxisRenderer;
import gov.usgs.volcanoes.core.legacy.plot.render.ImageDataRenderer;
import gov.usgs.volcanoes.core.time.J2kSec;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;

/**
 * Renderer to draw spectrograms. Keeps reference to processed wave data,
 * compute spectrograms and create image to render as ImageDataRenderer.
 * 
 * @author Dan Cervelli
 */
public class SpectrogramRenderer extends ImageDataRenderer {

  protected int hTicks;
  protected int vTicks;
  protected int nfft;
  protected int binSize;

  protected boolean logPower;
  protected boolean autoScale;

  protected double minFreq;
  protected double maxFreq;
  protected double minPower;
  protected double maxPower;
  protected double overlap;
  protected double viewStartTime;
  protected double viewEndTime;

  public boolean xTickMarks = true;
  public boolean xTickValues = true;
  public boolean xUnits = true;
  public boolean xLabel = false;
  public boolean yTickMarks = true;
  public boolean yTickValues = true;
  protected String timeZone;
  protected String dateFormatString = "yyyy-MM-dd HH:mm:ss";

  private String yLabelText = null;
  private String yUnitText = null;
  protected byte[] imgBuffer;
  protected Spectrum spectrum;
  protected MemoryImageSource mis;
  protected Image im;

  private Spectrogram spectrogram;

  protected SliceWave wave;

  protected FrameDecorator decorator;

  protected String channelTitle;

  private double[][] powerBuffer;

  /**
   * Default constructor
   */
  public SpectrogramRenderer() {
    axis = new AxisRenderer(this);
    hTicks = -1;
    vTicks = -1;
    minFreq = 0;
    maxFreq = 20;
    maxPower = -Double.MAX_VALUE;
    overlap = 0.859375;
    nfft = 0; // Auto
    binSize = 256;
    autoScale = false;
    logPower = false;
    spectrum = Jet2.getInstance();
  }

  /**
   * Constructor
   * 
   * @param w
   *            slice to present as spectrogram
   */
  public SpectrogramRenderer(SliceWave w) {
    this();
    wave = w;
  }

  /**
   * Set frame decorator
   * 
   * @param fd
   *            frame decorator
   */
  public void setFrameDecorator(FrameDecorator fd) {
    decorator = fd;
  }

  /**
   * Create default decorator to render frame
   */
  public void createDefaultFrameDecorator() {
    decorator = new DefaultWaveFrameDecorator();
  }

  /**
   * Set graph title
   * 
   * @param t
   *            title
   */
  public void setTitle(String t) {
    channelTitle = t.split("\\.")[0];
  }

  protected class DefaultWaveFrameDecorator extends DefaultFrameDecorator {
    public DefaultWaveFrameDecorator() {
      if (yUnitText != null) {
        this.yUnit = yUnitText;
      }
      if (yLabelText != null) {
        this.yAxisLabel = yLabelText;
      }
      if (xUnits) {
        this.xUnit = timeZone + " Time (" + J2kSec.format(dateFormatString, viewStartTime) + " to "
            + J2kSec.format(dateFormatString, viewEndTime) + ")";
      }
      this.xAxisLabels = xTickValues;
      this.yAxisLabels = yTickValues;
      if (!xTickMarks) {
        vTicks = 0;
      }
      if (!yTickMarks) {
        hTicks = 0;
      }
      this.title = channelTitle;
      this.titleBackground = Color.white;
    }
  }

  /**
   * Compute spectrogram. Reinitialize frame decorator with this renderer
   * data.
   * 
   * @param oldMaxPower
   * @return maximum magnitude
   */
  public double[] update() {
    if (decorator == null)
      createDefaultFrameDecorator();

    wave.setSlice(viewStartTime, viewEndTime);

    double[] signal = wave.getSignal();

    if (nfft == 0)
      nfft = binSize;

    spectrogram = new Spectrogram(signal, (int) wave.getSamplingRate(), nfft, binSize,
        (int) (binSize * overlap), 5);

    if (logPower)
      powerBuffer = spectrogram.getLogSpectraAmplitude();
    else
      powerBuffer = spectrogram.getSpectraAmplitude();

    int imgXSize = powerBuffer.length;
    int imgYSize = powerBuffer[0].length;

    imgBuffer = new byte[imgXSize * imgYSize];

    // Maps the range of power values to [0 254] (255/-1 is transparent).

    if (autoScale) {
      maxPower = Double.MIN_VALUE;
      minPower = Double.MAX_VALUE;
      for (int i = 0; i < imgXSize; i++)
        for (int j = 0; j < imgYSize; j++) {

          double power = powerBuffer[i][j];
          if (power == Double.NEGATIVE_INFINITY || power == Double.POSITIVE_INFINITY
              || power == 1E300 | power == -1E300)
            continue;

          else if (power > maxPower)
            maxPower = power;

          else if (power < minPower)
            minPower = power;
        }
    }

    double slope = 254 / (maxPower - minPower);
    double intercept = -slope * minPower;
    int counter = 0;
    double index;
    for (int i = imgXSize - 1; i >= 0; i--)
      for (int j = 0; j < imgYSize; j++) {

        index = slope * powerBuffer[i][j] + intercept;
        if (index < 0)
          index = 0;
        else if (index > 254)
          index = 254;
        imgBuffer[counter++] = (byte) index;
      }

    if (mis == null
        || (im != null && (im.getWidth(null) != imgXSize || im.getHeight(null) != imgYSize))) {
      mis = new MemoryImageSource(imgYSize, imgXSize, spectrum.palette, imgBuffer, 0, imgYSize);
    }

    im = Toolkit.getDefaultToolkit().createImage(mis);

    this.setImage(im);
    this.setDataExtents(wave.getStartTime(), wave.getEndTime(), 0, wave.getNyquist());
    this.setExtents(viewStartTime, viewEndTime,
        Math.max(minFreq, wave.getNyquist() / (imgXSize - 1)), maxFreq);
    decorator.decorate(this);

    double Power[] = {minPower, maxPower};
    return Power;

  }

  /**
   * Return powerBuffer
   * 
   * @return powerBuffer
   */
  public double[][] getPowerBuffer() {
    return powerBuffer;
  }

  /**
   * Return spectrogram
   * 
   * @return spectrogram
   */
  public Spectrogram getSpectrogram() {
    return spectrogram;
  }

  /**
   * Set autoscale flag
   * 
   * @param autoScale
   *            autoscale flag
   */
  public void setAutoScale(boolean autoScale) {
    this.autoScale = autoScale;
  }

  /**
   * Set size of fft
   * 
   * @param nfft
   *            Sets the number of points for the fft
   */
  public void setNfft(int nfft) {
    this.nfft = nfft;
  }

  /**
   * Set size of bin
   * 
   * @param binSize
   *            The bin size to set.
   */
  public void setBinSize(int binSize) {
    this.binSize = binSize;
  }

  // /**
  // * Set flag if we have logarithm frequency axis
  // * @param logFreq logarithm frequency axis flag
  // */
  // public void setLogFreq(boolean logFreq)
  // {
  // this.logFreq = logFreq;
  // }

  /**
   * Set flag if we have logarithm power axis
   * 
   * @param logPower
   *            logarithm power axis flag
   */
  public void setLogPower(boolean logPower) {
    this.logPower = logPower;
  }

  /**
   * Set maximum frequency
   * 
   * @param maxFreq
   *            maximum frequency
   */
  public void setMaxFreq(double maxFreq) {
    this.maxFreq = maxFreq;
  }

  /**
   * Set maximum power value
   * 
   * @param maxPower
   *            new maximum power
   */
  public void setMaxPower(double maxPower) {
    this.maxPower = maxPower;
  }

  /**
   * Set minimum power value
   * 
   * @param maxPower
   *            new minimum power
   */
  public void setMinPower(double minPower) {
    this.minPower = minPower;
  }

  /**
   * Set minimum frequency
   * 
   * @param minFreq
   *            minimum frequency
   */
  public void setMinFreq(double minFreq) {
    this.minFreq = minFreq;
  }

  /**
   * Set spectrogram overlapping flag
   * 
   * @param overlap
   *            spectrogram overlapping flag
   */
  public void setOverlap(double overlap) {
    this.overlap = overlap;
  }

  /**
   * Set viewEndTime.
   * 
   * @param viewEndTime
   *            view end time
   */
  public void setViewEndTime(double viewEndTime) {
    this.viewEndTime = viewEndTime;
  }

  /**
   * Set viewStartTime.
   * 
   * @param viewStartTime
   *            view start time
   */
  public void setViewStartTime(double viewStartTime) {
    this.viewStartTime = viewStartTime;
  }

  /**
   * Set viewStartTime.
   * 
   * @param viewStartTime
   *            view start time
   */
  public void setViewTimes() {
    viewStartTime = wave.getStartTime();
    viewEndTime = wave.getEndTime();
  }

  /**
   * Set Time Zone name.
   * 
   * @param timeZone
   *            time zone name
   */
  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  /**
   * Set Y axis label
   * 
   * @param s
   *            Y axis label
   */
  public void setYLabelText(String s) {
    yLabelText = s;
  }

  /**
   * Set Y axis unit
   * 
   * @param s
   *            Y axis unit
   */
  public void setYUnitText(String s) {
    yUnitText = s;
  }

  /**
   * Set slice to process
   * 
   * @param wave
   *            slice to process
   */
  public void setWave(SliceWave wave) {
    this.wave = wave;
  }

  /**
   * Set h ticks count
   * 
   * @param ticks
   *            h ticks count
   */
  public void setHTicks(int ticks) {
    hTicks = ticks;
  }

  /**
   * Set v ticks count
   * 
   * @param ticks
   *            v ticks count
   */
  public void setVTicks(int ticks) {
    vTicks = ticks;
  }

}
