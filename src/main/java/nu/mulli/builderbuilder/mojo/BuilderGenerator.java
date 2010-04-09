package nu.mulli.builderbuilder.mojo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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

					List<Param> ps = new LinkedList<Param>();
					for(JavaParameter p: m.getParameters()) {
					    ps.add(new Param(p.getType().toGenericString(), p.getName()));
					}
					st.setAttribute("parameters", ps);

					File pd = new File(outputDirectory, jc.getPackageName().replaceAll("\\.", "/"));
					pd.mkdirs();

					FileWriter out = new FileWriter(new File(pd, builderName + ".java"));
					try {
						out.append(st.toString());
					} finally {
						out.flush();
						out.close();
					}

					System.out.println(st.toString());
				} 
			} 
		}
	}

	public static class Param {
		public final String type;
		public final String name;
		
		public Param(String type, String name) {
			this.type = type;
			this.name = name;
		}
	}

	
}