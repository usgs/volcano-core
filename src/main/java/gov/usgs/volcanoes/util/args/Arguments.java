package gov.usgs.volcanoes.util.args;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;

/**
 * 
 * @author Tom Parker
 * 
 *         I waive copyright and related rights in the this work worldwide
 *         through the CC0 1.0 Universal public domain dedication.
 *         https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */
public interface Arguments {
	public JSAPResult parse(String[] args) throws Exception;

	public void registerParameter(Parameter parameter) throws JSAPException;

	public Parameter getById(String id);
}
