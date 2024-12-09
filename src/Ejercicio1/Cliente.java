package Ejercicio1;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 2000;

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Conectado al servidor.");
            String comando = "";
            while (!comando.equals("exit")) {
                System.out.print("Elije un comando: insert, select, delete o exit\n");
                comando = scanner.nextLine().toLowerCase();

                switch (comando) {
                    case "insert" -> {
                        // Pido los datos
                        System.out.print("ID: ");
                        String id = scanner.nextLine();
                        System.out.print("Nombre: ");
                        String nombre = scanner.nextLine();
                        System.out.print("Apellidos: ");
                        String apellido = scanner.nextLine();

                        // Envio el comando junto con los datos al servidor
                        out.println("insert," + id + "," + nombre + "," + apellido);
                        leerServidor(in);
                    }
                    case "select" -> {
                        System.out.print("ID: ");
                        String id = scanner.nextLine();

                        out.println("select," + id);
                        leerServidor(in);
                    }
                    case "delete" -> {
                        System.out.print("ID: ");
                        String id = scanner.nextLine();

                        out.println("delete," + id);
                        leerServidor(in);
                    }
                    case "exit" -> {
                        out.println("exit");
                        System.out.println("Cerrando programa...");
                    }
                    default -> System.out.println("Comando no reconocido.");
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // Leer respuesta del servidor
    private static void leerServidor(BufferedReader in) {
        try {
            String respuesta = in.readLine(); // Leer respuesta
            System.out.println("Respuesta del servidor: " + respuesta);
        } catch (IOException e) {
            System.out.println("Error al leer el servidor.");
        }
    }
}
