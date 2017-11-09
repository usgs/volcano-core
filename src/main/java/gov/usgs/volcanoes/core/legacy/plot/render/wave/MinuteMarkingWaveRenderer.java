package gov.usgs.volcanoes.core.legacy.plot.render.wave;

import gov.usgs.volcanoes.core.legacy.plot.render.AxisRenderer;

import java.awt.Color;

/**
 * Customized SliceWaveRenderer
 * This really needs to be generalized, but it works for now...
 * 
 * TODO: generalize
 * 
 * @author Tom Parker
 */
public class MinuteMarkingWaveRenderer extends SliceWaveRenderer {

  /**
   * Set axis ticks and labels values 
   */
  public void update() {
    this.setExtents(viewStartTime, viewEndTime, minY, maxY);
    int hTicks = graphWidth / 108;
    int vTicks = graphHeight / 24;
    this.createAxis(vTicks);
    this.setXAxisToTime(hTicks, false, false);
    this.getAxis().setInnerLeftLabelAsText(yLabelText, -46);
    if (title != null)
      this.getAxis().setLeftLabelAsText(title, -56);
  }


  /**
   * create axis with x-axis tick every minute 
   * @param vTicks
   */
  public void createAxis(int vTicks) {
    if (axis == null)
      axis = new AxisRenderer(this);
    axis.createDefault();
    createDefaultYAxis(vTicks, yTickMarks, yTickValues, true);

    int firstTick = (int) (getMinXAxis() + (1 - (getMinXAxis() % 60)));
    int lastTick = (int) (getMaxXAxis() - (getMinXAxis() % 60));
    int tickCount = 1 + (lastTick - firstTick) / 60;
    double[] tickList = new double[tickCount];
    for (int i = 0; i < tickCount; i++)
      tickList[i] = firstTick + i * 60;

    if (xTickMarks) {
      axis.createTopTicks(tickList, 5, Color.GRAY);
      axis.createBottomTicks(tickList, 5, Color.GRAY);
      // axis.createVerticalGridLines(tickList);
    }
    if (xTickValues) {
      axis.createBottomTickLabels(tickList, null);
    }
  }

}
