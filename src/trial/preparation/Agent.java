package trial.preparation;

import basic.preparation.AbstractNode;

public class Agent extends AbstractNode implements Cloneable {

	private static int base_id = 0;
	private int id;

	double expectation;

	private double infoValue;

	/**
	 * ���_
	 */
	double score;

	/**
	 * �R���X�g���N�^
	 */
	public Agent() {
		setId(base_id++);
		expectation = 0.0;
		score = 0;
		setInfoValue(0.0);
	}

	/**
	 * �w�肵���G�[�W�F���g��Id��Ԃ�
	 */
	public int getId() {
		return id;
	}

	public double getExpect() {
		return expectation;
	}

	/**
	 * �w�肵���G�[�W�F���g�̓��_��Ԃ�
	 */
	public double getScore() {
		return score;
	}

	public double getInfoValue() {
		return infoValue;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setInfoValue(double infoValue) {
		this.infoValue = infoValue;
	}

}
