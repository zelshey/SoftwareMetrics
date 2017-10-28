
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;


class AnnotationAdapter extends AnnotationVisitor implements Opcodes {


	public AnnotationAdapter(final AnnotationVisitor av) {
        super(ASM5, av);
    }
	
	
	@Override
	public void visit(String name, Object value){
		System.out.printf("Name:%s\nValue:%s\n", name, value.toString());
		super.visit(name, value);
	}

}