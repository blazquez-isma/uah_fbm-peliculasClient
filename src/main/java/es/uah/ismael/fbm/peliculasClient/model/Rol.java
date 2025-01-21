package es.uah.ismael.fbm.peliculasClient.model;

public class Rol {

    private Integer idRol;
    private String authority;

    public Rol() {
    }

    public Rol(String idRolAndName){
        if(idRolAndName != null && !idRolAndName.isEmpty()){
            String[] fieldPositions = idRolAndName.split("-");
            this.idRol = Integer.parseInt(fieldPositions[0]);
            this.authority = fieldPositions[1];
        }
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String toString() {
        return idRol+"-"+this.authority;
    }

}
