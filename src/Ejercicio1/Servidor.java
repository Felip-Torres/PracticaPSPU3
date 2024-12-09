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
            System.out.println("Conexion establecida");

            // Crear flujos de entrada/salida
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String comando = "";
            //El servidor seguira esperando comandos del cliente hasta que llegue exit
            while (!exit) {
                try {
                    if ((comando = in.readLine()) != null) {
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
                } catch (IOException e) {
                    System.out.println("Error al manejar el comando: " + e.getMessage());
                }
            }
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }

    //Devuelve una lista con los datos de la base de datos
    private static List<String[]> leerBBDD() {
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
            System.out.println("Error al leer la base de datos: " + e.getMessage());
        }

        return datos;
    }

    //Escribe la lista de datos en la base de datos
    private static void escribirBBDD(List<String[]> datos) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("bbdd.txt"))) {
            for (String[] fila : datos) {
                pw.println(String.join(",", fila));
            }
        } catch (IOException e) {
            System.out.println("Error al escribir en la base de datos: " + e.getMessage());
        }
    }

    //Añade los datos a la base de datos
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

    //Devuelve los datos de la id proporcionada
    private static String seleccionarDato(String id) {
        List<String[]> datos = leerBBDD();

        for (String[] fila : datos) {
            if (fila[0].equals(id)) {
                return String.join(" ", fila);
            }
        }

        return "Error: Elemento no encontrado en la base de datos.";
    }

    //Elimina los datos de la id proporcionada
    private static String eliminarDato(String id) {
        List<String[]> datos = leerBBDD();

        for (String[] fila : datos) {
            if (fila[0].equals(id)) {
                datos.remove(fila);
                escribirBBDD(datos);
                return "Dato eliminado de la base de datos.";
            }
        }

        return "Error: Elemento no encontrado en la base de datos.";
    }
}
