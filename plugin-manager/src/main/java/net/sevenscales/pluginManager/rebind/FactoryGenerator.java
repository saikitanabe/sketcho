package net.sevenscales.pluginManager.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

public class FactoryGenerator extends Generator {

	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
	  FactoryCreator binder = new FactoryCreator(logger, context, typeName);
		String className = binder.createWrapper();
		return className;
	}

}
