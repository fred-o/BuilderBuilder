package nu.mulli.builderbuilder.mojo;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.antlr.stringtemplate.StringTemplateGroup;
import org.apache.maven.plugin.AbstractMojo;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * @goal generate-sources
 * @phase generate-sources
 */
public class BuilderBuilderMojo extends AbstractMojo {
	/**
	 * Resources
	 * 
	 * @parameter
	 * @required
	 */
	List<String> sources;

	/**
	 * @parameter default-value="target/generated-sources/builderbuilder"
	 * @required
	 */
	File outputDirectory;

	@Override
	public void execute() {
		try {
			System.out.println("OUTPUT: " + outputDirectory.getAbsolutePath());

			InputStream is = BuilderBuilderMojo.class.getClassLoader().getResourceAsStream("builderbuilder.stg");
			StringTemplateGroup templates = new StringTemplateGroup(new InputStreamReader(is));

			BuilderGenerator generator = new BuilderGenerator(outputDirectory, templates);
			JavaDocBuilder docBuilder = new JavaDocBuilder();

			for (String r : sources) {
				System.out.println("SOURCE: " + r);
				docBuilder.addSourceTree(new File(r));
			}
			for (JavaClass jc : docBuilder.getClasses()) {
				System.out.println("CLASS: " + jc.toString());
				generator.generateBuilderFor(jc);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}