package Ejercicio2;

import com.google.gson.Gson; // Importar Gson
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        final String HOST = "localhost";
        final int PORT = 3000;
        final Gson gson = new Gson(); // Instanciar Gson

        // Formatear la fecha y hora
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner sc = new Scanner(System.in)) {

            System.out.println("Indica tu número:");
            while (!sc.hasNextInt()) {
                System.out.println("Escribe un numero entero:");
                sc.nextLine();
            }
            int numOrigen = sc.nextInt();
            sc.nextLine();

            System.out.println("Indica el número del receptor:");
            while (!sc.hasNextInt()) {
                System.out.println("Escribe un numero entero:");
                sc.nextLine();
            }
            int numDestino = sc.nextInt();
            sc.nextLine();

            while (true) {
                System.out.println("Escribe el mensaje a enviar:");
                String texto = sc.nextLine();

                // Crear un objeto Mensaje
                Mensaje mensaje = new Mensaje(numOrigen, numDestino, texto + " -- " + LocalDateTime.now().format(formateador));

                // Convertir el mensaje a JSON y enviarlo al servidor
                String mensajeJson = gson.toJson(mensaje);
                out.println(mensajeJson);

                if (texto.equalsIgnoreCase("adeu") || texto.equalsIgnoreCase("adios")) {
                    System.out.println("Chat finalizado.");
                    break;
                }

                // Leer todas las líneas enviadas por el servidor
                StringBuilder respuesta = new StringBuilder();
                String linea;

                while ((linea = in.readLine()) != null) {
                    if (linea.equals(";;;")) break; // Delimitador de fin de mensaje
                    respuesta.append(linea).append("\n");
                }

                // Mostrar la conversación completa
                System.out.println("Contenido actual de la conversación:");
                System.out.println(respuesta);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
