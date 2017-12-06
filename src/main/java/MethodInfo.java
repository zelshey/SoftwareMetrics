import org.apache.maven.plugin.logging.Log;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;

public class MethodInfo {

	public String get(MethodNode method, String className, Log l) throws AnalyzerException{
		MethodMetrics metrics = new MethodMetrics(className);
		InstructionInfo ii = new InstructionInfo();
		metrics.name = method.name + method.desc;
		metrics.numArgs = Type.getArgumentTypes(method.desc).length;
		metrics.setMods(method.access);
		metrics.thrownExceptions = method.exceptions;
		for(int i = 0; i < method.instructions.size(); i++){
			AbstractInsnNode inst = method.instructions.get(i);
			ii.get(inst, metrics);
		}
		if(method.instructions.size() > 0){
			AnalyzerAdapter a = new AnalyzerAdapter(new BasicInterpreter(), metrics);
			a.analyze(className, method);
			metrics.numLoops = a.cfg.loops.size();
			metrics.maxDepth = a.cfg.maxDepthNesting();
			metrics.totalDepth = a.cfg.totalDepthNesting();
			metrics.cycoComplex = a.getCyclomaticComplexity();
		}
		metrics.dispMetrics(l);
		return metrics.prepForCSV();
	}
	
	public String csvHeader(){
		return "Name,Cyclomatic Complexity,NumArgs,NumVarDec,NumVarRef,NumStat,MaxNestingDepth,"
				   + "HLTH,HVOC,HDIF,HVOL,HEFF,HBUG,TotalNestingDepth,NumCasts,NumLoops,NumOp,NumOd,ClassRef,"
				   + "ExternalMethods,LocalMethods,ExcRef,ThrownExc,Modifiers,NumLines\n";
	}
}
