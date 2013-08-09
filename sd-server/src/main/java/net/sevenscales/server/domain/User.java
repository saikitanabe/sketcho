package net.sevenscales.server.domain;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import net.sevenscales.domain.api.IUser;
import net.sevenscales.server.acl.BaseObject;

import org.hibernate.validator.NotNull;


/**
 *
 * @author jvance
 */
@Entity(name="users")
//"CREATE TABLE USERS(USERNAME VARCHAR_IGNORECASE(50) NOT NULL PRIMARY KEY,PASSWORD VARCHAR_IGNORECASE(50) NOT NULL,ENABLED BOOLEAN NOT NULL);");
public class User extends BaseObject implements IUser {
    private Long id;
    private String username;
    private String nickName;
    private String password;
    private Boolean enabled;

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
      this.id = id;
    }

    @Column(name="username", length=50, nullable=true, unique=true)
    @NotNull
    public String getUsername() {
        return username;
    }
    public void setUsername(String userName) {
      this.username = userName;
    }

    @Column(name="password", length=50, nullable=true)
    @NotNull
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
      this.password = password;
    }
    
    @Column(name="enabled", nullable=true)
    @NotNull
    public Boolean getEnabled() {
      return enabled;
    }
    public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.username != other.username && (this.username == null || !this.username.equals(other.username))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 14 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 14 * hash + (this.username != null ? this.username.hashCode() : 0);
        return hash;
    }
    @Override
    public String toString(){
        return "Class: " + username;
    }
}