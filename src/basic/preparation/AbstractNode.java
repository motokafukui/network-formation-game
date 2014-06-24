package basic.preparation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractNode implements Node {

	/** �m�[�h�ɑ΂��郊���N�̃}�b�v */
	protected Map<Node, Link> relationShipMap = new HashMap<Node, Link>();

	/*
	 * (�� Javadoc)
	 * 
	 * @see jp.ac.nagoyau.is.ss.kishii.network.Node#getLink(jp.ac.nagoyau.is.ss.kishii.network.Node)
	 */
	public Link getLink(Node target) {
		if (!isLinkTo(target)) {
			return null;
		}
		return relationShipMap.get(target);
	}

	/*
	 * (�� Javadoc)
	 * 
	 * @see jp.ac.nagoyau.is.ss.kishii.network.Node#getLinkSet()
	 */
	public Set<? extends Link> getLinkSet() {
		// Set<Link> linkSet = new HashSet<Link>();
		// for (Link link : relationShipMap.values()) {
		// linkSet.add(link);
		// }
		// return linkSet;

//		return (Set<Link>) relationShipMap.values();
		return new HashSet<Link>(relationShipMap.values());
	}

	/*
	 * (�� Javadoc)
	 * 
	 * @see jp.ac.nagoyau.is.ss.kishii.network.Node#getLinkedNodeSet()
	 */
	public Set<Node> getLinkedNodeSet() {
		return relationShipMap.keySet();
	}

	/*
	 * (�� Javadoc)
	 * 
	 * @see jp.ac.nagoyau.is.ss.kishii.network.Node#isLinkTo(jp.ac.nagoyau.is.ss.kishii.network.Node)
	 */
	public boolean isLinkTo(Node target) {
		return relationShipMap.containsKey(target);
	}

	@Override
	public void removeLink(Link link) {
		relationShipMap.remove(link.otherNode(this));
	}

	@Override
	public void removeLink(Node node) {
		relationShipMap.remove(node);
		
	}
	
	@Override
	public void removeAllLink(){
		relationShipMap.clear();
	}

	/**
	 * �����N��ǉ�����<br>
	 * �����N��SimpleLink���g��
	 * @param node
	 */
	public void addLink(Node node) {
		SimpleLink link = new SimpleLink(this, node, true);
		relationShipMap.put(node, link);
	}

	/**
	 * �����N��ǉ�����
	 * @param link
	 */
	public void addLink(Link link) {
		relationShipMap.put(link.otherNode(this), link);
	}
}
