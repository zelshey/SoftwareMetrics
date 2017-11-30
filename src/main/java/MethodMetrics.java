import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;

public class MethodMetrics {
	String name = "";
	int numArgs = 0;
	List<String> thrownExceptions;
	int numLines = 0;
	int numStatments = 0;
	String modifiers = "";
	int casts = 0;
	// halstead metrics
	int LTH = 0;
	int VOC = 0;
	double DIF = 0;
	double VOL = 0;
	double EFF = 0;
	double BUG = 0;

	int cycoComplex = 0;
	String owner = "";
	int numLoops = 0;
	int maxDepth = 0;
	int totalDepth = 0;

	Map<Integer, Integer> varDec = new HashMap<Integer, Integer>();// var_num,
																	// count
	Map<Integer, Integer> constants = new HashMap<Integer, Integer>();// op_code,
																		// count
	Map<Integer, Integer> ints = new HashMap<Integer, Integer>();// value, count
	Map<Object, Integer> ldcs = new HashMap<Object, Integer>();// ldc, count
	Map<Integer, Integer> varLoads = new HashMap<Integer, Integer>();// var_num,
																		// count
	Map<Integer, Integer> varStores = new HashMap<Integer, Integer>();// var_num,
																		// count
	Map<Integer, Integer> varRefs = new HashMap<Integer, Integer>();// var_num,
																	// count
	Map<Integer, Integer> math = new HashMap<Integer, Integer>();// op_code,
																	// count
	Map<Integer, Integer> comp = new HashMap<Integer, Integer>();// op_code,
																	// count
	Map<String, Integer> localMethods = new HashMap<String, Integer>();// name,
																		// count
	Map<String, Integer> externalMethods = new HashMap<String, Integer>();// name,
																			// count
	Map<String, Integer> classRef = new HashMap<String, Integer>();// name,
																	// count
	Map<String, Integer> exceptionRef = new HashMap<String, Integer>();// name,
																		// count

	public MethodMetrics(String owner) {
		this.owner = owner;
	}

	// deal with opcodes
	public void loadConstant(int op) {// 1-15
		int count = constants.containsKey(op) ? (constants.get(op) + 1) : 1;
		constants.put(op, count);
	}

	public void loadInt(int value) {// 16-17
		int count = ints.containsKey(value) ? (ints.get(value) + 1) : 1;
		ints.put(value, count);
	}

	public void loadLdc(Object obj) {// 18
		int count = ldcs.containsKey(obj) ? (ldcs.get(obj) + 1) : 1;
		ldcs.put(obj, count);
	}

	public void loadVar(int var) {// 21-25
		int count = varLoads.containsKey(var) ? (varLoads.get(var) + 1) : 1;
		varLoads.put(var, count);
		count = varRefs.containsKey(var) ? (varRefs.get(var) + 1) : 1;
		varRefs.put(var, count);
	}

	public void storeVar(int var) {// 54-58
		int count = varStores.containsKey(var) ? (varStores.get(var) + 1) : 1;
		varStores.put(var, count);
		count = varRefs.containsKey(var) ? (varRefs.get(var) + 1) : 1;
		varRefs.put(var, count);
		count = math.containsKey(2) ? (math.get(2) + 1) : 1;
		math.put(2, count);
		if (var > numArgs) {
			count = varDec.containsKey(var) ? (varDec.get(var) + 1) : 1;
			varDec.put(var, count);
		}
	}

	public void math(int op) {// 96-131
		int count = math.containsKey(op) ? (math.get(op) + 1) : 1;
		math.put(op, count);
	}

	public void incn(int var, int inc) {// 132
		int count = varRefs.containsKey(var) ? (varRefs.get(var) + 1) : 1;
		varRefs.put(var, count);
		count = math.containsKey(inc) ? (math.get(inc) + 1) : 1;
		math.put(inc, count);
	}

	public void compare(int op) {// 148-166
		int count = comp.containsKey(op) ? (comp.get(op) + 1) : 1;
		comp.put(op, count);
	}

	public void methods(String name, String owner) {// 182-185
		classRef(owner);
		if (owner.equals(this.owner)) {
			int count = localMethods.containsKey(name) ? (localMethods.get(name) + 1) : 1;
			localMethods.put(name, count);
		} else {
			int count = externalMethods.containsKey(name) ? (externalMethods.get(name) + 1) : 1;
			externalMethods.put(name, count);
		}
	}

	public void classRef(String name) {// 178-185, 189, 192-193
		int count = classRef.containsKey(name) ? (classRef.get(name) + 1) : 1;
		classRef.put(name, count);
	}

	public void excRef(String name) {
		int count = exceptionRef.containsKey(name) ? (exceptionRef.get(name) + 1) : 1;
		exceptionRef.put(name, count);
	}

	public int countUniqueOperands() {
		int count = 0;
		count += constants.size();
		count += varRefs.size();
		count += ldcs.size();
		return count;
	}

	public int countUniqueOperators() {
		int count = 0;
		count += math.size();
		count += comp.size();
		return count;
	}

	public int countOperands() {
		int count = 0;
		count += countObj(constants);
		count += countObj(varRefs);
		count += countObj(ldcs);
		return count;
	}

	public int countOperators() {
		int count = 0;
		count += countObj(math);
		count += countObj(comp);
		return count;
	}

	public int countObj(Map<?, Integer> obj) {
		int count = 0;
		for (Object i : obj.keySet()) {
			count += obj.get(i);
		}
		return count;
	}

	public void halstead() {
		LTH = countOperators() + countOperands();
		VOC = countUniqueOperators() + countUniqueOperands();
		DIF = ((double) countUniqueOperators() / 2.0) * ((double) countOperands() / (double) countUniqueOperands());
		VOL = (double) LTH * Math.log(VOC) / Math.log(2);
		EFF = DIF * VOL;
		BUG = VOL / 3000;
	}

	public void setMods(int access) {
		if (access == 0) {
			modifiers = "none";
			return;
		}
		if ((access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC)
			modifiers += "public ";
		if ((access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE)
			modifiers += "private ";
		if ((access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED)
			modifiers += "protected ";
		if ((access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC)
			modifiers += "static ";
		if ((access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL)
			modifiers += "final ";
		if ((access & Opcodes.ACC_SYNCHRONIZED) == Opcodes.ACC_SYNCHRONIZED)
			modifiers += "synchronized ";
		if ((access & Opcodes.ACC_BRIDGE) == Opcodes.ACC_BRIDGE)
			modifiers += "bridge ";
		if ((access & Opcodes.ACC_VARARGS) == Opcodes.ACC_VARARGS)
			modifiers += "varargs ";
		if ((access & Opcodes.ACC_NATIVE) == Opcodes.ACC_NATIVE)
			modifiers += "native ";
		if ((access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT)
			modifiers += "abstract ";
		if ((access & Opcodes.ACC_STRICT) == Opcodes.ACC_STRICT)
			modifiers += "strict ";
		if ((access & Opcodes.ACC_SYNTHETIC) == Opcodes.ACC_SYNTHETIC)
			modifiers += "synthetic ";
		if ((access & Opcodes.ACC_DEPRECATED) == Opcodes.ACC_DEPRECATED)
			modifiers += "deprecated ";
	}

	public void printX(int x, char c) {
		for (int i = 0; i < x; i++) {
			System.out.print(c);
		}
		System.out.println();
	}

	public void dispMetrics() {
		halstead();
		int off = 40;
		printX(off * 2, '=');
		System.out.printf("%-" + off + "s%s\n", "Name:", name);
		System.out.printf("%-" + off + "s%d\n", "Cyclomatic Complexity", cycoComplex);
		System.out.printf("%-" + off + "s%d\n", "Number of Arguments", numArgs);
		System.out.printf("%-" + off + "s%d\n", "Number of Variable Declarations:", varDec.size());
		System.out.printf("%-" + off + "s%d\n", "Number of Variable References:", varRefs.size());
		System.out.printf("%-" + off + "s%d\n", "Number of Statements:", numStatments);
		// num expressions
		System.out.printf("%-" + off + "s%d\n", "Max Depth of Nesting:", maxDepth);
		System.out.printf("%-" + off + "s%d\n", "Halstead Length:", LTH);
		System.out.printf("%-" + off + "s%d\n", "Halstead Vocabulary:", VOC);
		System.out.printf("%-" + off + "s%.2f\n", "Halstead Difficulty:", DIF);
		System.out.printf("%-" + off + "s%.2f\n", "Halstead Volume:", VOL);
		System.out.printf("%-" + off + "s%.2f\n", "Halstead Effort:", EFF);
		System.out.printf("%-" + off + "s%.2f\n", "Halstead Bugs:", BUG);
		System.out.printf("%-" + off + "s%d\n", "Total Depth of Nesting:", totalDepth);
		System.out.printf("%-" + off + "s%d\n", "Number of Casts:", casts);
		System.out.printf("%-" + off + "s%d\n", "Number of Loops:", numLoops);
		System.out.printf("%-" + off + "s%d\n", "Number of Operators:", countOperators());
		System.out.printf("%-" + off + "s%d\n", "Number of Operands:", countOperands());
		int i = 0;
		System.out.printf("%-" + off + "s%d\n", "Classes Referened:", classRef.size());
		for (String cRef : classRef.keySet()) {
			System.out.printf("%-" + off + "s%s\n", "[" + i++ + "]", cRef);
		}
		i = 0;
		System.out.printf("%-" + off + "s%d\n", "External Methods:", externalMethods.size());
		for (String external : externalMethods.keySet()) {
			System.out.printf("%-" + off + "s%s\n", "[" + i++ + "]", external);
		}
		System.out.printf("%-" + off + "s%d\n", "Local Methods:", localMethods.size());
		i = 0;
		for (String local : localMethods.keySet()) {
			System.out.printf("%-" + off + "s%s\n", "[" + i++ + "]", local);
		}
		System.out.printf("%-" + off + "s%d\n", "Exceptions Referenced:", exceptionRef.size());
		i = 0;
		for (String exception : exceptionRef.keySet()) {
			System.out.printf("%-" + off + "s%s\n", "[" + i++ + "]", exception);
		}
		System.out.printf("%-" + off + "s%d\n", "Thrown Exceptions:", thrownExceptions.size());
		for (i = 0; i < thrownExceptions.size(); i++) {
			System.out.printf("%-" + off + "s%s\n", "[" + i + "]", thrownExceptions.get(i));
		}
		System.out.printf("%-" + off + "s%s\n", "Modifiers:", modifiers);
		System.out.printf("%-" + off + "s%d\n", "Number of Lines:", numLines);
		printX(off * 2, '=');
	}

	public String prepForCSV() {
		String ret = "";
		ret += name + ",";
		ret += cycoComplex + ",";
		ret += numArgs + ",";
		ret += varDec.size() + ",";
		ret += varRefs.size() + ",";
		ret += numStatments + ",";
		ret += maxDepth + ",";
		ret += LTH + ",";
		ret += VOC + ",";
		ret += DIF + ",";
		ret += VOL + ",";
		ret += EFF + ",";
		ret += BUG + ",";
		ret += totalDepth + ",";
		ret += casts + ",";
		ret += numLoops + ",";
		ret += countOperators() + ",";
		ret += countOperands() + ",";
		String sep = " | ";
		for (String cRef : classRef.keySet()) {
			ret += cRef + sep;
		}
		if (!classRef.isEmpty())
			ret = ret.substring(0, ret.length() - sep.length());
		ret += ",";
		for (String external : externalMethods.keySet()) {
			ret += external + sep;
		}
		if (!externalMethods.isEmpty())
			ret = ret.substring(0, ret.length() - sep.length());
		ret += ",";
		for (String local : localMethods.keySet()) {
			ret += local + sep;
		}
		if (!localMethods.isEmpty())
			ret = ret.substring(0, ret.length() - sep.length());
		ret += ",";
		for (String exception : exceptionRef.keySet()) {
			ret += exception + sep;
		}
		if (!exceptionRef.isEmpty())
			ret = ret.substring(0, ret.length() - sep.length());
		ret += ",";
		for (int i = 0; i < thrownExceptions.size(); i++) {
			ret += thrownExceptions.get(i) + sep;
		}
		if (!thrownExceptions.isEmpty())
			ret = ret.substring(0, ret.length() - sep.length());
		ret += ",";
		ret += modifiers + ",";
		ret += numLines + "\n";

		return ret;
	}
}
