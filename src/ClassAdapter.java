import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;
import java.util.Arrays;

class ClassAdapter extends ClassVisitor implements Opcodes {
	String className;
	
	
    public ClassAdapter() {
        super(ASM5);
    }
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces){
		className = name;
		super.visit(version, access, name, signature, superName, interfaces);
	}

    @Override
    public MethodVisitor visitMethod(final int access, final String name,
            final String desc, final String signature, final String[] exceptions) {
				
		//System.out.printf("Access:\t\t\t%d\n", access);
		System.out.printf("Name:\t\t\t%s\n", name);
		//System.out.printf("Desc:\t\t\t%s\n", desc);
		System.out.printf("Signature:\t\t%s\n", signature);
		System.out.printf("Exceptions:\t\t%s\n", Arrays.toString(exceptions));
        System.out.printf("Number of Arguments:\t%d\n", Type.getArgumentTypes(desc).length);
		
		
		MethodNode mn = new MethodNode(access, name, desc, signature, exceptions);
		
		return new MethodAdapter();
    }
}