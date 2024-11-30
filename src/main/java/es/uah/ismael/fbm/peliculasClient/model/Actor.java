package es.uah.ismael.fbm.peliculasClient.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Actor {

    private  Integer idActor;
    private String nombre;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;
    private String paisNacimiento;
    private List<Pelicula> peliculas;

    public Actor() {
    }

    public Actor(Integer idActor, String nombre, LocalDate fechaNacimiento, String paisNacimiento, List<Pelicula> peliculas) {
        this.idActor = idActor;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.paisNacimiento = paisNacimiento;
        this.peliculas = peliculas;
    }

    public Actor(String nombre, LocalDate fechaNacimiento, String paisNacimiento) {
        this.idActor = 0;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.paisNacimiento = paisNacimiento;
        this.peliculas = new ArrayList<>();
    }

    public Integer getIdActor() {
        return idActor;
    }

    public void setIdActor(Integer idActor) {
        this.idActor = idActor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getPaisNacimiento() {
        return paisNacimiento;
    }

    public void setPaisNacimiento(String paisNacimiento) {
        this.paisNacimiento = paisNacimiento;
    }

    public List<Pelicula> getPeliculas() {
        return peliculas;
    }

    public void setPeliculas(List<Pelicula> peliculas) {
        this.peliculas = peliculas;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "idActor=" + idActor +
                ", nombre='" + nombre + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", paisNacimiento='" + paisNacimiento + '\'' +
                ", peliculas=" + peliculas +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Actor actor = (Actor) o;
        return Objects.equals(idActor, actor.idActor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idActor);
    }
}
