package nu.mulli.builderbuilder.mojo;

import java.io.File;
import java.util.List;

import org.apache.maven.model.Resource;
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

			BuilderGenerator generator = new BuilderGenerator(outputDirectory);
			JavaDocBuilder docBuilder = new JavaDocBuilder();

			for(String r: sources) {
				System.out.println("SOURCE: " + r);
			    docBuilder.addSourceTree(new File(r));
			}
			for(JavaClass jc: docBuilder.getClasses()) {
				System.out.println("CLASS: " + jc.toString());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}