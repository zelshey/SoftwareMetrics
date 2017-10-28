import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.AnnotationVisitor;

class MethodAdapter extends MethodVisitor implements Opcodes {
	int numLines;

    public MethodAdapter(final MethodVisitor mv) {
        super(ASM5, mv);
    }

    @Override
    public void visitCode(){
    	numLines = 0;
		super.visitCode();
    }
	
	@Override
	public void visitEnd(){
		System.out.printf("Lines of Code:\t\t%d\n\n", numLines);
		super.visitEnd();
	}
	
	@Override
    public void visitLineNumber(int line, Label start){
		numLines++;
		super.visitLineNumber(line, start);
	}	
}