package Ejercicio2;

import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {
    private static final int PORT = 3000;
    private static final String DIRECTORY = "conversaciones";

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
            e.printStackTrace();
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
                    Mensaje mensaje = parseJson(mensajeJson);

                    if (mensaje.getTexto().equalsIgnoreCase("adeu")) {
                        System.out.println("Chat finalizado por usuario: " + mensaje.getOrigen());
                        out.println("Chat cerrado.");
                        break;
                    }

                    String archivoConversacion = DIRECTORY + "/conversacion_" +
                            Math.min(mensaje.getOrigen(), mensaje.getDestino()) + "_" +
                            Math.max(mensaje.getOrigen(), mensaje.getDestino()) + ".txt";

                    guardarMensajeEnArchivo(archivoConversacion, mensaje);
                    String contenido = leerArchivo(archivoConversacion);

                    out.println(contenido); // Enviar la conversación completa al cliente
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void guardarMensajeEnArchivo(String archivo, Mensaje mensaje) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(archivo, true))) {
                pw.println(mensaje.getOrigen() + ": " + mensaje.getTexto());
            } catch (IOException e) {
                e.printStackTrace();
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
                e.printStackTrace();
            }
            contenido.append("---------------------------------");
            return contenido.toString();
        }

        private Mensaje parseJson(String json) {
            // Parse manual del JSON con formato {"origen":NUM, "destino":NUM, "texto":"TEXTO"}
            String[] partes = json.replace("{", "").replace("}", "").split(",");
            int origen = Integer.parseInt(partes[0].split(":")[1].trim());
            int destino = Integer.parseInt(partes[1].split(":")[1].trim());
            String texto = partes[2].split(":")[1].trim().replace("\"", "");
            return new Mensaje(origen, destino, texto);
        }
    }

    static class Mensaje {
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
}
