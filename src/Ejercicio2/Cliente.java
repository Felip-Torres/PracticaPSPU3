package Ejercicio2;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        final String HOST = "localhost";
        final int PORT = 3000;

        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Indica tu número:");
            int numOrigen = Integer.parseInt(scanner.nextLine());

            System.out.println("Indica el número del receptor:");
            int numDestino = Integer.parseInt(scanner.nextLine());

            while (true) {
                System.out.println("Escriu el missatge a enviar:");
                String texto = scanner.nextLine();

                String mensajeJson = String.format("{\"origen\":%d,\"destino\":%d,\"texto\":\"%s\"}", numOrigen, numDestino, texto);
                out.println(mensajeJson);

                if (texto.equalsIgnoreCase("adeu")) {
                    System.out.println("Chat finalizado.");
                    break;
                }

                String respuesta = in.readLine();
                System.out.println("Contingut actual de la conversa:");
                System.out.println(respuesta);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

