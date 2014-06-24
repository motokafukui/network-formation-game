package basic.preparation;

import java.io.Serializable;

/**
 * �P���ȃ����N
 * @author tori
 *
 */
public class SimpleLink extends AbstractLink implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * �V���v���Ȗ��������N���쐬����
	 * @param nodeFrom
	 * @param nodeTo
	 */
	public SimpleLink(Node nodeFrom, Node nodeTo) {
		super(nodeFrom, nodeTo);
		isDirected = false;
	}
	
	/**
	 * �������ǂ������w�肵�ă����N���쐬����
	 * @param nodeFrom
	 * @param nodeTo
	 * @param isDirected
	 */
	public SimpleLink(Node nodeFrom, Node nodeTo, boolean isDirected) {
		super(nodeFrom, nodeTo);
		this.isDirected = isDirected;
	}
}