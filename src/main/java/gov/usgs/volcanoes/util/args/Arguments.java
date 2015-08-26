package gov.usgs.volcanoes.util.args;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.ParseException;

public interface Arguments {
	public JSAPResult parse(String[] args) throws ParseException;
	public void registerParameter(Parameter parameter) throws JSAPException;
}
