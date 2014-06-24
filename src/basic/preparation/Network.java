package basic.preparation;

import java.util.Set;

/**
 * �l�b�g���[�N�S�̂������C���^�[�t�F�[�X
 * @author tori
 *
 */
public interface Network {

	/**
	 * �l�b�g���[�N�Ɋ܂܂�邷�ׂẴm�[�h��Ԃ�
	 * 
	 * @return
	 */
	abstract public Set<? extends Node> getNodeSet();

	/**
	 * �l�b�g���[�N�Ɋ܂܂��S�Ă�Link��Ԃ�
	 * @return
	 */
	abstract public Set<? extends Link> getLinkSet();

	/**
	 * ���̃l�b�g���[�N���[���h�ɑ��݂���m�[�h����Ԃ�
	 * @return
	 */
	public abstract int size();

}