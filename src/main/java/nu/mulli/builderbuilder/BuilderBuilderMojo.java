package nu.mulli.builderbuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.antlr.stringtemplate.StringTemplate;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

/**
 * @goal generate-sources
 * @phase generate-sources
 */
public class BuilderBuilderMojo extends AbstractCodeGeneratorMojo {

	@Override
	public void generate() throws Exception {
		for (JavaClass jc : docBuilder.getClasses()) {
			generateBuilderFor(jc);
		}
	}

	public void generateBuilderFor(JavaClass jc) throws IOException {
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

					String packageName = dc.getNamedParameter("package");
					if (packageName == null)
						packageName = jc.getPackageName();

                    String extendsClass = dc.getNamedParameter("extends");

					StringTemplate st = templates.getInstanceOf(
						builderAbstract ? "abstractBuilder" : "builder");

					st.setAttribute("packageName", packageName);
					st.setAttribute("builderName", builderName);
					st.setAttribute("resultClass", jc.asType().toString());
					st.setAttribute("createMethod", createMethod);
                    st.setAttribute("extendsClass", extendsClass);

					List<Param> ps = new LinkedList<Param>();
					List<Param> cs = new LinkedList<Param>();
					for(JavaParameter p: m.getParameters()) {
                        Param param = new Param(p.getType().toGenericString(), p.getName());
                        if (!p.getName().startsWith("_"))
                            ps.add(param);
                        cs.add(param);
					}
					st.setAttribute("parameters", ps);
                    st.setAttribute("arguments", cs);

					File pd = new File(outputDirectory, packageName.replaceAll("\\.", "/"));
					pd.mkdirs();

					FileWriter out = new FileWriter(new File(pd, builderName + ".java"));
					try {
						out.append(st.toString());
					} finally {
						out.flush();
						out.close();
					}

					if (getLog().isDebugEnabled()) {
						getLog().debug(builderName + ".java :");
						getLog().debug(st.toString());
					}
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