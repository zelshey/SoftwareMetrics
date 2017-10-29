import java.io.FileInputStream;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;

import org.objectweb.asm.ClassVisitor;

public class GetMetrics {
    public static void main(final String args[]) throws Exception {
        FileInputStream is = new FileInputStream(args[0]);

        ClassReader cr = new ClassReader(is);
        ClassAdapter ca = new ClassAdapter();
		
        cr.accept(ca, 0);
    }
}