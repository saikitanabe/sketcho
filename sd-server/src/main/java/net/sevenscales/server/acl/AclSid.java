package net.sevenscales.server.acl;import java.util.List;import javax.persistence.CascadeType;import javax.persistence.Column;import javax.persistence.Entity;import javax.persistence.FetchType;import javax.persistence.GeneratedValue;import javax.persistence.GenerationType;import javax.persistence.Id;import javax.persistence.JoinColumn;import javax.persistence.OneToMany;/** * * @author jvance */@Entity(name="acl_sid")public class AclSid extends BaseObject{    private Long id;    private Boolean principal;    private String sid;    private List<AclObjectIdentity> objectIdentities;    private List<AclEntry> aclEntries;    @Override    public String toString() {        return("ID: " + id + " SID: " + sid);    }    @Id @GeneratedValue(strategy = GenerationType.AUTO)    @Column(name="id")    public Long getId() {        return id;    }    public void setId(Long id) {        this.id = id;    }    @Column(name="principal", nullable=false)    public Boolean getPrincipal() {        return principal;    }    public void setPrincipal(Boolean principal) {        this.principal = principal;    }    @Column(name="sid", length=256, nullable=false)    public String getSid() {        return sid;    }    public void setSid(String sid) {        this.sid = sid;    }    @OneToMany(        cascade = {CascadeType.ALL},        fetch = FetchType.LAZY    )    @JoinColumn(name="sid")    public List<AclEntry> getAclEntries() {        return aclEntries;    }    public void setAclEntries(List<AclEntry> aclEntries) {        this.aclEntries = aclEntries;    }    @OneToMany(        cascade = {CascadeType.ALL},        fetch = FetchType.LAZY    )    @JoinColumn(name="owner_sid")    public List<AclObjectIdentity> getObjectIdentities() {        return objectIdentities;    }    public void setObjectIdentities(List<AclObjectIdentity> objectIdentities) {        this.objectIdentities = objectIdentities;    }    @Override    public boolean equals(Object obj) {        if (obj == null) {            return false;        }        if (getClass() != obj.getClass()) {            return false;        }        final AclSid other = (AclSid) obj;        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {            return false;        }        if (this.principal != other.principal && (this.principal == null || !this.principal.equals(other.principal))) {            return false;        }        if (this.sid != other.sid && (this.sid == null || !this.sid.equals(other.sid))) {            return false;        }        return true;    }    @Override    public int hashCode() {        int hash = 7;        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);        hash = 17 * hash + (this.principal != null ? this.principal.hashCode() : 0);        hash = 17 * hash + (this.sid != null ? this.sid.hashCode() : 0);        return hash;    }}