package basic.preparation;

import java.io.Serializable;

/**
 * �P���ȃm�[�h
 * @author tori
 *
 */
public class SimpleNode extends AbstractNode implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static private int baseId = 0;
	private int id = baseId++;
	
	public String toString(){
		return id+"";
	}
}
