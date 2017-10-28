import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

class ClassAdapter extends ClassVisitor implements Opcodes {

    public ClassAdapter(final ClassVisitor cv) {
        super(ASM5, cv);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name,
            final String desc, final String signature, final String[] exceptions) {
		
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
		
		System.out.printf("Access:\t\t\t%d\n", access);
		System.out.printf("name:\t\t\t%s\n", name);
		System.out.printf("desc:\t\t\t%s\n", desc);
		System.out.printf("signature:\t\t%s\n", signature);
		System.out.printf("Number of Arguments:\t%d\n", Type.getArgumentTypes(desc).length);
        
		return mv == null ? null : new MethodAdapter(mv);
    }
}