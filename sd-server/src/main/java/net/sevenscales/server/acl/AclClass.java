package net.sevenscales.server.acl;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;


/**
 *
 * @author jvance
 */
@Entity(name="acl_class")
public class AclClass extends BaseObject {
    private Long id;
    private String aclClass; //class of course is a reserved word!
    private List<AclObjectIdentity> AclObjectIdentities;

    @Column(name="class", length=256, nullable=true)
    public String getAclClass() {
        return aclClass;
    }

    public void setAclClass(String aclClass) {
        this.aclClass = aclClass;
    }

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToMany(
        cascade = {CascadeType.ALL},
        fetch = FetchType.EAGER
    )
    @JoinColumn(name="object_id_class")
    @OrderBy("objectIdentity")
    public List<AclObjectIdentity> getAclObjectIdentities() {
        return AclObjectIdentities;
    }

    public void setAclObjectIdentities(List<AclObjectIdentity> AclObjectIdentities) {
        this.AclObjectIdentities = AclObjectIdentities;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AclClass other = (AclClass) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.aclClass != other.aclClass && (this.aclClass == null || !this.aclClass.equals(other.aclClass))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 59 * hash + (this.aclClass != null ? this.aclClass.hashCode() : 0);
        return hash;
    }
    @Override
    public String toString(){
        return "Class: " + aclClass;
    }
}