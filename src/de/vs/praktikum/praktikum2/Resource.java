package de.vs.praktikum.praktikum2;

import java.io.Serializable;

/**
 * Created by Yang Mao on 5/24/18.
 * email: yang.mao@stud.hs-emden-leer.de
 */
public class Resource implements Serializable {
    private static final long serialVersionUID = 7526471155622776147L;
    private String id;
    public void setId(String id) {
        this.id = id;
    }

    public Resource(String id){
        this.id = id;
    }
    public String getId(){
        return id;
    }
}
