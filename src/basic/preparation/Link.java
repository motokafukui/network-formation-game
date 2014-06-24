package basic.preparation;

import java.util.List;

/**
 * 
 * �l�b�g���[�N�ɂ�����m�[�h�Ԃ̂Ȃ��������킷interface
 * 
 * @author tori
 *
 */
public interface Link {
	/**
	 * �n�_�I�_
	 * @return �n�_
	 */
	public List<? extends Node> getNodeList();

	/**
	 * �L���O���t���ǂ���
	 * @return false
	 */
	public boolean isDirected();
	
	/**
	 * ���̃����N�̋���
	 * @return �����N�̋���
	 */
	public double getPower();

	/**
	 * �����Е��̃����N
	 * @param node
	 * @return ���̃����N�����C�����Е��̃����N
	 * @throws NoNodeException �w�肵���m�[�h�������N�Ɋ܂܂�Ă��Ȃ�
	 */
	public Node otherNode(Node node)throws NoNodeException;
	
}
