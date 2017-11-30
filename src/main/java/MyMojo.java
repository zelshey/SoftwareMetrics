import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.objectweb.asm.tree.analysis.AnalyzerException;


@Mojo(name = "metrics")
public class MyMojo extends AbstractMojo{
	
	@Parameter(property = "path")
	private String path;
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			App a = new App(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AnalyzerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
