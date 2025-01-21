package es.uah.ismael.fbm.peliculasClient.model;

import java.time.LocalDate;

public class CriticaPelicula extends Critica {

        private String tituloPelicula;

        public CriticaPelicula() {
        }

        public CriticaPelicula(String tituloPelicula, Critica critica) {
            super(critica.getIdCritica(), critica.getIdPelicula(), critica.getValoracion(), critica.getNota(), critica.getFecha(), critica.getUsuario());
            this.tituloPelicula = tituloPelicula;
        }

        public CriticaPelicula(String tituloPelicula, Integer idCritica, Integer idPelicula, String valoracion, Integer nota, LocalDate fecha, Usuario usuario) {
            super(idCritica, idPelicula, valoracion, nota, fecha, usuario);
            this.tituloPelicula = tituloPelicula;
        }

        public String getTituloPelicula() {
            return tituloPelicula;
        }

        public void setTituloPelicula(String tituloPelicula) {
            this.tituloPelicula = tituloPelicula;
        }

        @Override
        public String toString() {
            return "Critica{" +
                    "idCritica=" + super.getIdCritica() +
                    ", idPelicula=" + super.getIdPelicula() +
                    ", tituloPelicula='" + tituloPelicula +
                    ", valoracion='" + super.getValoracion() + '\'' +
                    ", nota=" + super.getNota() +
                    ", fecha=" + super.getFecha() +
                    ", usuarioId=" + super.getUsuario().getIdUsuario() +
                    '}';
        }
}
