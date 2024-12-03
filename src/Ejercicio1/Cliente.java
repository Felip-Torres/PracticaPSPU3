package Ejercicio1;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 2000;

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Conectado al servidor.");
            String comando="";
            while (!comando.equals("exit")) {
                System.out.print("insert, select or delete data: ");
                comando = scanner.nextLine().toLowerCase();

                switch (comando) {
                    case "insert" -> {
                        System.out.print("ID: ");
                        String id = scanner.nextLine();
                        System.out.print("Nombre: ");
                        String nombre = scanner.nextLine();
                        System.out.print("Apellidos: ");
                        String apellido = scanner.nextLine();

                        out.println("insert," + id + "," + nombre + "," + apellido);
                        leerServidor(socket);
                    }
                    case "select" -> {
                        System.out.print("ID: ");
                        String id = scanner.nextLine();

                        out.println("select," + id);
                        leerServidor(socket);
                    }
                    case "delete" -> {
                        System.out.print("ID: ");
                        String id = scanner.nextLine();

                        out.println("delete," + id);
                        leerServidor(socket);
                    }
                    case "exit" -> {
                        out.println("exit");
                        System.out.println("Cerrando programa...");
                    }
                    default -> System.out.println("Comando no reconocido.");

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void leerServidor(Socket socket){
        // Leer respuesta del servidor
        String respuesta="";
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            respuesta = in.readLine();
        } catch (IOException e) {
            System.out.println("Error al leer el servidor.");
        }
        System.out.println("Respuesta del servidor: " + respuesta);
    }
}

