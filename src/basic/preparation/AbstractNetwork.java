package basic.preparation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import util.DegreeCorrelationMap;
import util.DegreeDistributionMap;
import util.EnumNetworkIndex;
import util.NetworkAnalyzer;

/**
 * �l�b�g���[�N�S�̂������A�u�X�g���N�g�N���X<br>
 * ���̃N���X���p�������l�b�g���[�N�S�̂������N���X���쐬����
 * 
 * @see Node
 * @see Link
 * @see NetworkFieldFrame
 * 
 * @author tori
 * 
 */
abstract public class AbstractNetwork implements Serializable, Network {
	/*
	 * (�� Javadoc)
	 * 
	 * @see jp.ac.nagoyau.is.ss.kishii.network.Network#getNodeSet()
	 */
	abstract public Set<? extends Node> getNodeSet();

	/*
	 * (�� Javadoc)
	 * 
	 * @see jp.ac.nagoyau.is.ss.kishii.network.Network#getLinkSet()
	 */
	abstract public Set<? extends Link> getLinkSet();

	/*
	 * (�� Javadoc)
	 * 
	 * @see jp.ac.nagoyau.is.ss.kishii.network.Network#size()
	 */
	public int size() {
		return getNodeSet().size();
	}

	/**
	 * ����NetworkWorld�S�̂̃N���X�^�v����Ԃ�
	 * 
	 * @return �N���X�^�W��
	 */
	public double getClusterValue() {
		double clusterValue = 0.0;
		for (Node from : getNodeSet()) {
			clusterValue += getClusterValue(from);
		}

		return clusterValue / getNodeSet().size();
	}

	/**
	 * �m�[�h�Ɋւ��ăN���X�^�W����Ԃ�
	 * 
	 * @param node �^�[�Q�b�g�ƂȂ�m�[�h
	 * @return �N���X�^�W��
	 */
	static public double getClusterValue(Node node) {
		Set<? extends Node> targetSet = node.getLinkedNodeSet();

		int linklink = 0;
		int all = 0;
		for (Node target : targetSet) {
			for (Node target2 : targetSet) {
				if (target == target2) {
					continue;
				}
				if (target.isLinkTo(target2)) {
					linklink++;
				}
				all++;
			}
		}
		if (all == 0) {
			return 0;
		}
		return (double) linklink / all;
	}

	/**
	 * ����m�[�h����ʂ̃m�[�h�܂ł̋��������߂�
	 * 
	 * @param node1 �n�_�m�[�h
	 * @param node2 �I�_�m�[�h
	 * @return �m�[�h�Ԃ̋����D�ڑ����Ă��Ȃ��ꍇ��-1��Ԃ�
	 */
	public int getPathLength(Node node1, Node node2) {
		if (node1 == node2) {
			return 0;
		}
		Set<Node> nodeSet = new HashSet<Node>();
		nodeSet.add(node1);
		int length = 0;
		while (true) {
			int lastSize = nodeSet.size();
			length++;
			Set<Node> linkedUserSet = new HashSet<Node>();
			for (Node n : nodeSet) {
				linkedUserSet.addAll(n.getLinkedNodeSet());
			}
			nodeSet.addAll(linkedUserSet);
			if (nodeSet.contains(node2)) {
				return length;
			} else if (nodeSet.size() == lastSize) {
				return -1;
			}
		}
	}

	/**
	 * �w�肵���m�[�h���瑼�̃m�[�h�ւ̕��ϋ����ł���ߐڒ��S����Ԃ�<br>
	 * ����Network���ɑ��݂��Ȃ��m�[�h�ɂ��Ă͍l�����Ȃ�
	 * @param node �n�_�m�[�h
	 * @return ����m�[�h�̋ߐڒ��S��
	 */
	public double getClosenessCentrality(Node node) {
		Set<Node> nodeSet = new HashSet<Node>();
		nodeSet.add(node);
		double totalLength = 0;
		int length = 0;
		int lastSize = 1;
		do {
			lastSize = nodeSet.size();
			length++;
			Set<Node> linkedNodeSet = new HashSet<Node>();
			for (Node n : nodeSet) {
				linkedNodeSet.addAll(n.getLinkedNodeSet());
			}
			//����Network���ɑ��݂��Ȃ��m�[�h�ɂ��Ă͍l�����Ȃ�
			linkedNodeSet.retainAll(getNodeSet());
			//�V�����擾�ł����S�Ẵm�[�h��ǉ�
			nodeSet.addAll(linkedNodeSet);
			int num = nodeSet.size() - lastSize;
			totalLength += length * num;
		} while (lastSize != nodeSet.size());
		return totalLength / (nodeSet.size()-1);
	}

	/**
	 * ���σp�X�����擾
	 * 
	 * @return �p�X��
	 */
	public double getAveragePathLength() {
		double averagePathLength = 0;
		for (Node node : getNodeSet()) {
			averagePathLength += getClosenessCentrality(node);
		}
		double length = averagePathLength / size();
		if(length < 1.0 && length > 0.0){
			/*
			averagePathLength = 0;
			for (Node node : getNodeSet()) {
				averagePathLength += getClosenessCentrality(node);
			}
			*/
			
			throw new RuntimeException("Length < 1.0!?");
		}
		return length;
	}

	/**
	 * �l�b�g���[�N��Assortativity(0 �� assortativity �� 1)��Ԃ�<br>
	 * �uAssortative mixing in networks�v Phys. Rev. Lett. 89, 208701 (2002)<br>
	 * �uMixing patterns in networks�v Phys. Rev. E 67, 026126 (2003)
	 * 
	 * @return Assortativity
	 */
	public double getAssortativity() {
		double r1;
		double r2;
		double r3;

		List<Link> allLinkList = new ArrayList<Link>(getLinkSet());
		double allLinkNum = allLinkList.size();

		double r1temp = 0;
		for (Link link : allLinkList) {
			List<? extends Node> nodes = link.getNodeList();
			r1temp += nodes.get(0).getLinkSet().size() * nodes.get(1).getLinkSet().size();
		}
		r1 = r1temp / allLinkNum;

		double r2temp = 0;
		for (Link link : allLinkList) {
			List<? extends Node> nodes = link.getNodeList();
			r2temp += (nodes.get(0).getLinkSet().size() + nodes.get(1).getLinkSet().size());
		}
		r2 = Math.pow(r2temp * 0.5 / allLinkNum, 2);

		double r3temp = 0;
		for (Link link : allLinkList) {
			List<? extends Node> nodes = link.getNodeList();
			r3temp += Math.pow(nodes.get(0).getLinkSet().size(), 2) + Math.pow(nodes.get(1).getLinkSet().size(), 2);
		}
		r3 = r3temp * 0.5 / allLinkNum;
//		r3 = r3temp / allLinkNum;

		if(r3-r2 == 0){
			return 0;
		}
		return (r1 - r2) / (r3 - r2);
	}

	/**
	 * ���݃����N����Ԃ�
	 * 
	 * @return ���݃����N��
	 */
	public double getInterLinkRate() {
		List<Node> nodes = new ArrayList<Node>(getNodeSet());
		double interLinkNum = 0.0;
		int allLinkNum = 0;
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);

			for (Link link : node.getLinkSet()) {
				Node targetNode = link.otherNode(node);
				allLinkNum++;
				if (targetNode.isLinkTo(node)) {
					interLinkNum++;
				}
			}
		}
		return interLinkNum / allLinkNum;
	}

	/**
	 * target�������N����Ă��鐔��Ԃ�
	 * 
	 * @param target
	 * @return
	 */
	public int getLinkedNum(Node target) {
		int num = 0;
		for (Node node : getNodeSet()) {
			if (node.isLinkTo(target)) {
				num++;
			}
		}
		return num;
	}
	
	/**
	 * ���̃l�b�g���[�N�Ɋւ��āC
	 * �������z�N���X�ł���DegreeDistributionMap��Ԃ�
	 * @return ���̃l�b�g���[�N��DegreeDistributionMap
	 */
	public DegreeDistributionMap getDegreeDistributionMap(){
		return new DegreeDistributionMap(this);
	}
	
	/**
	 * �����l�b�g���[�N��Ԃ�
	 * @return
	 */
	public Set<SimpleNetwork> getNetworkComponentSet(){
		Set<SimpleNetwork> networkSet = new TreeSet<SimpleNetwork>(new Comparator<Network>() {

			@Override
			public int compare(Network arg0, Network arg1) {
				if(arg0.size() != arg1.size()){
					return arg1.size()-arg0.size();
				}
				else{
					return arg0.hashCode()>arg1.hashCode()?1:-1;
				}
			}
			
		});
		Set<Node> usedNodeSet = new HashSet<Node>();
		Set<Node> remainNodeSet = new HashSet<Node>(getNodeSet());
		while(!remainNodeSet.isEmpty()){
			//System.out.println("Remain:"+remainNodeSet.size());
			Node node = remainNodeSet.iterator().next();
			Set<Node> enableNodeSet = getEnableNodeSet(node);
			networkSet.add(new SimpleNetwork<Node, Link>(enableNodeSet));
			remainNodeSet.removeAll(enableNodeSet);
		}
		
		return networkSet;
	}
	
	/**
	 * �ő�R���|�[�l���g��Ԃ�
	 * @return
	 */
	public SimpleNetwork getLargestComponentNetwork() {
		
		SimpleNetwork<Node, Link> network = null;
		for(SimpleNetwork<Node, Link> network2:getNetworkComponentSet()){
			if(network == null || network.size() < network2.size()){
				network = network2;
			}
		}
		if(network == null){
			return new SimpleNetwork<Node, Link>((Collection<Node>) getNodeSet());
		}
		else{
			return network;
		}
	}


	
	private Set<Node> getEnableNodeSet(Node baseNode) {
		Set<Node> nodeSet = new HashSet<Node>();
		Set<Node> lastSet = new HashSet<Node>();
		lastSet.add(baseNode);
		while(true){
			Set<Node> nextSet = new HashSet<Node>();
			for(Node node:lastSet){
				Set<Node> nodeLinkedNodeSet = (Set<Node>)node.getLinkedNodeSet();
				nextSet.addAll(nodeLinkedNodeSet);
			}
			nextSet.removeAll(nodeSet);
			nextSet.removeAll(lastSet);
			nodeSet.addAll(lastSet);
			lastSet = nextSet;
			if(nextSet.isEmpty()){
				break;
			}
			//System.out.println("lastSet.size()="+lastSet.size());
		}
		
		return nodeSet;
	}

	/**
	 * Sociarium�`���ŏo�͂���
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public void outputSociaruim(String fileName)throws IOException{
		FileWriter fw = null;
		BufferedWriter bw = null;
		try{
			fw = new FileWriter(fileName);
			bw = new BufferedWriter(fw);
			bw.append("#TimeSeries\n#Delimiter,\n");
			for(Link link: getLinkSet()){
				bw.append(String.format("%d,%s,%s,%.0f\n", 0, link.getNodeList().get(0).toString(), link.getNodeList().get(1).toString(), link.getPower()));
			}
		}finally{
			bw.close();
			fw.close();
		}
	}

	/**
	 * Sociarium�`���ŏo�͂���
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public void outputSequenceSociaruim(String fileName)throws IOException{
		FileWriter fw = null;
		BufferedWriter bw = null;
		try{
			fw = new FileWriter(fileName);
			bw = new BufferedWriter(fw);
			bw.append("#TimeSeries\n#Delimiter,\n");
			/*
			for(Link link: getLinkSet()){
				bw.append(String.format("%d,%s,%s,%.0f\n", 0, link.getNodeList().get(0).toString(), link.getNodeList().get(1).toString(), link.getPower()));
			}
			*/
			bw.append(getSociariumLinkData(0));
		}finally{
			if(bw != null){
				bw.close();
			}
			if(fw != null){
				fw.close();
			}
		}
	}

	public String getSociariumLinkData(long timeLine){
		StringBuffer buf = new StringBuffer();
		for(Link link: getLinkSet()){
			buf.append(String.format("%d,%s,%s,%.0f\n", timeLine, link.getNodeList().get(0).toString(), link.getNodeList().get(1).toString(), link.getPower()));
		}
		return buf.toString();
	}
	
}
