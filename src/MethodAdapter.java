import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;

class MethodAdapter extends MethodVisitor implements Opcodes {
	int numLines;

    public MethodAdapter(final MethodVisitor mv) {
        super(ASM5, mv);
    }

    @Override
    public void visitCode(){
    	System.out.println("Visiting Method");
    	numLines = 0;
		super.visitCode();
    }
	
	@Override
	public void visitEnd(){
		System.out.printf("Lines of Code:%d\n", numLines);
		System.out.println("Done visiting Method\n\n");
		super.visitEnd();
	}
	
	@Override
    public void visitLineNumber(int line, Label start){
		numLines++;
		super.visitLineNumber(line, start);
	}
}