package trial.preparation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import basic.preparation.NetworkGenerator;
import basic.preparation.Node;

public class LinkOptimizer_2 {

	static double sigma = 0.5; // ������
	static double linkedCost = 4.5; // ���ڃ����N����R�X�g

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NetworkGenerator generator = new NetworkGenerator();

		LinkOptimizer_2 lo = new LinkOptimizer_2();
		List<Agent> agentList = new ArrayList<>();

		int allLink = 4;

		for (int i = 0; i < 1000; i++) { // i�̏���ŃG�[�W�F���g���ύX
			agentList.add(new Agent());
		}

		for (Agent agent : agentList) {
			agent.setInfoValue(lo.setInfoValue(agent.getInfoValue())); // �e�G�[�W�F���g�̎����̉��l��ݒ�
			// System.out.println(agent.getId() + "'s info score is" +
			// agent.getInfoValue());
		}

		generator.createTwoWaySmallWorldConstLinkNum(agentList, allLink, 0.1, 0.05, 0.05); // �S�����N���A����ւ����A�����N�̃����_�����A�����N�̒P������

		// ����������Ғl�p[�����͗v��]
		try {
			Writer writer = new FileWriter("140114_expectation.txt", true);
			BufferedWriter bw = new BufferedWriter(writer);

			bw.write("From\tTo\tNew Links\tExpectation\n");

			bw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// �����܂Ŋ��Ғl�p

		// ��������m�[�h���X�g
		for (Agent node : agentList) {
			try {
				Writer writer = new FileWriter("140114_list.txt", true);
				BufferedWriter bw = new BufferedWriter(writer);

				bw.write(node.getId() + "\t");

				for (Node linkedNode : node.getLinkedNodeSet()) {
					Agent linkedAgent = (Agent) linkedNode;
					bw.write(linkedAgent.getId() + "\t");
				}

				bw.newLine();
				bw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// System.out.println(node.getId() + "�̖{���̃����N���F" +
			// node.getLinkedNodeSet().size());
		}
		// �����܂Ńm�[�h���X�g

		int agentNum = agentList.size();

		System.out.println("����NW�̓��_�F" + calculate(agentNum, agentList));
		double firstExpectation = calculate(agentNum, agentList);

		// ����������Ғl[�����͗v��]
		try {
			Writer writer = new FileWriter("140114_expectation.txt", true);
			BufferedWriter bw = new BufferedWriter(writer);

			bw.write("N/O\tN/O\tN/O\t" + firstExpectation + "\n");

			bw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// �����܂Ŋ��Ғl

		chooseNewLink(agentNum, agentList);
	}

	private final Random rand;

	public LinkOptimizer_2(Random rand) {
		this.rand = rand;
	}

	public LinkOptimizer_2() {
		this.rand = new Random();
	}

	private double setInfoValue(double infoValue) {
		infoValue = rand.nextDouble();
		// System.out.println(infoValue);
		return infoValue;
	}

	public static double calculate(int agentNum, List<Agent> agentList) {
		double networkExpectation = 0.0;
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

		// try {
		// Writer writer = new FileWriter("distanceArray.txt", true);
		// BufferedWriter bw = new BufferedWriter(writer);
		//
		// bw.write("<distance array>\n");
		// bw.write("from\tto\n");
		// for (int i = 0; i < distanceArray[0].length; i++) {
		// for (int j = 0; j < distanceArray.length; j++) {
		// bw.write(i + "\t" + j + "\t" + distanceArray[i][j] + "\n");
		// }
		// }
		// bw.close();
		// writer.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		// �t���C�h�̃A���S���Y���ɂ��ŒZ�o�H���Z�o
		int[][] countedDistanceAry = new int[agentNum][agentNum];
		countedDistanceAry = floydAlgo(agentList, distanceArray);

		// try {
		// Writer writer = new FileWriter("countedDistanceArray.txt", true);
		// BufferedWriter bw = new BufferedWriter(writer);
		//
		// bw.write("<counted distance array>\n");
		// bw.write("from\tto\n");
		// for (int i = 0; i < distanceArray[0].length; i++) {
		// for (int j = 0; j < distanceArray.length; j++) {
		// bw.write(i + "\t" + j + "\t" + countedDistanceAry[i][j] + "\n");
		// }
		// }
		// bw.close();
		// writer.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		// �l�b�g���[�N�S�̂̓��_�Z�o
		networkExpectation = calculateExpectation(agentList, countedDistanceAry);

		return networkExpectation;
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
					scoreAry[agent.getId()][target.getId()] += target.getInfoValue() * (sigma - linkedCost);
					// System.out.println(agent.getId() + "and" + target.getId()
					// + "is directly linked and target's info score is:"
					// + target.getInfoValue());
					// System.out.println(agent.getId() + "and" + target.getId()
					// + "is directly linked and score:"
					// + scoreAry[agent.getId()][target.getId()]);
				} else {
					scoreAry[agent.getId()][target.getId()] += target.getInfoValue()
							* (Math.pow(sigma, countedDistanceAry[agent.getId()][target.getId()]));
					// System.out.println(agent.getId() + "and" + target.getId()
					// + "is not directly linked and score:"
					// + scoreAry[agent.getId()][target.getId()]);
				}
				// try {
				// Writer writer = new FileWriter("scoreArray.txt", true);
				// BufferedWriter bw = new BufferedWriter(writer);
				//
				// bw.write("<score array>\n");
				// bw.write("from\tto\n");
				// for (int i = 0; i < scoreAry[0].length; i++) {
				// for (int j = 0; j < scoreAry.length; j++) {
				// bw.write(i + "\t" + j + "\t" + scoreAry[i][j] + "\n");
				// }
				// }
				// bw.close();
				// writer.close();
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				expectation[agent.getId()] += scoreAry[agent.getId()][target.getId()];
			}
		}

		for (Agent agent : agentList) {
			nwScore += expectation[agent.getId()];
		}
		return nwScore;
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
			// System.out.println("copyLinkedAgentSet Size:" +
			// copyLinkedAgentSet.size());
			for (Agent copyLinkedAgent : copyLinkedAgentSet) {
				copyList.get(k).addLink(copyLinkedAgent);
			}
		}
		// �����܂ł��R�s�[���X�g�쐬

		// for (int i = 0; i < copyList.size(); i++) {
		// System.out.println(copyList.get(i).getId() + "�̃����N����" +
		// copyList.get(i).getLinkedNodeSet().size());
		// }

		for (int i = 0; i < copyList.size(); i++) {

			Set<Node> twostepNodeSet = new HashSet<>();

			List<Node> linkedNodeList = new ArrayList<>();
			for (Node node : copyList.get(i).getLinkedNodeSet()) {
				Agent neighbor = (Agent) node;
				linkedNodeList.add(neighbor);
			}

			int counter = 0;

			for (Node node : linkedNodeList) {

				Agent neighbor = (Agent) node;
				Set<? extends Node> targetNodeSet = neighbor.getLinkedNodeSet();

				for (Node nextNode : targetNodeSet) {
					Agent target = (Agent) nextNode;
					twostepNodeSet.add(target);
				}

			}
			System.out.println("from:" + copyList.get(i).getId() + "\toriginal links:"
					+ copyList.get(i).getLinkedNodeSet().size() + "\tunlinked links:" + twostepNodeSet.size());

			// for (Node nextNode : twostepNodeSet) {
			// Agent target = (Agent) nextNode;
			// System.out.println(target.getId());
			// }

			// ��������m�[�h���X�g�p
			// try {
			// Writer writer = new FileWriter("list.txt", true);
			// BufferedWriter bw = new BufferedWriter(writer);
			//
			// bw.write(copyList.get(i).getId());
			//
			// bw.close();
			// writer.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// �����܂�

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
				// System.out.println("dammyLinkedAgentSet Size:" +
				// dammyLinkedAgentSet.size());
				for (Agent dammyLinkedAgent : dammyLinkedAgentSet) {
					dammyList.get(k).addLink(dammyLinkedAgent);
				}
			}
			// �����܂ł��_�~�[���X�g�쐬

			for (Node twostepNode : twostepNodeSet) {

				Agent candidate = (Agent) twostepNode;
				int id = 0;
				for (int j = 0; j < copyList.size(); j++) {
					if (candidate == copyList.get(j)) {
						id = j;
					}
				}

				if (copyList.get(i) != candidate && !copyList.get(i).isLinkTo(candidate)) {

					dammyList.get(i).addLink(dammyList.get(id));

					// System.out.println("����ւ���̃����N�� original:" +
					// agentList.get(i).getLinkedNodeSet().size() + " copy:"
					// + copyList.get(i).getLinkedNodeSet().size() + " dammy:"
					// + dammyList.get(i).getLinkedNodeSet().size());
					trialExpectation = calculate(agentNum, dammyList);
					System.out.println("newly linked to:" + dammyList.get(id).getId() + "\ttotal links:"
							+ dammyList.get(i).getLinkedNodeSet().size() + "\tscore�F" + trialExpectation);
					counter++;

					// ��������m�[�h���X�g[�����͗v��Ȃ�]
					// try {
					// Writer writer = new FileWriter("list.txt", true);
					// BufferedWriter bw = new BufferedWriter(writer);
					//
					// bw.write(dammyList.get(i).getId() + "\t");
					//
					// for (Node linkedNode :
					// dammyList.get(i).getLinkedNodeSet()) {
					// Agent linkedAgent = (Agent) linkedNode;
					// bw.write(linkedAgent.getId() + "\t");
					// }
					//
					// bw.newLine();
					// bw.write("copyListIs:\n");
					// bw.write(copyList.get(i).getId() + "\t");
					//
					// for (Node linkedNode :
					// copyList.get(i).getLinkedNodeSet()) {
					// Agent linkedAgent = (Agent) linkedNode;
					// bw.write(linkedAgent.getId() + "\t");
					// }
					//
					// bw.newLine();
					// bw.write("originalListIs:\n");
					// bw.write(agentList.get(i).getId() + "\t");
					//
					// for (Node linkedNode :
					// agentList.get(i).getLinkedNodeSet()) {
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
					// �����܂Ńm�[�h���X�g

					// ����������Ғl[�����͗v��]
					try {
						Writer writer = new FileWriter("140114_expectation.txt", true);
						BufferedWriter bw = new BufferedWriter(writer);

						bw.write(dammyList.get(i).getId()
								+ "\t"
								+ dammyList.get(id).getId()
								+ "\t"
								+ (dammyList.get(i).getLinkedNodeSet().size() - copyList.get(i).getLinkedNodeSet()
										.size()) + "\t" + trialExpectation + "\n");

						bw.close();
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					// �����܂Ŋ��Ғl

				}// if��

			}// for node��
				// System.out.println("�����X�g" + agentList.get(i).getId() +
				// "�̃����N����"
			// + agentList.get(i).getLinkedNodeSet().size());
			// System.out.println("�R�s�[���X�g" + copyList.get(i).getId() + "�̃����N����"
			// + copyList.get(i).getLinkedNodeSet().size());
			// System.out.println(copyList.get(i).getId() + "�̐V�K�����N��" +
			// counter);
		}
	}

}
