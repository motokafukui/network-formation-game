package trial.preparation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import basic.preparation.*;

public class LinkOptimizer_9 {

	static double sigma = 0.9; // ������
	static double linkedCost = 0.1; // ���ڃ����N����R�X�g

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NetworkGenerator generator = new NetworkGenerator();

		LinkOptimizer_9 lo = new LinkOptimizer_9();
		List<Agent> agentList = new ArrayList<>();

		int allLink = 4;
		int counter = 20; // 1NW������̌J��Ԃ���

		for (int i = 0; i < 100; i++) { // i�̏���ŃG�[�W�F���g���ύX
			agentList.add(new Agent());
		}
		for (int p = 0; p < 5; p++) {
			System.out.println(p + "���");
			for (Agent agent : agentList) {
				agent.setInfoValue(lo.setInfoValue(agent.getInfoValue())); // �e�G�[�W�F���g�̎����̉��l��ݒ�

				// System.out.println(agent.infoValue);
			}

			// generator.createSmallWorldConstLinkNum(agentList, allLink, 0);
			// generator.createCnnModel(agentList, 4);
			// generator.createBarabasiModel(agentList, 4, 2);

			generator.createTwoWaySmallWorldConstLinkNum(agentList, allLink, 0.0, 0.0, 0.05); // �S�����N���A����ւ����A�����N�̃����_�����A�����N�̒P������
			// generator.createRegularNetwork(agentList, allLink);
			// generator.createRandomNetwork(agentList, allLink);

			for (int j = 0; j < agentList.size(); j++) {
				System.out.println("linkpair_start");
				try {
					Writer writer = new FileWriter("140228_linkpair.txt", true);
					BufferedWriter bw = new BufferedWriter(writer);

					bw.write(agentList.get(j).getId() + "\t\t" + agentList.get(j).getLinkedNodeSet().size() + "\n");

					bw.close();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				for (Node linked : agentList.get(j).getLinkedNodeSet()) {
					Agent linkedA = (Agent) linked;
					try {
						Writer writer = new FileWriter("140228_linkpair.txt", true);
						BufferedWriter bw = new BufferedWriter(writer);

						bw.write(agentList.get(j).getId() + "\t" + linkedA.getId() + "\n");

						bw.close();
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			try {
				Writer writer = new FileWriter("140228_linkpair.txt", true);
				BufferedWriter bw = new BufferedWriter(writer);

				bw.newLine();

				bw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				Writer writer = new FileWriter("140228_newLink.txt", true);
				BufferedWriter bw = new BufferedWriter(writer);
				bw.newLine();
				bw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			int agentNum = agentList.size();
			double[] expectationAry = new double[agentNum];
			int[][] countedDistanceAry = calcCountedDistanceAry(agentNum, agentList);
			double firstNWExpectation = 0.0;
			// �l�b�g���[�N�S�̂̓��_�Z�o
			for (int q = 0; q < agentList.size(); q++) {
				firstNWExpectation += calcMyExpectFirst(agentList.get(q), agentList, countedDistanceAry);
			}
			// System.out.println("����NW�̓��_�F" + firstNWExpectation);
			SimpleNetwork<Agent, Link> network = new SimpleNetwork<Agent, Link>(agentList);
			double cv = network.getClusterValue();
			double path = network.getAveragePathLength();

			double ass = network.getAssortativity();
			double pow = network.getDegreeDistributionMap().getPowerIndex();
			double link = network.getDegreeDistributionMap().getAverageDegree();
			// System.out.printf("%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t\n", cv, path,
			// ass, pow, link);

			// ����������Ғl[�����͗v��]
			try {
				Writer writer = new FileWriter("140228_nwExpectation.txt", true);
				BufferedWriter bw = new BufferedWriter(writer);

				bw.write(firstNWExpectation + "\t" + cv + "\t" + path + "\t" + ass + "\t" + pow + "\t" + link + "\n");

				bw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

//			try {
//				Writer writer = new FileWriter("140228_nwExpectation_random.txt", true);
//				BufferedWriter bw = new BufferedWriter(writer);
//
//				bw.write(firstNWExpectation + "\t" + cv + "\t" + path + "\t" + ass + "\t" + pow + "\t" + link + "\n");
//
//				bw.close();
//				writer.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			// �����܂Ŋ��Ғl

			// chooseNewLinkSimple(agentNum, agentList, expectationAry,
			// counter);
			chooseNewLinkCompare(agentNum, agentList, expectationAry, counter);
			// chooseNewLinkOmni(agentNum, agentList, expectationAry, counter);
//			chooseNewLinkRandom(agentNum, agentList, counter);
		}
		System.out.println("finish");
	}

	private final Random rand;

	public LinkOptimizer_9(Random rand) {
		this.rand = rand;
	}

	public LinkOptimizer_9() {
		this.rand = new Random();
	}

	private double setInfoValue(double infoValue) {
		do {
			infoValue = rand.nextGaussian();
		} while (infoValue < 0);
		infoValue = 10 * infoValue;
		return infoValue;
	}

	/*
	 * 2�m�[�h�Ԃ̃X�e�b�v�������߂�
	 */
	public static int[][] calcCountedDistanceAry(int agentNum, List<Agent> agentList) {

		// 2�G�[�W�F���g�Ԃ̌o�H���̏�����
		int[][] distanceArray = new int[agentNum][agentNum];
		for (Agent agent : agentList) {
			for (Agent target : agentList) {
				if (agent.isLinkTo(target)) {
					distanceArray[agent.getId()][target.getId()] = 1;
				} else if (target == agent) {
					distanceArray[agent.getId()][target.getId()] = 0;
				} else {
					distanceArray[agent.getId()][target.getId()] = 10000;
				}
			}
		}

		// �t���C�h�̃A���S���Y���ɂ��ŒZ�o�H���Z�o
		int[][] countedDistanceAry = new int[agentNum][agentNum];
		countedDistanceAry = floydAlgo(agentList, distanceArray);

		return countedDistanceAry;

	}

	/*
	 * �t���C�h�̃A���S���Y����p���čŒZ�o�H�������߂� calcCountedDistanceAry����Ăяo��
	 */
	public static int[][] floydAlgo(List<Agent> agentList, int[][] distanceArray) {
		int agentNum = agentList.size();
		for (int k = 0; k < agentNum; k++) {
			for (int i = 0; i < agentNum; i++) {
				for (int j = 0; j < agentNum; j++) {
					// k��ڂ̃X�e�b�v�ɂ�����A�m�[�hi����m�[�hk�܂ł̋����i�b��j
					int distance_i_k_j = distanceArray[i][k] + distanceArray[k][j];

					if (distance_i_k_j < distanceArray[i][j]) {
						distanceArray[i][j] = distance_i_k_j;
					}
				}
			}
		}
		return distanceArray;
	}

	/*
	 * ����m�[�h����̏��̊��Ғl�i���ڃ����N�R�X�g���܂܂Ȃ��j���v�Z����
	 */
	public static double setPassedInfoValue(Agent receiver, int k, Agent sender, int distance) {
		double passedInfoExpect = 0.0;
		double delta = 0.0;
		if (k == 1) {
			delta = sigma;
		} else {
			delta = sigma / (Math.log(k));
		}

		if (distance == 1) {
			passedInfoExpect = sender.getInfoValue() * delta;
		} else if (distance != 0) {
			passedInfoExpect = sender.getInfoValue() * (Math.pow(delta, distance));
		}
		return passedInfoExpect;
	}

	public static double calcMyExpectFirst(Agent receiver, List<Agent> agentList, int[][] countedDistanceAry) {
		int agentNum = agentList.size();
		double[] scoreAry = new double[agentNum];
		double expectation = 0.0;

		for (Agent sender : agentList) {
			double a = setPassedInfoValue(receiver, receiver.getLinkedNodeSet().size(), sender,
					countedDistanceAry[receiver.getId()][sender.getId()]);
//			try {
//				Writer writer = new FileWriter("140228_each.txt", true);
//				BufferedWriter bw = new BufferedWriter(writer);
//
//				bw.write(receiver.getId() + "\t" + sender.getId() + "\t" + a + "\n");
//
//				bw.close();
//				writer.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			scoreAry[sender.getId()] += a;
			expectation += scoreAry[sender.getId()];
		}
		int linkedNum = receiver.getLinkedNodeSet().size();
		expectation -= linkedCost * linkedNum;
		try {
			Writer writer = new FileWriter("140228_Expectation.txt", true);
			BufferedWriter bw = new BufferedWriter(writer);

			bw.write(expectation + "\t" + linkedNum + "\n");

			bw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

//		try {
//			Writer writer = new FileWriter("140228_Expectation_random.txt", true);
//			BufferedWriter bw = new BufferedWriter(writer);
//
//			bw.write(expectation + "\t" + linkedNum + "\n");
//
//			bw.close();
//			writer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		return expectation;
	}

	/*
	 * NW�S�̂̊��Ғl���v�Z����
	 */
	public static double calcNWExpect(List<Agent> agentList, double[] expectation) {
		double nwScore = 0.0;
		for (Agent agent : agentList) {
			nwScore += expectation[agent.getId()];
		}
		return nwScore;
	}

	/*
	 * ����m�[�h�̓�������Ғl�̘a
	 */
	public static double calcMyExpectNoWrite(Agent receiver, List<Agent> agentList, int[][] countedDistanceAry) {
		int agentNum = agentList.size();
		double[] scoreAry = new double[agentNum];
		double expectation = 0.0;

		for (Agent sender : agentList) {
			double a = setPassedInfoValue(receiver, receiver.getLinkedNodeSet().size(), sender,
					countedDistanceAry[receiver.getId()][sender.getId()]);
			// try {
			// Writer writer = new FileWriter("140228_each.txt", true);
			// BufferedWriter bw = new BufferedWriter(writer);
			//
			// bw.write(receiver.getId() + "\t" + sender.getId() + "\t" + a +
			// "\n");
			//
			// bw.close();
			// writer.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			scoreAry[sender.getId()] += a;
			expectation += scoreAry[sender.getId()];
		}
		int linkedNum = receiver.getLinkedNodeSet().size();
		expectation -= linkedCost * linkedNum;
		return expectation;
	}

	public static double calcMyExpect(Agent receiver, List<Agent> agentList, int[][] countedDistanceAry) {
		int agentNum = agentList.size();
		double[] scoreAry = new double[agentNum];
		double expectation = 0.0;

		for (Agent sender : agentList) {
			double a = setPassedInfoValue(receiver, receiver.getLinkedNodeSet().size(), sender,
					countedDistanceAry[receiver.getId()][sender.getId()]);
//			try {
//				Writer writer = new FileWriter("140228_each.txt", true);
//				BufferedWriter bw = new BufferedWriter(writer);
//
//				bw.write(receiver.getId() + "\t" + sender.getId() + "\t" + a + "\n");
//
//				bw.close();
//				writer.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			scoreAry[sender.getId()] += a;
			expectation += scoreAry[sender.getId()];
		}
		int linkedNum = receiver.getLinkedNodeSet().size();
		expectation -= linkedCost * linkedNum;
		try {
			Writer writer = new FileWriter("140228_Expectation.txt", true);
			BufferedWriter bw = new BufferedWriter(writer);

			bw.write(expectation + "\t" + linkedNum + "\n");

			bw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return expectation;
	}

	public static double calcMyExpectRandom(Agent receiver, List<Agent> agentList, int[][] countedDistanceAry) {
		int agentNum = agentList.size();
		double[] scoreAry = new double[agentNum];
		double expectation = 0.0;

		for (Agent sender : agentList) {
			double a = setPassedInfoValue(receiver, receiver.getLinkedNodeSet().size(), sender,
					countedDistanceAry[receiver.getId()][sender.getId()]);
//			try {
//				Writer writer = new FileWriter("140228_each.txt", true);
//				BufferedWriter bw = new BufferedWriter(writer);
//
//				bw.write(receiver.getId() + "\t" + sender.getId() + "\t" + a + "\n");
//
//				bw.close();
//				writer.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			scoreAry[sender.getId()] += a;
			expectation += scoreAry[sender.getId()];
		}
		int linkedNum = receiver.getLinkedNodeSet().size();
		expectation -= linkedCost * linkedNum;
		try {
			Writer writer = new FileWriter("140228_Expectation_random.txt", true);
			BufferedWriter bw = new BufferedWriter(writer);

			bw.write(expectation + "\t" + linkedNum + "\n");

			bw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return expectation;
	}

	/*
	 * ����m�[�h����̊��Ғl���v�Z����
	 */
	public static double calcOneToOneExpect(Agent receiver, Agent sender, Set<? extends Node> nodeSet,
			int[][] countedDistanceAry) {
		int k = receiver.getLinkedNodeSet().size();
		double nodeExpect = setPassedInfoValue(receiver, receiver.getLinkedNodeSet().size(), sender,
				countedDistanceAry[receiver.getId()][sender.getId()]) - linkedCost * k;
		return nodeExpect;
	}

	public static List<Agent> makeCopyList(List<Agent> agentList) {
		List<Agent> copyList = new ArrayList<>();
		for (int k = 0; k < agentList.size(); k++) {
			Agent copyAgent = new Agent();

			int copyId = agentList.get(k).getId();
			double copyInfoValue = agentList.get(k).getInfoValue();

			copyList.add(copyAgent);
			copyList.get(k).setId(copyId);
			copyList.get(k).setInfoValue(copyInfoValue);
		}
		for (int j = 0; j < copyList.size(); j++) {
			Set<Agent> copyLinkedAgentSet = new HashSet<>();
			for (Node copyLinkedNode : agentList.get(j).getLinkedNodeSet()) {
				Agent copyLinkedAgent = (Agent) copyLinkedNode;
				int copyLinkedAgentId = copyLinkedAgent.getId();
				copyLinkedAgentSet.add(copyList.get(copyLinkedAgentId));
			}
			for (Agent copyLinkedAgent : copyLinkedAgentSet) {
				copyList.get(j).addLink(copyLinkedAgent);
			}
		}
		return copyList;
	}

	/*
	 * ������2�X�e�b�v�Ń����N���Ă���m�[�h�̃Z�b�g���쐬
	 */
	public static Set<Agent> makeTwostepAgentSet(Agent receiver) {
		// ������1�X�e�b�v�Ń����N���Ă���m�[�h�̃n�b�V���}�b�v���쐬
		HashMap<Integer, Agent> linkedAgentMap = new HashMap<Integer, Agent>();
		for (Node node : receiver.getLinkedNodeSet()) {
			Agent neighbor = (Agent) node;
			linkedAgentMap.put(neighbor.getId(), neighbor);
		}

		// ������2�X�e�b�v�Ń����N���Ă���m�[�h�̃Z�b�g���쐬
		Set<Agent> twostepAgentSet = new LinkedHashSet<>();
		for (Integer id : linkedAgentMap.keySet()) {
			Agent neighbor = linkedAgentMap.get(id);
			Set<Agent> targetNodeSet = new HashSet<>();
			for (Node hisLinked : neighbor.getLinkedNodeSet()) {
				Agent linked = (Agent) hisLinked;
				targetNodeSet.add(linked);
			}
			for (Node nextNode : targetNodeSet) {
				Agent target = (Agent) nextNode;
				if (target.getId() != receiver.getId() && !linkedAgentMap.containsKey(target.getId())) {
					twostepAgentSet.add(target);
				}
			}
		}
		return twostepAgentSet;
	}

	public static void chooseNewLinkOmni(int agentNum, List<Agent> agentList, double[] expectationAry, int maxLinkNum) {

		List<Agent> copyList = makeCopyList(agentList);

		for (int p = 0; p < maxLinkNum; p++) {

			int[][] preCountedDistanceAry = calcCountedDistanceAry(agentNum, copyList);
			double[] lastExpectAry = new double[agentNum];

			List<Integer> nextLinkList = new ArrayList<>(); // get(i)�ŁAid��i�����G�[�W�F���g�����Ƀ����N���쐬���ׂ��G�[�W�F���g��id�𓾂���
			// �e�G�[�W�F���g�����Ƀ����N���쐬���ׂ��G�[�W�F���g�����肷��
			for (int i = 0; i < copyList.size(); i++) {
				lastExpectAry[i] = calcMyExpectNoWrite(copyList.get(i), copyList, preCountedDistanceAry);

				Set<Agent> twostepAgentSet = makeTwostepAgentSet(copyList.get(i));
				// 2�X�e�b�v��̃m�[�h�̂����A���ōł����Ғl�̍������̂����߂�
				int[][] currentDistanceAry = calcCountedDistanceAry(agentNum, copyList); // �m�[�h���m�̋���������s��
				double preMax = -100.0; // Max�̊��Ғl�����邽�߂̏�����
				int preId = 10000; // Max�̊��Ғl�𓾂���G�[�W�F���g��Id�����邽�߂̏�����
				for (Agent sender : twostepAgentSet) {
					double eachExpect = setPassedInfoValue(copyList.get(i), copyList.get(i).getLinkedNodeSet().size(),
							sender, currentDistanceAry[copyList.get(i).getId()][sender.getId()]);
					// System.out.println("sender:" + sender.getId() + "score:"
					// + eachExpect);
					if (preMax < eachExpect) {
						preMax = eachExpect;
						preId = sender.getId();
					}
				}

				if (preMax <= 0.0) {
					// System.out.println("���Ғl����");
					nextLinkList.add(10000);
				} else {
					nextLinkList.add(preId);
				}
			}

			// �S�G�[�W�F���g��1�������N��ǉ�
			for (int i = 0; i < copyList.size(); i++) {
				double testMyExpect = 0.0;

				if (nextLinkList.get(i) < copyList.size()) {

					List<Agent> dammyList = makeCopyList(agentList);
					Agent receiver = dammyList.get(i);
					Agent candidate = dammyList.get(nextLinkList.get(i));
					receiver.addLink(candidate);
					int[][] testCountedDistanceAry = calcCountedDistanceAry(agentNum, dammyList);
					testMyExpect = calcMyExpectNoWrite(receiver, dammyList, testCountedDistanceAry);
					if (testMyExpect > lastExpectAry[i]) {// �R�X�g��������Ғl�����̃m�[�h���瓾����ΐV�K�����N�쐬
						copyList.get(nextLinkList.get(i)).addLink(copyList.get(i));
					}
				}
				// try {
				// Writer writer = new FileWriter("140228_test.txt", true);
				// BufferedWriter bw = new BufferedWriter(writer);
				//
				// bw.write(lastExpectAry[i] + "\t" + testMyExpect + "\n");
				//
				// bw.close();
				// writer.close();
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
			}

			// �G�[�W�F���g���X�g���R�s�[���X�g�ɒu������
			agentList = makeCopyList(copyList);

			for (int i = 0; i < agentList.size(); i++) {
				// ��������V�K�����N[�����͗v��]
				// try {
				// Writer writer = new FileWriter("140228_newLink.txt", true);
				// BufferedWriter bw = new BufferedWriter(writer);
				//
				// bw.write(agentList.get(i).getId() + "\t" +
				// agentList.get(nextLinkList.get(i)).getId() + "\t"
				// + agentList.get(i).getLinkedNodeSet().size() + "\n");
				//
				// bw.close();
				// writer.close();
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				// �����܂ŐV�K�����N
			}
			// ��������V�K�����N[�����͗v��]
			// try {
			// Writer writer = new FileWriter("140228_newLink.txt", true);
			// BufferedWriter bw = new BufferedWriter(writer);
			// bw.newLine();
			// bw.close();
			// writer.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// �����܂ŐV�K�����N

			int[][] countedDistanceAry = calcCountedDistanceAry(agentNum, agentList);
			double trialExpectation = 0.0;
			// �l�b�g���[�N�S�̂̓��_�Z�o
			for (int q = 0; q < agentList.size(); q++) {
				trialExpectation += calcMyExpect(agentList.get(q), agentList, countedDistanceAry);
			}
			// System.out.println("score�F" + trialExpectation);

			SimpleNetwork<Agent, Link> network = new SimpleNetwork<Agent, Link>(agentList);
			double cv = network.getClusterValue();
			double path = network.getAveragePathLength();

			double ass = network.getAssortativity();
			double pow = network.getDegreeDistributionMap().getPowerIndex();
			double link = network.getDegreeDistributionMap().getAverageDegree();
			// System.out.printf("%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t\n", cv, path,
			// ass, pow, link);

			// ����������Ғl[�����͗v��]
			try {
				Writer writer = new FileWriter("140228_nwExpectation.txt", true);
				BufferedWriter bw = new BufferedWriter(writer);

				bw.write(trialExpectation + "\t" + cv + "\t" + path + "\t" + ass + "\t" + pow + "\t" + link + "\n");

				bw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// �����܂Ŋ��Ғl
			if (p == (maxLinkNum - 1)) {
				// ��������V�K�����N[�����͗v��]
				// try {
				// Writer writer = new FileWriter("140228_newLink.txt", true);
				// BufferedWriter bw = new BufferedWriter(writer);
				// bw.write("finish\n");
				// bw.close();
				// writer.close();
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				// �����܂ŐV�K�����N
			}
		}

	}

	public static void chooseNewLinkCompare(int agentNum, List<Agent> agentList, double[] expectationAry, int maxLinkNum) {

		List<Agent> copyList = makeCopyList(agentList);

		for (int p = 0; p < maxLinkNum; p++) {

			int[][] preCountedDistanceAry = calcCountedDistanceAry(agentNum, copyList);
			double[] lastExpectAry = new double[agentNum];

			List<Integer> nextLinkList = new ArrayList<>(); // get(i)�ŁAid��i�����G�[�W�F���g�����Ƀ����N���쐬���ׂ��G�[�W�F���g��id�𓾂���
			List<Double> nextPlusList = new ArrayList<>();// ���Ƀ����N���쐬���ׂ��G�[�W�F���g���獡�񓾂�ꂽ���Ғl

			// �e�G�[�W�F���g�����Ƀ����N���쐬���ׂ��G�[�W�F���g�����肷��
			for (int i = 0; i < copyList.size(); i++) {
				lastExpectAry[i] = calcMyExpectNoWrite(copyList.get(i), copyList, preCountedDistanceAry);
				Set<Agent> twostepAgentSet = makeTwostepAgentSet(copyList.get(i));
				// 2�X�e�b�v��̃m�[�h�̂����A���ōł����Ғl�̍������̂����߂�
				int[][] currentDistanceAry = calcCountedDistanceAry(agentNum, copyList); // �m�[�h���m�̋���������s��
				double preMax = -100.0; // Max�̊��Ғl�����邽�߂̏�����
				int preId = 10000; // Max�̊��Ғl�𓾂���G�[�W�F���g��Id�����邽�߂̏�����
				for (Agent sender : twostepAgentSet) {
					double eachExpect = setPassedInfoValue(copyList.get(i), copyList.get(i).getLinkedNodeSet().size(),
							sender, currentDistanceAry[copyList.get(i).getId()][sender.getId()]);
					// System.out.println("sender:" + sender.getId() + "score:"
					// + eachExpect);
					if (preMax < eachExpect) {
						preMax = eachExpect;
						preId = sender.getId();
					}
				}

				if (preMax <= 0.0) {
					// System.out.println("���Ғl����");
					nextLinkList.add(10000);
					nextPlusList.add(0.0);
				} else {
					nextLinkList.add(preId);
					nextPlusList.add(preMax);
				}
			}

			// �S�G�[�W�F���g��1�������N��ǉ�
			for (int i = 0; i < copyList.size(); i++) {
				if (nextLinkList.get(i) < copyList.size()) {

					Agent receiver = copyList.get(i);
					int currentK = receiver.getLinkedNodeSet().size();
					Agent candidate = copyList.get(nextLinkList.get(i));
					double nextExpect = setPassedInfoValue(receiver, (currentK + 1), candidate, 1);// �����N�𑝂₵���ꍇ�̂��̃m�[�h���瓾������Ғl

					if ((nextExpect - linkedCost) > nextPlusList.get(i)) {// �O���������Ғl�����̃m�[�h���瓾����ΐV�K�����N�쐬
						receiver.addLink(candidate);
					}
				}
			}

			// �G�[�W�F���g���X�g���R�s�[���X�g�ɒu������
			agentList = makeCopyList(copyList);

			int[][] countedDistanceAry = calcCountedDistanceAry(agentNum, agentList);
			double trialExpectation = 0.0;
			// �l�b�g���[�N�S�̂̓��_�Z�o
			for (int q = 0; q < agentList.size(); q++) {
				trialExpectation += calcMyExpect(agentList.get(q), agentList, countedDistanceAry);
			}
			// System.out.println("score�F" + trialExpectation);

			SimpleNetwork<Agent, Link> network = new SimpleNetwork<Agent, Link>(agentList);
			double cv = network.getClusterValue();
			double path = network.getAveragePathLength();

			double ass = network.getAssortativity();
			double pow = network.getDegreeDistributionMap().getPowerIndex();
			double link = network.getDegreeDistributionMap().getAverageDegree();
			// System.out.printf("%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t\n", cv, path,
			// ass, pow, link);

			for (int i = 0; i < agentList.size(); i++) {
				if (nextLinkList.get(i) >= agentList.size()) {
					// ��������V�K�����N[�����͗v��]
					try {
						Writer writer = new FileWriter("140228_newLink.txt", true);
						BufferedWriter bw = new BufferedWriter(writer);

						bw.write(agentList.get(i).getId() + "\tN/O\t" + agentList.get(i).getLinkedNodeSet().size()
								+ "\n");

						bw.close();
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					// ��������V�K�����N[�����͗v��]
					try {
						Writer writer = new FileWriter("140228_newLink.txt", true);
						BufferedWriter bw = new BufferedWriter(writer);

						bw.write(agentList.get(i).getId() + "\t" + agentList.get(nextLinkList.get(i)).getId() + "\t"
								+ agentList.get(i).getLinkedNodeSet().size() + "\n");

						bw.close();
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				double testMyExpect = calcMyExpectNoWrite(agentList.get(i), agentList, countedDistanceAry);
				// �����܂ŐV�K�����N
				try {
					Writer writer = new FileWriter("140228_test.txt", true);
					BufferedWriter bw = new BufferedWriter(writer);

					bw.write(lastExpectAry[i] + "\t" + testMyExpect + "\n");

					bw.close();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// ��������V�K�����N[�����͗v��]
			try {
				Writer writer = new FileWriter("140228_newLink.txt", true);
				BufferedWriter bw = new BufferedWriter(writer);
				bw.newLine();
				bw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// �����܂ŐV�K�����N

			// ����������Ғl[�����͗v��]
			try {
				Writer writer = new FileWriter("140228_nwExpectation.txt", true);
				BufferedWriter bw = new BufferedWriter(writer);

				bw.write(trialExpectation + "\t" + cv + "\t" + path + "\t" + ass + "\t" + pow + "\t" + link + "\n");

				bw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// �����܂Ŋ��Ғl
			if (p == (maxLinkNum - 1)) {
				// ��������V�K�����N[�����͗v��]
				try {
					Writer writer = new FileWriter("140228_newLink.txt", true);
					BufferedWriter bw = new BufferedWriter(writer);
					bw.write("finish\n");
					bw.close();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// �����܂ŐV�K�����N
			}

		}
		try {
			Writer writer = new FileWriter("140228_Expectation.txt", true);
			BufferedWriter bw = new BufferedWriter(writer);

			bw.newLine();

			bw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void chooseNewLinkSimple(int agentNum, List<Agent> agentList, double[] expectationAry, int maxLinkNum) {

		List<Agent> copyList = makeCopyList(agentList);

		for (int p = 0; p < maxLinkNum; p++) {

			int[][] preCountedDistanceAry = calcCountedDistanceAry(agentNum, copyList);
			double[] lastExpectAry = new double[agentNum];

			List<Integer> nextLinkList = new ArrayList<>(); // get(i)�ŁAid��i�����G�[�W�F���g�����Ƀ����N���쐬���ׂ��G�[�W�F���g��id�𓾂���
			// �e�G�[�W�F���g�����Ƀ����N���쐬���ׂ��G�[�W�F���g�����肷��
			for (int i = 0; i < copyList.size(); i++) {
				lastExpectAry[i] = calcMyExpectNoWrite(copyList.get(i), copyList, preCountedDistanceAry);

				Set<Agent> twostepAgentSet = makeTwostepAgentSet(copyList.get(i));
				// 2�X�e�b�v��̃m�[�h�̂����A���ōł����Ғl�̍������̂����߂�
				int[][] currentDistanceAry = calcCountedDistanceAry(agentNum, copyList); // �m�[�h���m�̋���������s��
				double preMax = -100.0; // Max�̊��Ғl�����邽�߂̏�����
				int preId = 10000; // Max�̊��Ғl�𓾂���G�[�W�F���g��Id�����邽�߂̏�����
				for (Agent sender : twostepAgentSet) {
					double eachExpect = setPassedInfoValue(copyList.get(i), copyList.get(i).getLinkedNodeSet().size(),
							sender, currentDistanceAry[copyList.get(i).getId()][sender.getId()]);
					// System.out.println("sender:" + sender.getId() + "score:"
					// + eachExpect);
					if (preMax < eachExpect) {
						preMax = eachExpect;
						preId = sender.getId();
					}
				}

				if (preMax <= 0.0) {
					// System.out.println("���Ғl����");
					nextLinkList.add(10000);
				} else {
					nextLinkList.add(preId);
				}
			}

			// �S�G�[�W�F���g��1�������N��ǉ�
			for (int i = 0; i < copyList.size(); i++) {
				if (nextLinkList.get(i) < copyList.size()) {

					Agent receiver = copyList.get(i);
					int currentK = receiver.getLinkedNodeSet().size();
					Agent candidate = copyList.get(nextLinkList.get(i));
					double nextExpect = setPassedInfoValue(receiver, (currentK + 1), candidate, 1);// �����N�𑝂₵���ꍇ�̂��̃m�[�h���瓾������Ғl

					if (nextExpect > linkedCost) {// �R�X�g��������Ғl�����̃m�[�h���瓾����ΐV�K�����N�쐬
						// System.out.println("before linknum:receiver:"+receiver.getLinkedNodeSet().size()+"sender"+candidate.getLinkedNodeSet().size());
						receiver.addLink(candidate);
						// System.out.println("after linknum:receiver:"+receiver.getLinkedNodeSet().size()+"sender"+candidate.getLinkedNodeSet().size());
						// System.out.println("receiver isLinkTo sender:"+receiver.isLinkTo(candidate)+"sender isLinkTo receiver:"+candidate.isLinkTo(receiver));
					}
				}
			}

			// �G�[�W�F���g���X�g���R�s�[���X�g�ɒu������
			agentList = makeCopyList(copyList);

			int[][] countedDistanceAry = calcCountedDistanceAry(agentNum, agentList);
			double trialExpectation = 0.0;
			// �l�b�g���[�N�S�̂̓��_�Z�o
			for (int q = 0; q < agentList.size(); q++) {
				trialExpectation += calcMyExpect(agentList.get(q), agentList, countedDistanceAry);
			}
			// System.out.println("score�F" + trialExpectation);

			SimpleNetwork<Agent, Link> network = new SimpleNetwork<Agent, Link>(agentList);
			double cv = network.getClusterValue();
			double path = network.getAveragePathLength();

			double ass = network.getAssortativity();
			double pow = network.getDegreeDistributionMap().getPowerIndex();
			double link = network.getDegreeDistributionMap().getAverageDegree();
			// System.out.printf("%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t\n", cv, path,
			// ass, pow, link);
			for (int i = 0; i < agentList.size(); i++) {
				if (nextLinkList.get(i) > agentList.size()) {
					// ��������V�K�����N[�����͗v��]
					// try {
					// Writer writer = new FileWriter("140228_newLink.txt",
					// true);
					// BufferedWriter bw = new BufferedWriter(writer);
					//
					// bw.write(agentList.get(i).getId() + "\tN/O\t" +
					// agentList.get(i).getLinkedNodeSet().size()
					// + "\n");
					//
					// bw.close();
					// writer.close();
					// } catch (IOException e) {
					// e.printStackTrace();
					// }
				} else {
					// ��������V�K�����N[�����͗v��]
					// try {
					// Writer writer = new FileWriter("140228_newLink.txt",
					// true);
					// BufferedWriter bw = new BufferedWriter(writer);
					//
					// bw.write(agentList.get(i).getId() + "\t" +
					// agentList.get(nextLinkList.get(i)).getId() + "\t"
					// + agentList.get(i).getLinkedNodeSet().size() + "\n");
					//
					// bw.close();
					// writer.close();
					// } catch (IOException e) {
					// e.printStackTrace();
					// }
				}
				double testMyExpect = calcMyExpectNoWrite(agentList.get(i), agentList, countedDistanceAry);
				// �����܂ŐV�K�����N
				// try {
				// Writer writer = new FileWriter("140228_test.txt", true);
				// BufferedWriter bw = new BufferedWriter(writer);
				//
				// bw.write(lastExpectAry[i] + "\t" + testMyExpect + "\n");
				//
				// bw.close();
				// writer.close();
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				System.out.println("linkpair_final");
				for (Node linked : agentList.get(i).getLinkedNodeSet()) {
					Agent linkedA = (Agent) linked;
					try {
						Writer writer = new FileWriter("140228_linkpair.txt", true);
						BufferedWriter bw = new BufferedWriter(writer);

						bw.write(agentList.get(i).getId() + "\t" + linkedA.getId() + "\n");

						bw.close();
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				Writer writer = new FileWriter("140228_linkpair.txt", true);
				BufferedWriter bw = new BufferedWriter(writer);

				bw.newLine();

				bw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// ����������Ғl[�����͗v��]
			try {
				Writer writer = new FileWriter("140228_nwExpectation.txt", true);
				BufferedWriter bw = new BufferedWriter(writer);

				bw.write(trialExpectation + "\t" + cv + "\t" + path + "\t" + ass + "\t" + pow + "\t" + link + "\n");

				bw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// �����܂Ŋ��Ғl
			if (p == (maxLinkNum - 1)) {
				// ��������V�K�����N[�����͗v��]
				// try {
				// Writer writer = new FileWriter("140228_newLink.txt", true);
				// BufferedWriter bw = new BufferedWriter(writer);
				// bw.write("finish\n");
				// bw.close();
				// writer.close();
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				// �����܂ŐV�K�����N
			}
		}

	}

	public static void chooseNewLinkRandom(int agentNum, List<Agent> agentList, int maxLinkNum) {

		List<Agent> copyList = makeCopyList(agentList);

		for (int p = 0; p < maxLinkNum; p++) {
			List<Integer> nextLinkList = new ArrayList<>(); // get(i)�ŁAid��i�����G�[�W�F���g�����Ƀ����N���쐬���ׂ��G�[�W�F���g��id�𓾂���

			// �e�G�[�W�F���g�����Ƀ����N���쐬���ׂ��G�[�W�F���g�����肷��
			for (int i = 0; i < copyList.size(); i++) {
				Set<Agent> twostepAgentSet = makeTwostepAgentSet(copyList.get(i));

				// 2�X�e�b�v��̃m�[�h���烉���_����1�I��
				int preId = 10000;
				if (twostepAgentSet.size() > 0) {
					Random rand = new Random();
					int randomAgentNum = rand.nextInt(twostepAgentSet.size());
					int counter = 0;
					for (Agent randomA : twostepAgentSet) {
						preId = randomA.getId();
						while (counter < randomAgentNum) {
							counter++;
							break;
						}
					}
				}
				nextLinkList.add(preId);
			}

			// �S�G�[�W�F���g��1�������N��ǉ�
			for (int i = 0; i < copyList.size(); i++) {
				if (nextLinkList.get(i) > copyList.size()) {
					continue;
				} else {
					Agent receiver = copyList.get(i);
					Agent newLinker = copyList.get(nextLinkList.get(i));
					receiver.addLink(newLinker);
				}
			}

			int[][] countedDistanceAry = calcCountedDistanceAry(agentNum, copyList);
			double[] newExpectationAry = new double[copyList.size()];
			double trialExpectation = 0.0;
			// �l�b�g���[�N�S�̂̓��_�Z�o
			for (int q = 0; q < copyList.size(); q++) {
				trialExpectation += calcMyExpectNoWrite(copyList.get(q), copyList, countedDistanceAry);
			}
			// System.out.println("score�F" + trialExpectation);

			SimpleNetwork<Agent, Link> network = new SimpleNetwork<Agent, Link>(copyList);
			double cv = network.getClusterValue();
			double path = network.getAveragePathLength();

			double ass = network.getAssortativity();
			double pow = network.getDegreeDistributionMap().getPowerIndex();
			double link = network.getDegreeDistributionMap().getAverageDegree();
			// System.out.printf("%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t\n", cv, path,
			// ass, pow, link);

			// ����������Ғl[�����͗v��]
			try {
				Writer writer = new FileWriter("140228_nwExpectation_random.txt", true);
				BufferedWriter bw = new BufferedWriter(writer);

				bw.write(trialExpectation + "\t" + cv + "\t" + path + "\t" + ass + "\t" + pow + "\t" + link + "\n");

				bw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// �����܂Ŋ��Ғl
		}
		try {
			Writer writer = new FileWriter("140228_Expectation_random.txt", true);
			BufferedWriter bw = new BufferedWriter(writer);

			bw.newLine();

			bw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
