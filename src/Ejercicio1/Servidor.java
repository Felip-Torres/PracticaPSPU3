package Ejercicio1;

import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {
    public static void main(String[] args) {
        int port = 2000;
        boolean exit = false;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor en espera de conexiones...");
            Socket clientSocket = serverSocket.accept();
            String comando="";
            while (!exit) {
                System.out.println("Conexion establecida");

                try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    while ((comando = in.readLine()) != null) {
                        if (comando.equals("exit")) {
                            exit = true;
                            System.out.println("Cerrando servidor...");
                            break;
                        }
                        String[] partes = comando.split(",");
                        String accion = partes[0];
                        String respuesta = switch (accion) {
                            case "insert" -> insertarDato(partes[1], partes[2], partes[3]);
                            case "select" -> seleccionarDato(partes[1]);
                            case "delete" -> eliminarDato(partes[1]);
                            default -> "Error: Comando no reconocido.";
                        };

                        out.println(respuesta);
                    }
                }
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized List<String[]> leerBBDD() {
        List<String[]> datos = new ArrayList<>();
        File archivo = new File("bbdd.txt");

        if (!archivo.exists()) {
            return datos;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                datos.add(linea.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return datos;
    }

    private static synchronized void escribirBBDD(List<String[]> datos) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("bbdd.txt"))) {
            for (String[] registro : datos) {
                pw.println(String.join(",", registro));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String insertarDato(String id, String nombre, String apellido) {
        List<String[]> datos = leerBBDD();

        for (String[] registro : datos) {
            if (registro[0].equals(id)) {
                return "Error: ID ya existe en la base de datos.";
            }
        }

        datos.add(new String[]{id, nombre, apellido});
        escribirBBDD(datos);
        return "Dato insertado en la base de datos.";
    }

    private static String seleccionarDato(String id) {
        List<String[]> datos = leerBBDD();

        for (String[] registro : datos) {
            if (registro[0].equals(id)) {
                return String.join(" ", registro);
            }
        }

        return "Error: Elemento no encontrado en la base de datos.";
    }

    private static String eliminarDato(String id) {
        List<String[]> datos = leerBBDD();

        for (Iterator<String[]> iterator = datos.iterator(); iterator.hasNext(); ) {
            String[] registro = iterator.next();
            if (registro[0].equals(id)) {
                iterator.remove();
                escribirBBDD(datos);
                return "Dato eliminado de la base de datos.";
            }
        }

        return "Error: Elemento no encontrado en la base de datos.";
    }
}
