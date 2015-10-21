package com.yin.spell;

import java.util.ArrayList;
import java.util.HashMap;

import com.yin.spellchecker.lib.SpellChecker;

/**
 * 真词纠错
 * 
 * @author yinchuandong
 *
 */
public class RealWordCorrect {

	private CorpusUtil corpusUtil;

	/**
	 * 初始概率
	 */
	private HashMap<String, Double> initProbMap;
	/**
	 * 转移概率
	 */
	private HashMap<String, Double> tranProbMap;

	private static double MIN_PROB = 0.0000000000000001;

	private String[] words;
	private Node[][] matrix;
	private Node maxFinalNode = null;
	
	//结果句子记录矩阵
	/** 未纠错的句子矩阵 */
	String[][] oldWordMatrix = null;
	/** 纠错之后的句子矩阵 */
	String[][] newWordMatrix = null;
	/** 句子对应的标点符号 */
	String[] punctArr = null;

	public RealWordCorrect(CorpusUtil corpusUtil) {
		this.corpusUtil = corpusUtil;
		this.initProbMap = corpusUtil.getInitProbMap();
		this.tranProbMap = corpusUtil.getTranProbMap();
	}

	/**
	 * 初始化句子，每次只传入一句
	 * @param words
	 */
	public void init(String[] words) {
		this.words = words;
		this.maxFinalNode = null;
		this.matrix = new Node[words.length][];
		this.initMatrix();
	}

	private void initMatrix() {
		for (int i = 0; i < words.length; i++) {
			words[i] = words[i].toLowerCase();
			ArrayList<Node> candidateList = corpusUtil.getCandidateList(words[i]);
			matrix[i] = new Node[candidateList.size()];
			for (int j = 0; j < candidateList.size(); j++) {
				Node node = candidateList.get(j).clone(); // 一定要加clone，否则无法display
				node.prob = 0.0;
				matrix[i][j] = node;
			}
		}

		// 创建初始维特比概率
		for (int j = 0; j < matrix[0].length; j++) {
			Node node = matrix[0][j];
			double initProb = initProbMap.containsKey(node.word) ? initProbMap.get(node.word) : MIN_PROB;
//			String emitKey = words[0] + "|" + node.word;
//			double emitProb = emitProbMap.containsKey(emitKey) ? emitProbMap.get(emitKey) : MIN_PROB;
//			node.prob = initProb * emitProb;
			node.prob = initProb;
		}
	}

	/**
	 * 采用二元语法计算
	 */
	private void runBigram() {
		
		for (int t = 1; t < words.length; t++) {
			Node[] curNodes = matrix[t];
			Node[] preNodes = matrix[t - 1];
			for (int i = 0; i < curNodes.length; i++) {
				Node maxPreNode = preNodes[0];
				double maxProb = Integer.MIN_VALUE;

				for (int j = 0; j < preNodes.length; j++) {
					double preProb = MIN_PROB;
					String tranKey = preNodes[j].word + "|" + curNodes[i].word;
					if (tranProbMap.containsKey(tranKey)) {
						preProb = tranProbMap.get(tranKey);
					}
					double tmpProb = matrix[t - 1][j].prob * preProb;
					if (tmpProb > maxProb) {
						maxPreNode = preNodes[j];
						maxProb = tmpProb;
					}
				}

//				String emitKey = words[t] + "|" + curNodes[i].word;
//				double emitProb = emitProbMap.containsKey(emitKey) ? emitProbMap.get(emitKey) : MIN_PROB;
//				maxProb = maxProb * emitProb;
				curNodes[i].prob = maxProb;
				curNodes[i].preNode = maxPreNode;
			}
		}

		//find the maximum node in the last column
		Node[] finalRow = matrix[matrix.length - 1];
		for (int k = 0; k < finalRow.length; k++) {
			if (maxFinalNode == null || finalRow[k].prob > maxFinalNode.prob) {
				maxFinalNode = finalRow[k];
			}
		}

	}
	
	private String[] decode(){
		String[] result = new String[words.length];
		Node tmpNode = maxFinalNode;
		int i = words.length - 1;
		while (tmpNode != null) {
			result[i--] = tmpNode.word;
			tmpNode = tmpNode.preNode;
		}
		return result;
	}

	public void run(String article) {
		if (!article.endsWith(".")) {
			article += ".";
		}
		article = article.replaceAll("\\s+", " ");
		String[] oldSentences = article.split("[,.;]");
		
		oldWordMatrix = new String[oldSentences.length][]; 
		newWordMatrix = new String[oldSentences.length][];
		punctArr = new String[oldSentences.length];

		int offset = 0;
		for (int i = 0; i < oldSentences.length; i++) {
			//获取每一句对应的标点符号
			offset += oldSentences[i].length() + 1;
			punctArr[i] = article.substring(offset - 1, offset);
			
			String oldSent = oldSentences[i].trim();
			oldWordMatrix[i] = oldSent.split("\\s");
			init(oldWordMatrix[i]);
			runBigram();
			newWordMatrix[i] = decode();
			
			for (int j = 0; j < newWordMatrix[i].length; j++) {
				if (i == 0) {
					newWordMatrix[i][0] = CorpusUtil.toUpperCaseFirstChar(newWordMatrix[i][0]);
				}
				if (i > 0 && (punctArr[i - 1].equals(".") || punctArr[i - 1].equals(";"))) {
					newWordMatrix[i][0] = CorpusUtil.toUpperCaseFirstChar(newWordMatrix[i][0]);
				}
				if (newWordMatrix[i][j].equals("i")) {
					newWordMatrix[i][j] = newWordMatrix[i][j].toUpperCase();
				}
			}
		}
		display();
		System.out.println("----------end run------");
	}
	

	public void display() {
		System.out.println();
		System.out.println("纠错句子：");
		for (int i = 0; i < newWordMatrix.length; i++) {
			for (int j = 0; j < newWordMatrix[i].length; j++) {
				System.out.print(newWordMatrix[i][j] + " ");
			}
			System.out.println(punctArr[i]);
		}
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		System.out.println("start:");
		/*
		 * you is a boy 用来测试emit prob
		 * he do love you
		 * you parent do love you
		 * there is lots of apple which I like
		 */
		String sentence = "";
		sentence += "he am a boys,I has a apples. you is a boy.";
		sentence += "there is lots of appe whih I like.";
		sentence += "he do love you, She done love you.";
		sentence += "my name as John.";
		sentence += "I want too eat pizza this afternoon among my parent.";
		sentence += "the weather is good today.";
		sentence += "His favourite sports is basketball.";
		sentence += "he like making faces or telling jokes.";
		sentence += "he have play tuis thing.";
		System.out.println("原始句子：" + sentence);
		
		SpellChecker checker = new SpellChecker();
		RealWordCorrect model = new RealWordCorrect(CorpusUtil.getInstance(checker.init()));
		model.run(sentence);
		long end = System.currentTimeMillis();
		System.out.println("end;  delay: " + (end - start));
	}

}
