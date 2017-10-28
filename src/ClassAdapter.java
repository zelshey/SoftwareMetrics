import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class ClassAdapter extends ClassVisitor implements Opcodes {

    public ClassAdapter(final ClassVisitor cv) {
        super(ASM5, cv);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name,
            final String desc, final String signature, final String[] exceptions) {
		System.out.printf("Access:\t\t%d\nName:\t\t%s\nDesc:\t\t%s\nSignature:\t%s\n\n", access, name, desc, signature);
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        return mv;
    }
}