import org.objectweb.asm.tree.*;
import org.objectweb.asm.*;
import java.util.*;
import java.io.FileInputStream;
import java.io.InputStream;




public class Analize implements Opcodes{
	int num_lines;
	
	public static void main(String[] args) throws Exception{
		FileInputStream is = new FileInputStream(args[0]);
        ClassReader cr = new ClassReader(is);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
		Analize analize = new Analize();
		
		List<MethodNode> methods = cn.methods;
		for(MethodNode method : methods){
			analize.getMethodInfo(method);
		}
	}
	
	
	public void getMethodInfo(MethodNode method){
		System.out.printf("Name:\t\t\t%s\n", method.name);
		System.out.printf("Exceptions:\t\t%d\n", method.exceptions.size());
		if(method.exceptions.size()>0){
			System.out.printf("\t\t\t%s\n", method.exceptions.toString());
		}
		System.out.printf("Number of Arguments:\t%d\n", Type.getArgumentTypes(method.desc).length);
		System.out.printf("Signature:\t\t%s\n", method.signature);
		System.out.printf("Local Vars:\t\t%d\n", method.localVariables.size());
		System.out.printf("Max Locals:\t\t%d\n", method.maxLocals	);
		
		System.out.printf("Instructions:\t\t%d\n", method.instructions.size());
		
		num_lines = 0;
		
		for(int i = 0; i < method.instructions.size(); i++){
			AbstractInsnNode instruction = method.instructions.get(i);
			getInstructionInfo(instruction);
		}
		
		System.out.printf("Number of lines:\t%d\n", num_lines);
		System.out.println();
	}
	
	public void getInstructionInfo(AbstractInsnNode instruction){
		System.out.printf("%d\t%d\n", instruction.getOpcode(), instruction.getType());
		switch(instruction.getType()){
			case 15://LINE
				num_lines++;
				break;
			case 2://VAR_INSN
				
				break;
		}
	}
}