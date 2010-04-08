package nu.mulli.builderbuilder.gen;

import java.io.File;
import java.io.IOException;

import com.thoughtworks.qdox.model.JavaClass;

/**
 * Generates the actual java code for the generator class.
 */
public class BuilderGenerator {
	private File outputDirectory;
    
	public BuilderGenerator(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public void generateBuilderFor(JavaClass clazz) throws IOException {
		outputDirectory.mkdirs();
	}
	
}