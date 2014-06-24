//package trial.preparation;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class game {
//
//	public static void main(String[] args) {
//
//		List<Agent> agentList = new ArrayList<>();
//
//		// �G�[�W�F���g���쐬
//		for (int i = 0; i < 5; i++) {
//			agentList.add(new Agent());
//		}
//		// �����N�𒣂�
//		agentList.get(0).addLink(agentList.get(1));
//		agentList.get(0).addLink(agentList.get(2));
//		agentList.get(1).addLink(agentList.get(0));
//		agentList.get(1).addLink(agentList.get(2));
//		agentList.get(1).addLink(agentList.get(3));
//		agentList.get(2).addLink(agentList.get(0));
//		agentList.get(2).addLink(agentList.get(1));
//		agentList.get(2).addLink(agentList.get(4));
//		agentList.get(3).addLink(agentList.get(1));
//		agentList.get(3).addLink(agentList.get(4));
//		agentList.get(4).addLink(agentList.get(2));
//		agentList.get(4).addLink(agentList.get(3));
//
//		// // �ŒZ�o�H�����߂�
//		// int distance = dijkstra(agentList, agentList.get(4),
//		// agentList.get(0));
//		// System.out.println("���\�b�h���狁�߂�"+agentList.get(4).getId()+"����"+agentList.get(0).getId()+"�܂ł̋�����"+distance);
//
//		// ����������z��
//		int arraySize = agentList.size() * 3;
//		int[] distanceArray = new int[arraySize];
//		for (Agent receiver : agentList) {
//			System.out.println("receiver:" + receiver.getId());
//			for (Agent sender : agentList) {
//				System.out.println("sender:" + sender.getId());
//				for(Agent resetAgent : agentList){
//					resetAgent.fixed = false;
//					resetAgent.cost = 1000000000;
//				}
//				int distance = dijkstra(agentList, receiver, sender);
//				switch (distance) {
//				case 1:
//					int arrayNum1 = sender.getId() * 3;
//					distanceArray[arrayNum1]++;
//				case 2:
//					int arrayNum2 = sender.getId() * 3 + 1;
//					distanceArray[arrayNum2]++;
//				case 3:
//					int arrayNum3 = sender.getId() * 3 + 2;
//					distanceArray[arrayNum3]++;
//				}
//			}
//		}
//		
//		for (int i = 0; i < distanceArray.length; i++) {
//			System.out.println("distanceArray" +distanceArray[i]);
//		}
//		
//
//		double[] scoreArray = countScore(agentList, distanceArray);
//		double totalScore = 0;
//		for (int i = 0; i < scoreArray.length; i++) {
//			totalScore = scoreArray[i]++;
//		}
//		System.out.println("�l�b�g���[�N�S�̂̓��_��" + totalScore);
//	}
//
//	public static void floydAlgo(int[][] distanceArray){
//		
//	}
//	
//	public static int dijkstra(List<Agent> agentList, Agent src, Agent dst) {
//
//		int distance = 0;
//
//		src.cost = 0; // �n�_�̃R�X�g��0��
//		src.fixed = true; // �n�_�����؍ς݂�
//		List<Agent> passedList = new ArrayList<>();// �o�R�����m�[�h�̃��X�g
//		passedList.add(src);
//
//		while (true) {
//			Agent prev = passedList.get(passedList.size() - 1); // ��O�Ōo�R�����m�[�h���擾
//			int minCost = 1000000000; // �R�X�g���m�肳����܂ł̃_�~�[
//			Agent passed = null; // �o�R�m�[�h���m�肳����܂ł̃_�~�[
//			
//			for (Agent agent : agentList) {
//				System.out.println(agent.getId()+"�����؂���");
//				if (prev == dst){
//					System.out.println("�ړI�n�����Ɍo�R���Ă���");
//					break;
//				} else if (prev.isLinkTo(agent)) { // ��O�Ōo�R�����m�[�h�ƃ����N���Ă���m�[�h�̂���
//					
//					if (agent.getFixed() == false) { // �����؂̂��̂��l����
//						
//					if (agent == dst){
//						passed = agent;
//						System.out.println("���ڃ����N���Ă���");
//						break;
//					} 
//					
//						System.out.println(agent.getId() + "��" + prev.getId()
//								+ "�ƃ����N���Ă���");
//						int newCost = prev.getCost() + 1;
//						if (agent.getCost() > newCost) {
//							agent.cost = newCost; // �R�X�g�̏�������
//							// �b��I�Ȍo�R�n�Ƃ���
//							if (minCost > agent.cost) {
//								minCost = agent.cost;
//								passed = agent;
//								System.out.println(agent.getId() + "�͎b��I�Ȍo�R�n");
//							} else {
//								System.out.println(agent.getId()+"�͌o�R�n�ł͂Ȃ�");
//							}
//						}
//					}
//				} else {
//					continue;
//				}
//			}
//
//			if (passed == null) {
//				System.out.println("�o�R�n�Ȃ�");
//				break;
//			} else {
//				// �o�R�n���m�肳����
//				passed.fixed = true;
//				passedList.add(passed);
//				prev = passed;
//				System.out.println(prev.getId() + "�͌o�R�n�Ƃ��Ċm��");
//
//				if (prev.getId() == dst.getId()) {
//					distance = passedList.size() - 1;
//					System.out.println("������" + distance);
//					break;
//				}
//			}
//		}
//		return distance;
//
//	}
//
//	public static double[] countScore(List<Agent> agentList, int[] distanceArray) {
//		double delta = 0.5;
//		double linkCost = 0.1;
//
//		double[] scoreArray = new double[agentList.size()];
//		for (Agent agent : agentList) {
//			int arrayNum1 = distanceArray[agent.getId() * 3];
//			int arrayNum2 = distanceArray[agent.getId() * 3 + 1];
//			int arrayNum3 = distanceArray[agent.getId() * 3 + 2];
//			scoreArray[agent.getId()] = arrayNum1 * (delta - linkCost)
//					+ arrayNum2 * Math.pow(delta, 2) + arrayNum3
//					* Math.pow(delta, 3);
//			System.out.println(agent.getId() + "�̓��_��"
//					+ scoreArray[agent.getId()]);
//		}
//
//		return scoreArray;
//	}
//
//}
