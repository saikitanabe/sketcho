package net.sevenscales.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import net.sevenscales.domain.api.IRelationshipItem;

@Entity(name="relationship_item")
public class RelationshipItem extends DiagramItem implements IRelationshipItem {
  private Integer startx;
	private Integer starty;
	private Integer endx;
	private Integer endy;
	private Integer capabilities;

  @Column
	public Integer getStartx() {
		return startx;
	}
	public void setStartx(Integer startx) {
		this.startx = startx;
	}

  @Column
	public Integer getStarty() {
		return starty;
	}
	public void setStarty(Integer starty) {
		this.starty = starty;
	}

	@Column
	public Integer getEndx() {
		return endx;
	}
	public void setEndx(Integer endx) {
		this.endx = endx;
	}
	
  @Column
  public Integer getEndy() {
		return endy;
	}
	public void setEndy(Integer endy) {
		this.endy = endy;
	}
	
  @Column
  public Integer getCapabilities() {
		return capabilities;
	}
	public void setCapabilities(Integer capabilities) {
		this.capabilities = capabilities;
	}
}
