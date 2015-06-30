package sernet.gs.reveng;

// Generated Jun 5, 2015 1:28:30 PM by Hibernate Tools 3.4.0.CR1

import java.util.Date;

/**
 * StgMbDelBaustId generated by hbm2java
 */
public class StgMbDelBaustId implements java.io.Serializable {

	private Integer bauId;
	private Integer bauImpId;
	private Integer impId;
	private Date timestamp;
	private String guid;
	private String geloeschtDurch;

	public StgMbDelBaustId() {
	}

	public StgMbDelBaustId(Integer bauId, Integer bauImpId, Integer impId,
			Date timestamp, String guid, String geloeschtDurch) {
		this.bauId = bauId;
		this.bauImpId = bauImpId;
		this.impId = impId;
		this.timestamp = timestamp;
		this.guid = guid;
		this.geloeschtDurch = geloeschtDurch;
	}

	public Integer getBauId() {
		return this.bauId;
	}

	public void setBauId(Integer bauId) {
		this.bauId = bauId;
	}

	public Integer getBauImpId() {
		return this.bauImpId;
	}

	public void setBauImpId(Integer bauImpId) {
		this.bauImpId = bauImpId;
	}

	public Integer getImpId() {
		return this.impId;
	}

	public void setImpId(Integer impId) {
		this.impId = impId;
	}

	public Date getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getGuid() {
		return this.guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getGeloeschtDurch() {
		return this.geloeschtDurch;
	}

	public void setGeloeschtDurch(String geloeschtDurch) {
		this.geloeschtDurch = geloeschtDurch;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof StgMbDelBaustId))
			return false;
		StgMbDelBaustId castOther = (StgMbDelBaustId) other;

		return ((this.getBauId() == castOther.getBauId()) || (this.getBauId() != null
				&& castOther.getBauId() != null && this.getBauId().equals(
				castOther.getBauId())))
				&& ((this.getBauImpId() == castOther.getBauImpId()) || (this
						.getBauImpId() != null
						&& castOther.getBauImpId() != null && this
						.getBauImpId().equals(castOther.getBauImpId())))
				&& ((this.getImpId() == castOther.getImpId()) || (this
						.getImpId() != null && castOther.getImpId() != null && this
						.getImpId().equals(castOther.getImpId())))
				&& ((this.getTimestamp() == castOther.getTimestamp()) || (this
						.getTimestamp() != null
						&& castOther.getTimestamp() != null && this
						.getTimestamp().equals(castOther.getTimestamp())))
				&& ((this.getGuid() == castOther.getGuid()) || (this.getGuid() != null
						&& castOther.getGuid() != null && this.getGuid()
						.equals(castOther.getGuid())))
				&& ((this.getGeloeschtDurch() == castOther.getGeloeschtDurch()) || (this
						.getGeloeschtDurch() != null
						&& castOther.getGeloeschtDurch() != null && this
						.getGeloeschtDurch().equals(
								castOther.getGeloeschtDurch())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getBauId() == null ? 0 : this.getBauId().hashCode());
		result = 37 * result
				+ (getBauImpId() == null ? 0 : this.getBauImpId().hashCode());
		result = 37 * result
				+ (getImpId() == null ? 0 : this.getImpId().hashCode());
		result = 37 * result
				+ (getTimestamp() == null ? 0 : this.getTimestamp().hashCode());
		result = 37 * result
				+ (getGuid() == null ? 0 : this.getGuid().hashCode());
		result = 37
				* result
				+ (getGeloeschtDurch() == null ? 0 : this.getGeloeschtDurch()
						.hashCode());
		return result;
	}

}