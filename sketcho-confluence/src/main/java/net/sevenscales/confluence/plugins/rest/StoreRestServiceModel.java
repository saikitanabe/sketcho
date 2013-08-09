package net.sevenscales.confluence.plugins.rest;

import javax.xml.bind.annotation.*;
@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class StoreRestServiceModel {

    @XmlElement(name = "value")
    private String message;

    public StoreRestServiceModel() {
    }

    public StoreRestServiceModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}