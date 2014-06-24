package basic.preparation;

import java.util.Set;

/**
 * �l�b�g���[�N��̃m�[�h�������C���^�[�t�F�[�X
 * @author tori
 *
 */
public interface Node{

	/**
	 * ���̃m�[�h�Ɛڑ����Ă���S�Ẵm�[�h��Ԃ�
	 * @return ���̃m�[�h�Ɛڑ����Ă���S�Ẵm�[�h
	 */
	public Set<? extends Node> getLinkedNodeSet(); 
	
    /**
     * ���̃m�[�h�������ׂẴ����N���擾
     * @return ���̃m�[�h�������ׂẴ����N
     */
    public Set<? extends Link> getLinkSet();
    
	/**
	 * �Ώۃm�[�h�ւ̃����N��Ԃ�
	 * @param target �Ώۂ̃m-�h
	 * @return �Ώۂւ̃����N<br>
	 * ���݂��Ȃ����null��Ԃ�
	 */
	public Link getLink(Node target);

	/**
	 * ����m�[�h�ƃ����N���Ă��邩�ǂ�����Ԃ�
	 * @param target �����N���Ă��邩�ǂ�����Ԃ�
	 * @return �����N���Ă����true
	 */
	public boolean isLinkTo(Node target);
	
	/**
	 * �ΏۂƂȂ�m�[�h�̃����N���폜����
	 * @param node
	 */
	public void removeLink(Node node);
	
	/**
	 * �ΏۂƂȂ郊���N���폜����
	 * @param link
	 */
	public void removeLink(Link link);
	
	/**
	 * �S�����N���폜����
	 */
	public void removeAllLink();
	
	/**
	 * �w�肵���m�[�h�ւ̃����N��ǉ�����<br>
	 * @param node
	 */
	public void addLink(Node node);
	
	/**
	 * �w�肵�������N��ǉ�����
	 * @param link
	 */
	public void addLink(Link link);
}
