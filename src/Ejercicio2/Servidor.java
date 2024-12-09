package Ejercicio2;

import com.google.gson.Gson;
import java.io.*;
import java.net.*;

public class Servidor {
    private static final int PORT = 3000;
    private static final String DIRECTORY = "conversaciones";
    private static final Gson gson = new Gson(); // Instanciar Gson

    public static void main(String[] args) {
        File dir = new File(DIRECTORY);
        if (!dir.exists()) dir.mkdir(); // Crear el directorio de conversaciones si no existe

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor de chat en espera de conexiones...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado.");
                new Thread(new ManejadorCliente(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static class ManejadorCliente implements Runnable {
        private final Socket socket;

        public ManejadorCliente(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String mensajeJson;

                while ((mensajeJson = in.readLine()) != null) {
                    // Convertir JSON a objeto Mensaje usando Gson
                    Mensaje mensaje = gson.fromJson(mensajeJson, Mensaje.class);

                    if (mensaje.getTexto().equalsIgnoreCase("adios")||mensaje.getTexto().equalsIgnoreCase("adeu")) {
                        System.out.println("Chat finalizado por usuario: " + mensaje.getOrigen());
                    }

                    String archivoConversacion = DIRECTORY + "/conversacion_" +
                            Math.min(mensaje.getOrigen(), mensaje.getDestino()) + "_" +
                            Math.max(mensaje.getOrigen(), mensaje.getDestino()) + ".txt";

                    guardarMensajeEnArchivo(archivoConversacion, mensaje);

                    // Leer el archivo de la conversación completa y enviarlo al cliente
                    String contenido = leerArchivo(archivoConversacion);
                    out.println(contenido);
                    out.flush();
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }

        private void guardarMensajeEnArchivo(String archivo, Mensaje mensaje) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(archivo, true))) {
                pw.println(mensaje.getOrigen() + ": " + mensaje.getTexto());
                pw.flush();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        private String leerArchivo(String archivo) {
            StringBuilder contenido = new StringBuilder("---------------------------------\n");
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    contenido.append(linea).append("\n");
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
            contenido.append("---------------------------------");
            contenido.append("\n;;;"); // Delimitador al final
            return contenido.toString();
        }
    }
}
