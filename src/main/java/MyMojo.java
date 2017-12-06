import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.objectweb.asm.tree.analysis.AnalyzerException;

@Mojo(name = "metrics")
public class MyMojo extends AbstractMojo{
	
	@Parameter(defaultValue = "${project.build.directory}")
	private String path;
	@Parameter(defaultValue = "${project.name}")
	private String name;
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			Log l = getLog();
			l.info("Project: " + name);
			l.info("Path: " + path);
			App a = new App(name, path, l);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AnalyzerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
