package sernet.gs.reveng;

// Generated Jun 5, 2015 1:28:30 PM by Hibernate Tools 3.4.0.CR1

/**
 * MbStatusTxtId generated by hbm2java
 */
public class MbStatusTxtId implements java.io.Serializable {

	private int staImpId;
	private int staId;
	private short sprId;

	public MbStatusTxtId() {
	}

	public MbStatusTxtId(int staImpId, int staId, short sprId) {
		this.staImpId = staImpId;
		this.staId = staId;
		this.sprId = sprId;
	}

	public int getStaImpId() {
		return this.staImpId;
	}

	public void setStaImpId(int staImpId) {
		this.staImpId = staImpId;
	}

	public int getStaId() {
		return this.staId;
	}

	public void setStaId(int staId) {
		this.staId = staId;
	}

	public short getSprId() {
		return this.sprId;
	}

	public void setSprId(short sprId) {
		this.sprId = sprId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof MbStatusTxtId))
			return false;
		MbStatusTxtId castOther = (MbStatusTxtId) other;

		return (this.getStaImpId() == castOther.getStaImpId())
				&& (this.getStaId() == castOther.getStaId())
				&& (this.getSprId() == castOther.getSprId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getStaImpId();
		result = 37 * result + this.getStaId();
		result = 37 * result + this.getSprId();
		return result;
	}

}