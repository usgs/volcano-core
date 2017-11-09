package gov.usgs.volcanoes.core.data;


import gov.usgs.volcanoes.core.legacy.plot.Plot;
import gov.usgs.volcanoes.core.legacy.plot.render.HelicorderRenderer;

import java.awt.Color;
import java.util.TimeZone;

/**
 * A class that encapsulated the settings for a HelicorderRenderer.
 *
 * @author Dan Cervelli
 */
public class HelicorderSettings {

  public String channel;
  public String channelCode;
  public double endTime;
  public double startTime;
  public double timeChunk = 20 * 60;

  public int width = 1000;
  public int height = 1000;
  public int left = 70;
  public int top = 20;

  public int clipValue = -1;
  public int barRange = -1;
  public float barMult = 3;
  public boolean showClip = false;
  public boolean forceCenter = false;

  public String timeZoneAbbr = "GMT";
  public double timeZoneOffset = 0;
  public TimeZone timeZone = TimeZone.getTimeZone(timeZoneAbbr);

  public boolean minimumAxis = false;
  public boolean largeChannelDisplay = false;
  public boolean showDecorator = true;
  public boolean showLegend = false;

  /**
   * Apply settings stored in this class to HelicorderRenderer
   * @param hr renderer to tune
   * @param hd data to apply to renderer
   */
  public void applySettings(HelicorderRenderer hr, HelicorderData hd) {

    hr.setChannel(channel);
    hr.setData(hd);
    hr.setTimeChunk(timeChunk);
    hr.setLocation(left, top, width, height);
    hr.setForceCenter(forceCenter);

    double mean = hd.getMeanMax();
    double bias = hd.getBias();
    mean = Math.abs(bias - mean);

    // auto-scale
    if (minimumAxis)
      barMult = 6;
    if (clipValue == -1)
      clipValue = (int) (21 * mean);
    if (barRange == -1)
      barRange = (int) (barMult * mean);

    hr.setHelicorderExtents(startTime, endTime, -1 * Math.abs(barRange), Math.abs(barRange));
    hr.setClipValue(clipValue);
    hr.setShowClip(showClip);
    hr.setTimeZone(timeZone);
    hr.setShowDecorator(showDecorator);
    hr.setLargeChannelDisplay(largeChannelDisplay);
    if (minimumAxis)
      hr.createMinimumAxis();
    else
      hr.createDefaultAxis();
    if (showLegend)
      hr.createDefaultLegendRenderer(new String[] {channelCode});
  }

  /**
   * Compute helicorder size
   * @param w plot width
   * @param h plot height
   */
  public void setSizeFromPlotSize(int w, int h) {
    width = w - left * 2;
    height = h - (top + 50);
  }

  /**
   * set helicorder position
   */
  public void setMinimumSizes() {
    left = 31;
    top = 16;
  }

  /**
   * Create plot 
   * @param hd data to render
   * @return plot created
   */
  public Plot createPlot(HelicorderData hd) {
    Plot plot = new Plot();
    plot.setSize(width + 140, height + 70);
    plot.setBackgroundColor(new Color(0.97f, 0.97f, 0.97f));
    HelicorderRenderer hr = new HelicorderRenderer();
    hr.setChannel(channel);
    applySettings(hr, hd);
    plot.addRenderer(hr);
    return plot;
  }
}
