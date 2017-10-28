import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class MethodAdapter extends MethodVisitor implements Opcodes {

    public MethodAdapter(final MethodVisitor mv) {
        super(ASM5, mv);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
		System.out.println(name);
    }
}