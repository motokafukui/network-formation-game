package basic.preparation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * ��{�I�ȋ@�\�݂̂����V���v���ȃl�b�g���[�N���쐬����
 * @author tori
 *
 * @param <N> �m�[�h�̃^�C�v
 * @param <L>�@�����N�̃^�C�v
 */
public class SimpleNetwork<N extends Node, L extends Link> extends AbstractNetwork {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Set<N> nodeSet;
	Set<L> linkSet;
	
	/**
	 * Node��Collection���w�肵�č쐬
	 * @param nodeCollection �l�b�g���[�N�ɑ��݂���m�[�h
	 */
	public SimpleNetwork(Collection<N> nodeCollection) {
		nodeSet = new HashSet<N>(nodeCollection);
		linkSet = new HashSet<L>();
		for(N n:nodeCollection){
			linkSet.addAll((Set<L>)n.getLinkSet());
		}

	}

	public SimpleNetwork(Network network){
		this((Set<N>) network.getNodeSet());
	}
	
	@Override
	public Set<L> getLinkSet() {
		return linkSet;
	}

	@Override
	public Set<N> getNodeSet() {
		return nodeSet;
	}

}
