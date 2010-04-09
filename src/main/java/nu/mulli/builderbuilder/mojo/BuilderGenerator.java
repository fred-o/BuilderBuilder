package nu.mulli.builderbuilder.mojo;

import java.io.File;
import java.io.IOException;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

/**
 * Generates the actual java code for the generator class.
 */
public class BuilderGenerator {
	private File outputDirectory;
	private StringTemplateGroup templateGroup;
    
	public BuilderGenerator(File outputDirectory, StringTemplateGroup templateGroup) {
		this.outputDirectory = outputDirectory;
		this.templateGroup = templateGroup;
	}
	
	public void generateBuilderFor(JavaClass jc) throws IOException {
		outputDirectory.mkdirs();
		for(JavaMethod m: jc.getMethods()) {
		    if (m.isConstructor()) {
				DocletTag dc = m.getTagByName("builder");
				if (dc != null) {
					boolean builderAbstract = Boolean.valueOf(dc.getNamedParameter("abstract"));

					String builderName = dc.getNamedParameter("name");
					if (builderName == null) {
						if (builderAbstract)
							builderName = "Abstract" + jc.getName() + "Builder";
						else
							builderName = jc.getName() + "Builder";
					}

					String createMethod = dc.getNamedParameter("createMethod");
					if (createMethod == null)
						createMethod = "create";

					StringTemplate st = templateGroup.getInstanceOf(
						builderAbstract ? "abstractBuilder" : "builder");

					st.setAttribute("packageName", jc.getPackageName());
					st.setAttribute("builderName", builderName);
					st.setAttribute("resultClass", jc.asType().toString());
					st.setAttribute("createMethod", createMethod);
					st.setAttribute("parameters", m.getParameters());

					System.out.println(st.toString());
				} 
			} 
		}
	}


	
}