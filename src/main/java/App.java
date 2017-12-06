import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.maven.plugin.logging.Log;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

public class App {

	
	public App(String n, String p, Log l) throws IOException, AnalyzerException{
		File f = new File(p);
		PrintWriter pw = new PrintWriter(new File(n.toUpperCase() + "_METRICS.csv"));
		if(f.isFile() && isFileType(f.getName(), "class")){
			processFile(f, pw, l);
		}else if(f.isFile() && isFileType(f.getName(), "jar")){
			//processJar(f, pw, l);
		}else if(f.isDirectory()){
			processDirectory(f, pw, l);
		}
		pw.close();
	}
	
	
	public static void processJar(File file, PrintWriter pw, Log l) throws IOException, AnalyzerException{
		
		URL url = file.toURI().toURL();
		URL[] urls = new URL[]{url};
		ClassLoader cl = new URLClassLoader(urls);		
		FileInputStream fis = new FileInputStream(file);
		ZipInputStream zis = new ZipInputStream(fis);
		ZipEntry ze;
		while((ze = zis.getNextEntry()) != null){
			String name = ze.getName();
			if(isFileType(name, "class")){
				InputStream is = cl.getResourceAsStream(ze.getName());
				processFile(is, pw, l);
			}
		}
	}
	
	public static void processDirectory(File file, PrintWriter pw, Log l) throws IOException, AnalyzerException{
		for(File f: file.listFiles()){
			if(f.isFile() && isFileType(f.getName(), "class")){
				processFile(f, pw, l);
			}else if(f.isFile() && isFileType(f.getName(), "jar")){
				//processJar(f, pw, l);
			}else if(f.isDirectory()){
				processDirectory(f, pw, l);
			}
		}
	}

	public static boolean isFileType(String path, String type){
		return fileType(path).equals(type);
	}
	
	public static String fileType(String path){
		int index = path.lastIndexOf('.');
		return (index == -1) ? "" : path.substring(index + 1);
	}
	
	
	public static void processFile(InputStream is, PrintWriter pw, Log l) throws IOException, AnalyzerException{
		
        ClassReader cr = new ClassReader(is);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        
        int len = cn.name.length();
        printX(len + 4, '*', l);
        l.info("* " + cn.name + " *");
        printX(len + 4, '*', l);
		MethodInfo mi = new MethodInfo();
		
		pw.write(cn.name + '\n');
		pw.write(mi.csvHeader());
		List<MethodNode> methods = cn.methods;
		for(MethodNode method : methods){
			pw.write(mi.get(method, cn.name, l));
		}
		pw.write("\n");
		is.close();
	}
	
	public static void processFile(File f, PrintWriter pw, Log l) throws IOException, AnalyzerException{
		FileInputStream is = new FileInputStream(f);
		processFile(is, pw, l);
	}
	
	public static void printX(int x, char c, Log l) {
		String s = "";
		for (int i = 0; i < x; i++) {
			s = s + c;
		}
		l.info(s);
	}
	
	public static void viewByteCode(ClassNode cn){
        final List<MethodNode> mns = cn.methods; 
        Printer printer = new Textifier(); 
        TraceMethodVisitor mp = new TraceMethodVisitor(printer); 
        for (MethodNode mn : mns) { 
            InsnList inList = mn.instructions; 
            System.out.println(mn.name); 
            for (int i = 0; i < inList.size(); i++) { 
                inList.get(i).accept(mp); 
                StringWriter sw = new StringWriter(); 
                printer.print(new PrintWriter(sw)); 
                printer.getText().clear(); 
                System.out.print(sw.toString()); 
            } 
        } 
	}
}
