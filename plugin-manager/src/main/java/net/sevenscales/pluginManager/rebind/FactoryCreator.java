package net.sevenscales.pluginManager.rebind;

import java.io.PrintWriter;
import java.util.List;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Génère un wrapper pour une classe appelé par GWT.create et qui implemente
 * l'interface SileneObject.
 * 
 * @author Jean-Philippe Dournel
 * 
 */
public class FactoryCreator {

	private TreeLogger logger;

	private GeneratorContext context;

	private TypeOracle typeOracle;

	private String typeName;

	public FactoryCreator(TreeLogger logger, GeneratorContext context,
			String typeName) {
		this.logger = logger;
		this.context = context;
		this.typeOracle = context.getTypeOracle();
		this.typeName = typeName;
	}

	public String createWrapper() {
	  try {
			PropertyOracle propertyOracle = context.getPropertyOracle();
//			String[] values;
//			ConfigurationProperty cp = propertyOracle.getConfigurationProperty("plugins");
//      values = propertyOracle.getPropertyValueSet(logger, "plugins");
//			values = pluginNames(cp.getValues());

			  String values[] = {"net.sevenscales.login.client.LoginPlugin", 
					  			 "net.sevenscales.share.plugin.SharePlugin",
					  			 "net.sevenscales.webAdmin.client.WebAdminPlugin"};

      JClassType classType;
      classType = typeOracle.getType(typeName);
      SourceWriter source = getSourceWriter(classType);

      if (source == null) {
        return classType.getParameterizedQualifiedSourceName() + "Wrapper";
      } else {
  			source.indent();
  			source.println(" public IPlugin[] plugins() {");
  			source.indent();
  			source.println("IPlugin[] result = new IPlugin[" + values.length + "];");
  			int i = 0;
  			for (String ps : values) {
  			  source.println("result[" + i + "] = (IPlugin) GWT.create(" + ps + ".class);");
  			  ++i;
  			}
  			source.println("return result;");
  			source.outdent();
        source.println("}");
  			
  			// secont func
        source.indent();
        source.println("public String getFactoryName() {");
        source.indent();
        source.println("return \"kettufactory\";");
        source.outdent();
  			source.println("}");
  			
  			source.commit(logger);
  			return classType.getParameterizedQualifiedSourceName() + "Wrapper";
      }
    } catch (NotFoundException e) {
      e.printStackTrace();
      return null;
    }
//    } catch (BadPropertyValueException e) {
//      e.printStackTrace();
//      return null;
//    }
	}

	/**
	 * SourceWriter instantiation. Return null if the resource already exist.
	 * 
	 * @return sourceWriter
	 */
	public SourceWriter getSourceWriter(JClassType classType) {
		String packageName = classType.getPackage().getName();
		String simpleName = classType.getSimpleSourceName() + "Wrapper";
		ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(
				packageName, simpleName);
		composer.addImplementedInterface("net.sevenscales.pluginManager.api.IPluginFactory");
		composer.addImport("net.sevenscales.pluginManager.api.IPlugin");
		composer.addImport("com.google.gwt.core.client.GWT");
		PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);
		if (printWriter != null) {
		  return composer.createSourceWriter(context, printWriter); 
		}
		return null; 
	}
	
	private String[] pluginNames(List<String> pluginNames) {
	  // by default there is a none plugin as well, which 
	  // is now filtered
	  String[] result = new String[pluginNames.size() - 1];
	  int i = 0;
	  for (String p : pluginNames) {
	    if (!p.equals("none")) {
	      // none plugin name is not added
  	    result[i] = p.replaceAll("_", ".");
  	    ++i;
	    }
	  }
	  
	  return result;
	}
}
