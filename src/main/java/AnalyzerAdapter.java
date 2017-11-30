import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;

public class AnalyzerAdapter extends Analyzer{

	MethodMetrics metrics;
	Graph cfg;

	public AnalyzerAdapter(Interpreter arg0, MethodMetrics metrics) {
		super(arg0);
		cfg = new Graph();
		this.metrics = metrics;
	}
	
	@Override
	public Frame[] analyze(String arg0, MethodNode arg1) throws AnalyzerException {
		Frame[] frames = super.analyze(arg0, arg1);
		cfg.calculateDoms();
		cfg.naturalLoops();
		cfg.combNatLoops();
		return frames;
	}

	@Override
	protected boolean newControlFlowExceptionEdge(int insn, TryCatchBlockNode tcbn) {
		metrics.excRef(tcbn.type);
		return super.newControlFlowExceptionEdge(insn, tcbn);
	}
	
	@Override
	protected boolean newControlFlowExceptionEdge(int insn, int successor) {
		cfg.addEdge(insn, successor);
		return super.newControlFlowExceptionEdge(insn, successor);
	}

	@Override
	protected void newControlFlowEdge(int insn, int successor) {
		super.newControlFlowEdge(insn, successor);
		cfg.addEdge(insn, successor);
	}
	
	public int getCyclomaticComplexity(){
		return cfg.edges.size() - cfg.nodes.size() + 2;
	}
	
	public class Graph{
		ArrayList<Node> nodes;
		ArrayList<Edge> edges;
		ArrayList<Loop> loops;
		
		Graph(){
			nodes = new ArrayList<Node>();
			edges = new ArrayList<Edge>();
			loops = new ArrayList<Loop>();
		}
		
		public void calculateDoms(){
			if (nodes.isEmpty()){
				return;
			}
			Node start = nodes.get(0);
			start.dom.add(start);
			Queue<Node> q = new LinkedList<Node>();
			for(int i = 1; i < nodes.size(); i++){
				Node n = nodes.get(i);
				for(int j = 0; j < nodes.size(); j++){
					n.dom.add(nodes.get(j));
				}
				q.add(n);
			}
			while(!q.isEmpty()){
				Node n = q.poll();
				ArrayList<Node> preds = getPreds(n);
				
				Set<Node> newDom = new HashSet<Node>();
				if(!preds.isEmpty()){
					newDom.addAll(preds.get(0).dom);
					for(int i = 1; i < preds.size(); i++){
						newDom.retainAll(preds.get(i).dom);
					}
				}
				newDom.add(n);
				if(!domEq(n.dom, newDom)){
					n.dom = newDom;
					ArrayList<Node> succs = getSuccs(n);
					for(int i = 0; i < succs.size(); i++){
						Node s = succs.get(i);
						if(!q.contains(s) && !s.equals(start)){
							q.add(s);
						}
					}
				}
			}
		}
		
		public void combNatLoops(){
			for(int i = 0; i < loops.size(); i++){
				for(int j = i + 1; j < loops.size(); j++){
					if(loops.get(i).header.equals(loops.get(j).header)){
						loops.get(i).body.addAll(loops.get(j).body);
						loops.remove(j);
						j--;
					}
				}
			}
		}
		
		public int maxDepthNesting(){
			int maxDepth = 0;
			for(int i = 0; i < loops.size(); i++){
				int depth = NestingDepth(i);
				maxDepth = (depth > maxDepth) ? depth : maxDepth;
			}
			return maxDepth;
		}
		
		public int totalDepthNesting(){
			int totalDepth = 0;
			for(int i = 0; i < loops.size(); i++){
				totalDepth = isLeaf(i) ? (totalDepth + NestingDepth(i)) : totalDepth;
			}
			return totalDepth;
		}
		
		public boolean isLeaf(int i){
			for(int j = 0; j < loops.size(); j++){
				if(i != j){
					Loop inner = loops.get(i);
					Loop outer = loops.get(j);
					if(inner.body.containsAll(outer.body)){
						return false;
					}
				}
			}
			return true;
		}
		
		public int NestingDepth(int i){
			int maxDepth = 0;
			for(int j = 0; j < loops.size(); j++){
				int depth = 1;
				if(i != j){
					Loop inner = loops.get(i);
					Loop outer = loops.get(j);
					if(outer.body.containsAll(inner.body)){
						depth += NestingDepth(j);
					}
				}
				maxDepth = (depth > maxDepth) ? depth : maxDepth;
			}
			return maxDepth;
		}
		
		public int numLoops(){
			return loops.size();
		}
		
		public void naturalLoops(){
			for (Node h: nodes){
				for(Node n: nodes){
					boolean backEdge = false;
					for(Edge e: edges){
						if(e.start.equals(n) && e.end.equals(h) && n.dom.contains(h)){
							backEdge = true;
						}
					}
					if(backEdge){
						Loop loop = new Loop(h);
						Stack<Node> stack = new Stack<Node>();
						stack.push(n);
						while(!stack.isEmpty()){
							Node d = stack.pop();
							if(!loop.contains(d)){
								loop.addNode(d);
								ArrayList<Node> preds = getPreds(d);
								for(Node p: preds){
									stack.push(p);
								}
							}
						}
						loops.add(loop);
					}
				}
			}
		}
		
		public boolean domEq(Set<Node> d1, Set<Node> d2){
			for(Node s: d1){
				if(!d2.contains(s)){
					return false;
				}
			}
			return true;
		}
		
		public void printDom(Set<Node> n){
			System.out.print("{");
			for(Node d: n){
				System.out.print(d.insn + ",");
			}
			System.out.println("}");
		}
		
		public void printDoms(){
			for(Node n: nodes){
				System.out.print(n.insn);
				printDom(n.dom);
			}
		}
		
		public ArrayList<Node> getPreds(Node n){
			ArrayList<Node> preds = new ArrayList<Node>();
			for(Edge e : edges){
				Node end = e.end;
				if(end.equals(n)){
					preds.add(e.start);
				}
			}
			return preds;
		}
		
		public ArrayList<Node> getSuccs(Node n){
			ArrayList<Node> succs = new ArrayList<Node>();
			for(Edge e : edges){
				Node start = e.start;
				if(n.insn == start.insn){
					succs.add(e.end);
				}
			}
			return succs;
		}
		
		public String toString(){
			String s = "Nodes\n";
			for(int i = 0; i < nodes.size(); i++){
				s += "\t" + nodes.get(i).toString() + "\n";
			}
			s += "Edges\n";
			for(int i = 0; i < edges.size(); i++){
				s += "\t" + edges.get(i).toString() + "\n";
			}
			return s;
		}
		
		public void addEdge(int insn, int successor){
			Node start = addNode(insn);
			Node end = addNode(successor);
			Edge e = new Edge(start, end);
			edges.add(e);
		}
		
		public Node getNode(int insn){
			for(Node n : nodes){
				if(n.insn == insn){
					return n;
				}
			}
			return null;
		}
		
		public Node addNode(int insn){
			Node n = getNode(insn);
			if(n == null){
				n = new Node(insn);
				nodes.add(n);
			}
			return n;
		}
		
		public class Loop{
			Node header;
			Set<Node> body;
			public Loop(Node h){
				header = h;
				body = new HashSet<Node>();
				body.add(h);
			}
			
			public void addNode(Node n){
				body.add(n);
			}
			
			public boolean contains(Node n){
				return body.contains(n);
			}
		}
		
		public class Edge{
			Node start;
			Node end;
			
			Edge(Node start, Node end){
				this.start = start; this.end = end;
			}
			
			public String toString(){
				return start.toString() + " " + end.toString();
			}
		}
		
		public class Node{
			int insn;
			Set<Node> dom;
			
			Node(int insn){
				this.insn = insn;
				dom = new HashSet<Node>();
			}
			
			public String toString(){
				return insn + "";
			}
			
			public boolean equals(Node n){
				return insn == n.insn;
			}
			
		}
	}

}
