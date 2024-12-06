package Ejercicio2;

// Clase Mensaje para manejar los datos del mensaje
public class Mensaje {
    private int origen;
    private int destino;
    private String texto;

    public Mensaje(int origen, int destino, String texto) {
        this.origen = origen;
        this.destino = destino;
        this.texto = texto;
    }

    public int getOrigen() {
        return origen;
    }

    public int getDestino() {
        return destino;
    }

    public String getTexto() {
        return texto;
    }
}