package gov.usgs.volcanoes.core.contrib.hypo71.pause;

/**
 * Represents Hypo71 crustal model card.
 * 
 * @author Oleg Shepelev
 * @author Tom Parker
 */
public class CrustalModel {
	public final double v;
	public final double d;

	public CrustalModel(double v, double d) {
		this.v = v;
		this.d = d;
	}
}