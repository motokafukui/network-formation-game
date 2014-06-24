package trial.preparation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import basic.preparation.NetworkGenerator;
import basic.preparation.Node;

public class LinkOptimizer_3 {

	static double sigma = 0.5; // ������
	static double linkedCost = 2.0; // ���ڃ����N����R�X�g

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NetworkGenerator generator = new NetworkGenerator();

		LinkOptimizer_3 lo = new LinkOptimizer_3();
		List<Agent> agentList = new ArrayList<>();

		int allLink = 4;

		for (int i = 0; i < 100; i++) { // i�̏���ŃG�[�W�F���g���ύX
			agentList.add(new Agent());
		}

		for (Agent agent : agentList) {
			agent.setInfoValue(lo.setInfoValue(agent.getInfoValue())); // �e�G�[�W�F���g�̎����̉��l��ݒ�
		}

		generator.createTwoWaySmallWorldConstLinkNum(agentList, allLink, 0.1, 0.05, 0.05); // �S�����N���A����ւ����A�����N�̃����_�����A�����N�̒P������

		// // ����������Ғl�p[�����͗v��]
		// try {
		// Writer writer = new FileWriter("140114_expectation.txt", true);
		// BufferedWriter bw = new BufferedWriter(writer);
		//
		// bw.write("From\tTo\tNew Links\tExpectation\n");
		//
		// bw.close();
		// writer.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// // �����܂Ŋ��Ғl�p
		//
		// // ��������m�[�h���X�g
		// for (Agent node : agentList) {
		// try {
		// Writer writer = new FileWriter("140114_list.txt", true);
		// BufferedWriter bw = new BufferedWriter(writer);
		//
		// bw.write(node.getId() + "\t");
		//
		// for (Node linkedNode : node.getLinkedNodeSet()) {
		// Agent linkedAgent = (Agent) linkedNode;
		// bw.write(linkedAgent.getId() + "\t");
		// }
		//
		// bw.newLine();
		// bw.close();
		// writer.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		// // �����܂Ńm�[�h���X�g

		int agentNum = agentList.size();
		double firstNWExpectation = 0.0;
		int[][] countedDistanceAry = calcCountedDistanceAry(agentNum, agentList);
		// �l�b�g���[�N�S�̂̓��_�Z�o
		firstNWExpectation = calculateExpectation(agentList, countedDistanceAry);
		System.out.println("����NW�̓��_�F" + firstNWExpectation);

		// // ����������Ғl[�����͗v��]
		// try {
		// Writer writer = new FileWriter("140114_expectation.txt", true);
		// BufferedWriter bw = new BufferedWriter(writer);
		//
		// bw.write("N/O\tN/O\tN/O\t" + firstNWExpectation + "\n");
		//
		// bw.close();
		// writer.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// // �����܂Ŋ��Ғl

		chooseNewLink(agentNum, agentList);
	}

	private final Random rand;

	public LinkOptimizer_3(Random rand) {
		this.rand = rand;
	}

	public LinkOptimizer_3() {
		this.rand = new Random();
	}

	private double setInfoValue(double infoValue) {
		infoValue = rand.nextDouble();
		return infoValue;
	}

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

	public static double calculateExpectation(List<Agent> agentList, int[][] countedDistanceAry) {
		int agentNum = agentList.size();
		double nwScore = 0.0;
		double[][] scoreAry = new double[agentNum][agentNum];
		double[] expectation = new double[agentNum];
		for (Agent agent : agentList) {
			for (Agent target : agentList) {
				if (countedDistanceAry[agent.getId()][target.getId()] == 0) {
					continue;
				} else if (countedDistanceAry[agent.getId()][target.getId()] == 1) {
					double myScore = target.getInfoValue() * (sigma - linkedCost);
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
		}

		for (Agent agent : agentList) {
			nwScore += expectation[agent.getId()];
		}
		return nwScore;
	}

	public static double calculateEachExpect(Agent receiver, Agent sender, Set<? extends Node> nodeSet,
			int[][] countedDistanceAry) {
		double nodeExpect = 0.0;
		if (countedDistanceAry[receiver.getId()][sender.getId()] == 1) {
			nodeExpect = sender.getInfoValue() * (sigma - linkedCost);
		} else if (countedDistanceAry[receiver.getId()][sender.getId()] != 0) {
			nodeExpect = sender.getInfoValue()
					* (Math.pow(sigma, countedDistanceAry[receiver.getId()][sender.getId()]));
		}
		return nodeExpect;
	}

	public static void chooseNewLink(int agentNum, List<Agent> agentList) {
		double trialExpectation = 0.0;

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

		for (int i = 0; i < copyList.size(); i++) {

			// ��������
			List<Agent> dammyList = new ArrayList<>();
			for (int k = 0; k < agentList.size(); k++) {
				Agent dammyAgent = new Agent();

				int dammyId = agentList.get(k).getId();
				double dammyInfoValue = agentList.get(k).getInfoValue();

				dammyList.add(dammyAgent);
				dammyList.get(k).setId(dammyId);
				dammyList.get(k).setInfoValue(dammyInfoValue);

			}

			for (int k = 0; k < dammyList.size(); k++) {
				Set<Agent> dammyLinkedAgentSet = new HashSet<>();
				for (Node dammyLinkedNode : agentList.get(k).getLinkedNodeSet()) {
					Agent dammyLinkedAgent = (Agent) dammyLinkedNode;
					int dammyLinkedAgentId = dammyLinkedAgent.getId();
					dammyLinkedAgentSet.add(dammyList.get(dammyLinkedAgentId));
				}

				for (Agent dammyLinkedAgent : dammyLinkedAgentSet) {
					dammyList.get(k).addLink(dammyLinkedAgent);
				}
			}
			// �����܂ł��_�~�[���X�g�쐬

			int newlinks = 0;

			HashMap<Integer, Agent> linkedAgentMap = new HashMap<Integer, Agent>();
			for (Node node : dammyList.get(i).getLinkedNodeSet()) {
				Agent neighbor = (Agent) node;
				linkedAgentMap.put(neighbor.getId(), neighbor);
			}

			do {
				Set<Node> twostepNodeSet = new HashSet<>();
				for (Integer id : linkedAgentMap.keySet()) {
					Agent neighbor = linkedAgentMap.get(id);
					Set<? extends Node> targetNodeSet = neighbor.getLinkedNodeSet();

					for (Node nextNode : targetNodeSet) {
						Agent target = (Agent) nextNode;
						if (target.getId() != dammyList.get(i).getId() && !linkedAgentMap.containsKey(target.getId())) {
							twostepNodeSet.add(target);
						}
					}
				}

				System.out.println("linkedNode");
				for (Integer id : linkedAgentMap.keySet()) {
					Agent linkedAgent = linkedAgentMap.get(id);
					System.out.print(linkedAgent.getId() + "\t");
				}

				System.out.println();
				System.out.println("twostepNode");
				for (Node twostepNode : twostepNodeSet) {
					Agent twostepAgent = (Agent) twostepNode;
					System.out.print(twostepAgent.getId() + "\t");
				}
				System.out.println();
				System.out.println("from:" + copyList.get(i).getId() + "\tcurrent links:"
						+ dammyList.get(i).getLinkedNodeSet().size() + "\tcurrent unlinked links:"
						+ twostepNodeSet.size());
				// 2�X�e�b�v��̃m�[�h�̂����A�ł����Ғl�̍������̂����߂�
				int[][] countedDistanceAry = calcCountedDistanceAry(agentNum, dammyList);
				HashMap<Integer, Double> twostepExpectMap = new HashMap<Integer, Double>();
				double preMax = 0.0;
				int preId = 10000;
				for (Node twostepNode : twostepNodeSet) {
					Agent sender = (Agent) twostepNode;
					double eachExpect = calculateEachExpect(dammyList.get(i), sender, twostepNodeSet,
							countedDistanceAry);
					// System.out.println("sender:"+sender.getId()+"score:"+eachExpect);
					twostepExpectMap.put(sender.getId(), eachExpect);
					if (preMax < twostepExpectMap.get(sender.getId())) {
						preMax = twostepExpectMap.get(sender.getId());
						preId = sender.getId();
					}
				}

				dammyList.get(i).addLink(dammyList.get(preId));
				countedDistanceAry = calcCountedDistanceAry(agentNum, dammyList);

				trialExpectation = calculateExpectation(dammyList, countedDistanceAry);
				System.out.println("newly linked to:" + dammyList.get(preId).getId() + "\ttotal links:"
						+ dammyList.get(i).getLinkedNodeSet().size() + "\tscore�F" + trialExpectation);

				newlinks = dammyList.get(i).getLinkedNodeSet().size() - copyList.get(i).getLinkedNodeSet().size();
				linkedAgentMap.put(preId, dammyList.get(preId));

				// // ����������Ғl[�����͗v��]
				// try {
				// Writer writer = new FileWriter("140114_expectation.txt",
				// true);
				// BufferedWriter bw = new BufferedWriter(writer);
				//
				// bw.write(dammyList.get(i).getId()
				// + "\t"
				// + dammyList.get(preId).getId()
				// + "\t"
				// + (dammyList.get(i).getLinkedNodeSet().size() -
				// copyList.get(i).getLinkedNodeSet()
				// .size()) + "\t" + trialExpectation + "\n");
				//
				// bw.close();
				// writer.close();
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				// // �����܂Ŋ��Ғl
			} while (newlinks < 5);
		}//for�̕�
	}

}
