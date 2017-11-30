import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class InstructionInfo {

	public void get(AbstractInsnNode instruction, MethodMetrics metrics) {
		int type = instruction.getType();
		int opcode = instruction.getOpcode();
		switch (type) {
		case AbstractInsnNode.INSN:// 0
			InsnNode in = (InsnNode) instruction;
			if (opcode >= Opcodes.ACONST_NULL && opcode <= Opcodes.DCONST_1) {
				metrics.loadConstant(opcode);
			}
			if (opcode >= Opcodes.IADD && opcode <= Opcodes.LXOR) {
				metrics.math(opcode);
			}
			if (opcode >= Opcodes.I2L && opcode <= Opcodes.I2S) {
				metrics.casts++;
			}
			if(opcode >= Opcodes.LCMP && opcode <= Opcodes.DCMPG){
				metrics.compare(opcode);
			}
			break;
		case AbstractInsnNode.INT_INSN:// 1
			IntInsnNode iin = (IntInsnNode) instruction;
			if (opcode >= Opcodes.BIPUSH && opcode <= Opcodes.SIPUSH) {
				metrics.loadInt(iin.operand);
			}
			break;
		case AbstractInsnNode.VAR_INSN:// 2
			VarInsnNode vin = (VarInsnNode) instruction;
			int var = vin.var;
			if (opcode >= Opcodes.ISTORE && opcode <= Opcodes.ASTORE) {
				metrics.storeVar(var);
			}
			if (opcode >= Opcodes.ILOAD && opcode <= Opcodes.ALOAD) {
				metrics.loadVar(var);
			}
			break;
		case AbstractInsnNode.TYPE_INSN:// 3
			TypeInsnNode tin = (TypeInsnNode) instruction;
			metrics.classRef(tin.desc);
			break;
		case AbstractInsnNode.FIELD_INSN:// 4
			FieldInsnNode fin = (FieldInsnNode) instruction;
			metrics.classRef(fin.owner);
			break;
		case AbstractInsnNode.METHOD_INSN:// 5
			MethodInsnNode min = (MethodInsnNode) instruction;
			metrics.methods(min.name, min.owner);
			break;
		case AbstractInsnNode.INVOKE_DYNAMIC_INSN:// 6
			InvokeDynamicInsnNode idin = (InvokeDynamicInsnNode) instruction;
			break;
		case AbstractInsnNode.JUMP_INSN:// 7
			JumpInsnNode jin = (JumpInsnNode) instruction;
			if(opcode >= Opcodes.IFEQ && opcode <= Opcodes.IF_ACMPNE){
				metrics.compare(opcode);
			}
			break;
		case AbstractInsnNode.LABEL:// 8
			LabelNode ln = (LabelNode) instruction;
			metrics.numStatments++;
			break;
		case AbstractInsnNode.LDC_INSN:// 9
			LdcInsnNode lin = (LdcInsnNode) instruction;
			metrics.loadLdc(lin.cst);
			break;
		case AbstractInsnNode.IINC_INSN:// 10
			IincInsnNode incn = (IincInsnNode) instruction;
			metrics.incn(incn.var, incn.incr);
			break;
		case AbstractInsnNode.TABLESWITCH_INSN:// 11
			TableSwitchInsnNode tsin = (TableSwitchInsnNode) instruction;
			break;
		case AbstractInsnNode.LOOKUPSWITCH_INSN:// 12
			LookupSwitchInsnNode lsin = (LookupSwitchInsnNode) instruction;
			break;
		case AbstractInsnNode.MULTIANEWARRAY_INSN:// 13
			MultiANewArrayInsnNode manain = (MultiANewArrayInsnNode) instruction;
			break;
		case AbstractInsnNode.FRAME:// 14
			FrameNode fn = (FrameNode) instruction;
			break;
		case AbstractInsnNode.LINE:// 15
			LineNumberNode lnn = (LineNumberNode) instruction;
			metrics.numLines++;
			break;
		}
	}

}
