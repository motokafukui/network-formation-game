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

public class LinkOptimizer_7 {

	static double sigma = 0.5; // ������
	static double linkedCost = 0.2; // ���ڃ����N����R�X�g

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NetworkGenerator generator = new NetworkGenerator();

		LinkOptimizer_7 lo = new LinkOptimizer_7();
		List<Agent> agentList = new ArrayList<>();

		int allLink = 4;

		for (int i = 0; i < 100; i++) { // i�̏���ŃG�[�W�F���g���ύX
			agentList.add(new Agent());
		}
		for (int p = 0; p < 50; p++) {
			System.out.println(p + "���");
			for (Agent agent : agentList) {
				agent.setInfoValue(lo.setInfoValue(agent.getInfoValue())); // �e�G�[�W�F���g�̎����̉��l��ݒ�
//				System.out.println(agent.infoValue);
			}

//			generator.createTwoWaySmallWorldConstLinkNum(agentList, allLink, 0.1, 0.05, 0.05); // �S�����N���A����ւ����A�����N�̃����_�����A�����N�̒P������
//			 generator.createCnnModel(agentList, 4);
//			 generator.createRegularNetwork(agentList, allLink);
//			 generator.createBarabasiModel(agentList, 4, 2);
			 generator.createRandomNetwork(agentList, allLink);

			int agentNum = agentList.size();
			double firstNWExpectation = 0.0;
			double[] expectationAry = new double[agentNum];
			int[][] countedDistanceAry = calcCountedDistanceAry(agentNum, agentList);
			expectationAry = firstCalculateMyExpect(agentList, countedDistanceAry);
			// �l�b�g���[�N�S�̂̓��_�Z�o
			firstNWExpectation = calculateExpectation(agentList, expectationAry);
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
				Writer writer = new FileWriter("140204_nwExpectation.txt", true);
				BufferedWriter bw = new BufferedWriter(writer);

				bw.write(firstNWExpectation + "\t" + cv + "\t" + path + "\t" + ass + "\t" + pow + "\t" + link + "\n");

				bw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// �����܂Ŋ��Ғl
			// // ����������Ғl[�����͗v��]
			// try {
			// Writer writer = new
			// FileWriter("140204_nwExpectation_compare.txt", true);
			// BufferedWriter bw = new BufferedWriter(writer);
			//
			// bw.write(firstNWExpectation + "\t" + cv + "\t" + path + "\t" +
			// ass + "\t" + pow + "\t" + link + "\n");
			//
			// bw.close();
			// writer.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// // �����܂Ŋ��Ғl
			// ����������Ғl[�����͗v��]
			try {
				Writer writer = new FileWriter("140204_nwExpectation_random.txt", true);
				BufferedWriter bw = new BufferedWriter(writer);

				bw.write(firstNWExpectation + "\t" + cv + "\t" + path + "\t" + ass + "\t" + pow + "\t" + link + "\n");

				bw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// �����܂Ŋ��Ғl

			chooseNewLink(agentNum, agentList, expectationAry, 100);
			// compareChooseNewLink(agentNum, agentList, 50);
			randomChooseNewLink(agentNum, agentList, 100);
		}
		System.out.println("finish");
	}

	private final Random rand;

	public LinkOptimizer_7(Random rand) {
		this.rand = rand;
	}

	public LinkOptimizer_7() {
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
				if (target.isLinkTo(agent)) {
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

	public static double[] firstCalculateMyExpect(List<Agent> agentList, int[][] countedDistanceAry) {
		int agentNum = agentList.size();
		double[][] scoreAry = new double[agentNum][agentNum];
		double[] expectation = new double[agentNum];
		for (Agent agent : agentList) {
			for (Agent target : agentList) {
				if (countedDistanceAry[agent.getId()][target.getId()] == 0) {
					continue;
				} else if (countedDistanceAry[agent.getId()][target.getId()] == 1) {
					double myScore = target.getInfoValue() * sigma;
					scoreAry[agent.getId()][target.getId()] += myScore;
					agent.score = myScore;

				} else {
					double myScore = target.getInfoValue()
							* (Math.pow(sigma, countedDistanceAry[agent.getId()][target.getId()]));
					scoreAry[agent.getId()][target.getId()] += myScore;
					agent.score = myScore;
				}

				expectation[agent.getId()] += scoreAry[agent.getId()][target.getId()];
			}
			double preExp = expectation[agent.getId()];
			int linkedNum = agent.getLinkedNodeSet().size();
			expectation[agent.getId()] -= linkedCost * linkedNum;
			// try {
			// Writer writer = new FileWriter("140204_Expectation.txt", true);
			// BufferedWriter bw = new BufferedWriter(writer);
			//
			// bw.write(expectation[agent.getId()] + "\t" + linkedNum + "\t" +
			// preExp + "\n");
			//
			// bw.close();
			// writer.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// try {
			// Writer writer = new FileWriter("140204_Expectation_compare.txt",
			// true);
			// BufferedWriter bw = new BufferedWriter(writer);
			//
			// bw.write(expectation[agent.getId()] + "\t" + linkedNum + "\t" +
			// preExp + "\n");
			//
			// bw.close();
			// writer.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// try {
			// Writer writer = new FileWriter("140204_Expectation_random.txt",
			// true);
			// BufferedWriter bw = new BufferedWriter(writer);
			//
			// bw.write(expectation[agent.getId()] + "\t" + linkedNum + "\t" +
			// preExp + "\n");
			//
			// bw.close();
			// writer.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
		}
		// try {
		// Writer writer = new FileWriter("140204_Expectation.txt", true);
		// BufferedWriter bw = new BufferedWriter(writer);
		//
		// bw.newLine();
		//
		// bw.close();
		// writer.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// try {
		// Writer writer = new FileWriter("140204_Expectation_compare.txt",
		// true);
		// BufferedWriter bw = new BufferedWriter(writer);
		//
		// bw.newLine();
		//
		// bw.close();
		// writer.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// try {
		// Writer writer = new FileWriter("140204_Expectation_random.txt",
		// true);
		// BufferedWriter bw = new BufferedWriter(writer);
		//
		// bw.newLine();
		//
		// bw.close();
		// writer.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		return expectation;
	}

	public static double[] nowriteCalculateMyExpect(List<Agent> agentList, int[][] countedDistanceAry) {
		int agentNum = agentList.size();
		double[][] scoreAry = new double[agentNum][agentNum];
		double[] expectation = new double[agentNum];
		for (Agent agent : agentList) {
			for (Agent target : agentList) {
				if (countedDistanceAry[agent.getId()][target.getId()] == 0) {
					continue;
				} else if (countedDistanceAry[agent.getId()][target.getId()] == 1) {
					double myScore = target.getInfoValue() * sigma;
					scoreAry[agent.getId()][target.getId()] += myScore;
					agent.score = myScore;

				} else {
					double myScore = target.getInfoValue()
							* (Math.pow(sigma, countedDistanceAry[agent.getId()][target.getId()]));
					scoreAry[agent.getId()][target.getId()] += myScore;
					agent.score = myScore;
				}

				expectation[agent.getId()] += scoreAry[agent.getId()][target.getId()];
			}
			int linkedNum = agent.getLinkedNodeSet().size();
			expectation[agent.getId()] -= linkedCost * linkedNum;
		}
		return expectation;
	}

	public static double[] defaultCalculateMyExpect(List<Agent> agentList, int[][] countedDistanceAry) {
		int agentNum = agentList.size();
		double[][] scoreAry = new double[agentNum][agentNum];
		double[] expectation = new double[agentNum];
		for (Agent agent : agentList) {
			for (Agent target : agentList) {
				if (countedDistanceAry[agent.getId()][target.getId()] == 0) {
					continue;
				} else if (countedDistanceAry[agent.getId()][target.getId()] == 1) {
					double myScore = target.getInfoValue() * sigma;
					scoreAry[agent.getId()][target.getId()] += myScore;
					agent.score = myScore;

				} else {
					double myScore = target.getInfoValue()
							* (Math.pow(sigma, countedDistanceAry[agent.getId()][target.getId()]));
					scoreAry[agent.getId()][target.getId()] += myScore;
					agent.score = myScore;
				}

				expectation[agent.getId()] += scoreAry[agent.getId()][target.getId()];
			}
			double preExp = expectation[agent.getId()];
			int linkedNum = agent.getLinkedNodeSet().size();
			expectation[agent.getId()] -= linkedCost * linkedNum;
			try {
				Writer writer = new FileWriter("140204_Expectation.txt", true);
				BufferedWriter bw = new BufferedWriter(writer);

				bw.write(expectation[agent.getId()] + "\t" + linkedNum + "\n");

				bw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			Writer writer = new FileWriter("140204_Expectation.txt", true);
			BufferedWriter bw = new BufferedWriter(writer);

			bw.newLine();

			bw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return expectation;
	}

	public static double[] compareCalculateMyExpect(List<Agent> agentList, int[][] countedDistanceAry) {
		int agentNum = agentList.size();
		double[][] scoreAry = new double[agentNum][agentNum];
		double[] expectation = new double[agentNum];
		for (Agent agent : agentList) {
			for (Agent target : agentList) {
				if (countedDistanceAry[agent.getId()][target.getId()] == 0) {
					continue;
				} else if (countedDistanceAry[agent.getId()][target.getId()] == 1) {
					double myScore = target.getInfoValue() * sigma;
					scoreAry[agent.getId()][target.getId()] += myScore;
					agent.score = myScore;

				} else {
					double myScore = target.getInfoValue()
							* (Math.pow(sigma, countedDistanceAry[agent.getId()][target.getId()]));
					scoreAry[agent.getId()][target.getId()] += myScore;
					agent.score = myScore;
				}

				expectation[agent.getId()] += scoreAry[agent.getId()][target.getId()];
			}
			double preExp = expectation[agent.getId()];
			int linkedNum = agent.getLinkedNodeSet().size();
			expectation[agent.getId()] -= linkedCost * linkedNum;
			try {
				Writer writer = new FileWriter("140204_Expectation_compare.txt", true);
				BufferedWriter bw = new BufferedWriter(writer);

				bw.write(expectation[agent.getId()] + "\t" + linkedNum + "\n");

				bw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			Writer writer = new FileWriter("140204_Expectation_compare.txt", true);
			BufferedWriter bw = new BufferedWriter(writer);

			bw.newLine();

			bw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return expectation;
	}

	public static double[] randomCalculateMyExpect(List<Agent> agentList, int[][] countedDistanceAry) {
		int agentNum = agentList.size();
		double[][] scoreAry = new double[agentNum][agentNum];
		double[] expectation = new double[agentNum];
		for (Agent agent : agentList) {
			for (Agent target : agentList) {
				if (countedDistanceAry[agent.getId()][target.getId()] == 0) {
					continue;
				} else if (countedDistanceAry[agent.getId()][target.getId()] == 1) {
					double myScore = target.getInfoValue() * sigma;
					scoreAry[agent.getId()][target.getId()] += myScore;
					agent.score = myScore;

				} else {
					double myScore = target.getInfoValue()
							* (Math.pow(sigma, countedDistanceAry[agent.getId()][target.getId()]));
					scoreAry[agent.getId()][target.getId()] += myScore;
					agent.score = myScore;
				}

				expectation[agent.getId()] += scoreAry[agent.getId()][target.getId()];
			}
			double preExp = expectation[agent.getId()];
			int linkedNum = agent.getLinkedNodeSet().size();
			expectation[agent.getId()] -= linkedCost * linkedNum;
			try {
				Writer writer = new FileWriter("140204_Expectation_random.txt", true);
				BufferedWriter bw = new BufferedWriter(writer);

				bw.write(expectation[agent.getId()] + "\t" + linkedNum + "\n");

				bw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			Writer writer = new FileWriter("140204_Expectation_random.txt", true);
			BufferedWriter bw = new BufferedWriter(writer);

			bw.newLine();

			bw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return expectation;
	}

	/*
	 * NW�S�̂̊��Ғl���v�Z����
	 */
	public static double calculateExpectation(List<Agent> agentList, double[] expectation) {
		double nwScore = 0.0;
		for (Agent agent : agentList) {
			nwScore += expectation[agent.getId()];
		}
		return nwScore;
	}

	/*
	 * ����m�[�h����̊��Ғl���v�Z����
	 */
	public static double calculateEachExpect(Agent receiver, Agent sender, Set<? extends Node> nodeSet,
			int[][] countedDistanceAry) {
		double nodeExpect = 0.0;
		if (countedDistanceAry[receiver.getId()][sender.getId()] == 1) {
			nodeExpect = sender.getInfoValue() * sigma;
		} else if (countedDistanceAry[receiver.getId()][sender.getId()] != 0) {
			nodeExpect = sender.getInfoValue()
					* (Math.pow(sigma, countedDistanceAry[receiver.getId()][sender.getId()]));
		}
		int linkedNum = receiver.getLinkedNodeSet().size();
		nodeExpect -= linkedCost * linkedNum;
		return nodeExpect;
	}

	public static void chooseNewLink(int agentNum, List<Agent> agentList, double[] expectationAry, int maxLinkNum) {

		// ��������
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
		// �����܂ł��R�s�[���X�g�쐬

		for (int p = 0; p < maxLinkNum; p++) {

			int[][] preCountedDistanceAry = calcCountedDistanceAry(agentNum, copyList);
			double[] lastExpectation = new double[copyList.size()];
			lastExpectation = nowriteCalculateMyExpect(copyList, preCountedDistanceAry);

			List<Integer> nextLinkList = new ArrayList<>(); // get(i)�ŁAid��i�����G�[�W�F���g�����Ƀ����N���쐬���ׂ��G�[�W�F���g��id�𓾂���
			List<Double> nextPlusList = new ArrayList<>(); // �`�`���ɑ�������Ғl�̑傫����������
			// �e�G�[�W�F���g�����Ƀ����N���쐬���ׂ��G�[�W�F���g�����肷��
			for (int i = 0; i < copyList.size(); i++) {

				// ������1�X�e�b�v�Ń����N���Ă���m�[�h�̃n�b�V���}�b�v���쐬
				HashMap<Integer, Agent> linkedAgentMap = new HashMap<Integer, Agent>();
				for (Node node : copyList.get(i).getLinkedNodeSet()) {
					Agent neighbor = (Agent) node;
					linkedAgentMap.put(neighbor.getId(), neighbor);
				}

				// ������2�X�e�b�v�Ń����N���Ă���m�[�h�̃��X�g���쐬
				Set<Agent> twostepAgentSet = new HashSet<>();
				for (Integer id : linkedAgentMap.keySet()) {
					Agent neighbor = linkedAgentMap.get(id);
					Set<Agent> targetNodeSet = new HashSet<>();
					for (Node hisLinked : neighbor.getLinkedNodeSet()) {
						Agent linked = (Agent) hisLinked;
						targetNodeSet.add(linked);
					}
					for (Node nextNode : targetNodeSet) {
						Agent target = (Agent) nextNode;
						if (target.getId() != copyList.get(i).getId() && !linkedAgentMap.containsKey(target.getId())) {
							twostepAgentSet.add(target);
						}
					}
				}

				// 2�X�e�b�v��̃m�[�h�̂����A���ōł����Ғl�̍������̂����߂�
				int[][] currentDistanceAry = calcCountedDistanceAry(agentNum, copyList); // �m�[�h���m�̋���������s��
				double preMax = -100.0; // Max�̊��Ғl�����邽�߂̏�����
				int preId = 10000; // Max�̊��Ғl�𓾂���G�[�W�F���g��Id�����邽�߂̏�����
				for (Agent sender : twostepAgentSet) {
					double eachExpect = calculateEachExpect(copyList.get(i), sender, twostepAgentSet,
							currentDistanceAry);
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

				if (nextLinkList.get(i) > copyList.size()) {
					continue;
				} else if (nextPlusList.get(i) > linkedCost) {// �����̊��Ғl���ő�ɂȂ������߂�
					Agent receiver = copyList.get(i);
					Agent newLinker = copyList.get(nextLinkList.get(i));
					receiver.addLink(newLinker);
				}
			}

			int[][] countedDistanceAry = calcCountedDistanceAry(agentNum, copyList);
			double[] newExpectationAry = new double[copyList.size()];
			double trialExpectation = 0.0;
			if (p == maxLinkNum - 1) {
				newExpectationAry = defaultCalculateMyExpect(copyList, countedDistanceAry);
			} else {
				newExpectationAry = nowriteCalculateMyExpect(copyList, countedDistanceAry);
			}
			trialExpectation = calculateExpectation(copyList, newExpectationAry);
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
				Writer writer = new FileWriter("140204_nwExpectation.txt", true);
				BufferedWriter bw = new BufferedWriter(writer);

				bw.write(trialExpectation + "\t" + cv + "\t" + path + "\t" + ass + "\t" + pow + "\t" + link + "\n");

				bw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// �����܂Ŋ��Ғl
		}
	}

	public static void compareChooseNewLink(int agentNum, List<Agent> agentList, int maxLinkNum) {

		// ��������
		List<Agent> copyList = new ArrayList<>();
		for (int k = 0; k < agentList.size(); k++) {
			Agent copyAgent = new Agent();

			int copyId = agentList.get(k).getId();
			double copyInfoValue = agentList.get(k).getInfoValue();

			copyList.add(copyAgent);
			copyList.get(k).setId(copyId);
			copyList.get(k).setInfoValue(copyInfoValue);
		}
		for (int k = 0; k < copyList.size(); k++) {
			Set<Agent> copyLinkedAgentSet = new HashSet<>();
			for (Node copyLinkedNode : agentList.get(k).getLinkedNodeSet()) {
				Agent copyLinkedAgent = (Agent) copyLinkedNode;
				int copyLinkedAgentId = copyLinkedAgent.getId();
				copyLinkedAgentSet.add(copyList.get(copyLinkedAgentId));
			}
			for (Agent copyLinkedAgent : copyLinkedAgentSet) {
				copyList.get(k).addLink(copyLinkedAgent);
			}
		}
		// �����܂ł��R�s�[���X�g�쐬

		for (int p = 0; p < maxLinkNum; p++) {
			List<Integer> nextLinkList = new ArrayList<>(); // get(i)�ŁAid��i�����G�[�W�F���g�����Ƀ����N���쐬���ׂ��G�[�W�F���g��id�𓾂���

			// �e�G�[�W�F���g�����Ƀ����N���쐬���ׂ��G�[�W�F���g�����肷��
			for (int i = 0; i < copyList.size(); i++) {
				// ������1�X�e�b�v�Ń����N���Ă���m�[�h�̃n�b�V���}�b�v���쐬
				HashMap<Integer, Agent> linkedAgentMap = new HashMap<Integer, Agent>();
				for (Node node : copyList.get(i).getLinkedNodeSet()) {
					Agent neighbor = (Agent) node;
					linkedAgentMap.put(neighbor.getId(), neighbor);
				}

				// ������2�X�e�b�v�Ń����N���Ă���m�[�h�̃��X�g���쐬
				Set<Agent> twostepAgentSet = new HashSet<>();
				for (Integer id : linkedAgentMap.keySet()) {
					Agent neighbor = linkedAgentMap.get(id);
					Set<Agent> targetNodeSet = new HashSet<>();
					for (Node hisLinked : neighbor.getLinkedNodeSet()) {
						Agent linked = (Agent) hisLinked;
						targetNodeSet.add(linked);
					}
					for (Node nextNode : targetNodeSet) {
						Agent target = (Agent) nextNode;
						if (target.getId() != copyList.get(i).getId() && !linkedAgentMap.containsKey(target.getId())) {
							twostepAgentSet.add(target);
						}
					}
				}

				// 2�X�e�b�v��̃m�[�h�̂����A�ł����Ғl�̍������̂����߂�
				int[][] currentDistanceAry = calcCountedDistanceAry(agentNum, copyList); // �m�[�h���m�̋���������s��
				double preMax = -100.0; // Max�̊��Ғl�����邽�߂̏�����
				int preId = 10000; // Max�̊��Ғl�𓾂���G�[�W�F���g��Id�����邽�߂̏�����
				for (Agent sender : twostepAgentSet) {
					double eachExpect = calculateEachExpect(copyList.get(i), sender, twostepAgentSet,
							currentDistanceAry);
					// System.out.println("sender:"+sender.getId()+"score:"+eachExpect);
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
			if (p == maxLinkNum - 1) {
				newExpectationAry = compareCalculateMyExpect(copyList, countedDistanceAry);
			} else {
				newExpectationAry = nowriteCalculateMyExpect(copyList, countedDistanceAry);
			}
			trialExpectation = calculateExpectation(copyList, newExpectationAry);
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
				Writer writer = new FileWriter("140204_nwExpectation_compare.txt", true);
				BufferedWriter bw = new BufferedWriter(writer);

				bw.write(trialExpectation + "\t" + cv + "\t" + path + "\t" + ass + "\t" + pow + "\t" + link + "\n");

				bw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// �����܂Ŋ��Ғl
		}

	}

	public static void randomChooseNewLink(int agentNum, List<Agent> agentList, int maxLinkNum) {

		// ��������
		List<Agent> copyList = new ArrayList<>();
		for (int k = 0; k < agentList.size(); k++) {
			Agent copyAgent = new Agent();

			int copyId = agentList.get(k).getId();
			double copyInfoValue = agentList.get(k).getInfoValue();

			copyList.add(copyAgent);
			copyList.get(k).setId(copyId);
			copyList.get(k).setInfoValue(copyInfoValue);
		}
		for (int k = 0; k < copyList.size(); k++) {
			Set<Agent> copyLinkedAgentSet = new HashSet<>();
			for (Node copyLinkedNode : agentList.get(k).getLinkedNodeSet()) {
				Agent copyLinkedAgent = (Agent) copyLinkedNode;
				int copyLinkedAgentId = copyLinkedAgent.getId();
				copyLinkedAgentSet.add(copyList.get(copyLinkedAgentId));
			}
			for (Agent copyLinkedAgent : copyLinkedAgentSet) {
				copyList.get(k).addLink(copyLinkedAgent);
			}
		}
		// �����܂ł��R�s�[���X�g�쐬

		for (int p = 0; p < maxLinkNum; p++) {
			List<Integer> nextLinkList = new ArrayList<>(); // get(i)�ŁAid��i�����G�[�W�F���g�����Ƀ����N���쐬���ׂ��G�[�W�F���g��id�𓾂���

			// �e�G�[�W�F���g�����Ƀ����N���쐬���ׂ��G�[�W�F���g�����肷��
			for (int i = 0; i < copyList.size(); i++) {
				// ������1�X�e�b�v�Ń����N���Ă���m�[�h�̃n�b�V���}�b�v���쐬
				HashMap<Integer, Agent> linkedAgentMap = new HashMap<Integer, Agent>();
				for (Node node : copyList.get(i).getLinkedNodeSet()) {
					Agent neighbor = (Agent) node;
					linkedAgentMap.put(neighbor.getId(), neighbor);
				}

				// ������2�X�e�b�v�Ń����N���Ă���m�[�h�̃��X�g���쐬
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
						if (target.getId() != copyList.get(i).getId() && !linkedAgentMap.containsKey(target.getId())) {
							twostepAgentSet.add(target);
						}
					}
				}

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
			if (p == maxLinkNum - 1) {
				newExpectationAry = randomCalculateMyExpect(copyList, countedDistanceAry);
			} else {
				newExpectationAry = nowriteCalculateMyExpect(copyList, countedDistanceAry);
			}
			trialExpectation = calculateExpectation(copyList, newExpectationAry);
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
				Writer writer = new FileWriter("140204_nwExpectation_random.txt", true);
				BufferedWriter bw = new BufferedWriter(writer);

				bw.write(trialExpectation + "\t" + cv + "\t" + path + "\t" + ass + "\t" + pow + "\t" + link + "\n");

				bw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// �����܂Ŋ��Ғl
		}

	}
}
