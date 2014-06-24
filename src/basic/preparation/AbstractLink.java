package basic.preparation;

import java.util.Arrays;
import java.util.List;

/**
 * �����N�̊�{�N���X�D
 * @author tori
 *
 */
public abstract class AbstractLink implements Link {

	/**
	 * ���̊֌W�Ɋ֌W����G�[�W�F���g
	 */
	protected Node[] nodes;

	/**
	 * �����N�̋���
	 */
	protected double linkPower = 1.0;

	/**
	 * �L�������N���ǂ���
	 */
	protected boolean isDirected = false;

	/**
	 * �m�[�h���w�肵�ă����N���쐬
	 * 
	 * @param nodeFrom
	 * @param nodeTo
	 */
	public AbstractLink(Node nodeFrom, Node nodeTo) {
		nodes = new Node[2];
		nodes[0] = nodeFrom;
		nodes[1] = nodeTo;
	}

	/**
	 * �m�[�h�A�����N���x���w�肵�ă����N���쐬
	 * 
	 * @param nodeFrom
	 * @param nodeTo
	 * @param power
	 */
	public AbstractLink(Node nodeFrom, Node nodeTo, double power) {
		nodes = new Node[2];
		nodes[0] = nodeFrom;
		nodes[1] = nodeTo;
		linkPower = power;
	}

	/*
	 * (�� Javadoc)
	 * 
	 * @see jp.ac.nagoyau.is.ss.kishii.network.Link#getNodeList()
	 */
	public List<Node> getNodeList() {
		return Arrays.asList(nodes);
	}

	/*
	 * (�� Javadoc)
	 * 
	 * @see jp.ac.nagoyau.is.ss.kishii.network.Link#getPower()
	 */
	public double getPower() {
		return linkPower;
	}

	/*
	 * (�� Javadoc)
	 * 
	 * @see jp.ac.nagoyau.is.ss.kishii.network.Link#isDirected()
	 */
	public boolean isDirected() {
		return isDirected;
	}

	/*
	 * (�� Javadoc)
	 * 
	 * @see jp.ac.nagoyau.is.ss.kishii.network.Link#otherNode(jp.ac.nagoyau.is.ss.kishii.network.Node)
	 */
	public Node otherNode(Node node) throws NoNodeException {
		if (nodes[0] == node) {
			return nodes[1];
		}
		if (nodes[1] == node) {
			return nodes[0];
		}
		throw new NoNodeException();
	}

	/* (�� Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if(isDirected()){
			final int prime = 31;
			int result = 1;
			result = prime * result + (isDirected ? 1231 : 1237);
			long temp;
			temp = Double.doubleToLongBits(linkPower);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			result = prime * result + Arrays.hashCode(nodes);
			return result;
		}
		else{
			final int prime = 31;
			int result = 1;
			result = prime * result + (isDirected ? 1231 : 1237);
			long temp;
			temp = Double.doubleToLongBits(linkPower);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			result = prime * result + nodes[0].hashCode()+nodes[1].hashCode();
			return result;
		}
	}

	/* (�� Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractLink other = (AbstractLink) obj;
		if (isDirected != other.isDirected)
			return false;
		if (Double.doubleToLongBits(linkPower) != Double
				.doubleToLongBits(other.linkPower))
			return false;
		if(isDirected()){
			if (!Arrays.equals(nodes, other.nodes))
				return false;
			return true;
		}
		else{
			if(nodes[0].equals(other.nodes[0]) && nodes[1].equals(other.nodes[1])){
				return true;
			}
			else if(nodes[0].equals(other.nodes[1]) && nodes[1].equals(other.nodes[0])){
				return true;
			}
			else{
				return false;
			}
		}
	}

	
	
}
