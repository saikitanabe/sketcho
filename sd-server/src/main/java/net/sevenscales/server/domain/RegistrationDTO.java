package net.sevenscales.server.domain;


//@XmlRootElement(name = "RegistrationDTO")
public class RegistrationDTO {
    @Override
    public String toString() {
      return "RegistrationDTO [activated=" + activated + ", email=" + email
          + ", id=" + id + "]";
    }

    private String id;
    private String email;
    private Boolean activated;
    
    public String getId() {
      return id;
    }
    public void setId(String id) {
      this.id = id;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public Boolean isActivated() {
      return activated;
    }

    public void setActivated(Boolean activated) {
      this.activated = activated;
    }

}
