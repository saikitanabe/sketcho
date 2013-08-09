package net.sevenscales.server.acl;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import net.sevenscales.server.domain.User;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.NotNull;


/**
 *
 * @author jvance
 */
@Entity(name="authorities")
//"CREATE TABLE AUTHORITIES(USERNAME VARCHAR_IGNORECASE(50) NOT NULL,AUTHORITY VARCHAR_IGNORECASE(50) NOT NULL,CONSTRAINT FK_AUTHORITIES_USERS FOREIGN KEY(USERNAME) REFERENCES USERS(USERNAME));");
public class Authority extends BaseObject {
    private Long id;
    private User user;
    private String authority;

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
      this.id = id;
    }

//    @Id
//    @Column(name="username", length=50, nullable=true)
    @ManyToOne
    @JoinColumn(name="username", referencedColumnName="username", nullable=true)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @NotNull
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
      this.user = user;
    }

    @Column(name="authority", length=50, nullable=true)
    @NotNull
    public String getAuthority() {
        return authority;
    }
    public void setAuthority(String authority) {
      this.authority = authority;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Authority other = (Authority) obj;
        if (this.user.getUsername() != other.user.getUsername() && 
           (this.user.getUsername() == null || !this.user.getUsername().equals(other.user.getUsername()))) {
            return false;
        }
        if (this.user.getUsername() != other.user.getUsername() && 
           (this.user.getUsername() == null || !this.user.getUsername().equals(other.user.getUsername()))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 16 * hash + (this.user.getUsername() != null ? this.user.getUsername().hashCode() : 0);
        hash = 16 * hash + (this.user.getUsername() != null ? this.user.getUsername().hashCode() : 0);
        return hash;
    }
    @Override
    public String toString(){
        return "Class: " + user.getUsername();
    }
}