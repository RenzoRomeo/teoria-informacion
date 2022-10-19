import java.io.*;
import java.util.HashMap;

public class SegundaParte {
    private static int CANTIDAD_SIMBOLOS = 3;

    public static void procesarCodigo(int longitudExtension) {
        HashMap<String, Double> probabilidades = new HashMap<>();

    }

    private static void calcularProbabilidades(String nombreArchivo, int tamanoPalabra, HashMap<String, Double> codigos) {
        try {
            File archivo = new File(nombreArchivo);
            Reader reader = new FileReader(archivo);

            // Acumula la cantidad de palabras que aparecen en el archivo.
            int apariciones = 0;
            String palabra = "";
            char c;

            while((c = (char) reader.read()) != -1) {
                palabra += c;

                if(palabra.length() == tamanoPalabra) {
                    if(codigos.containsKey(palabra)) {
                        apariciones = codigos.get(palabra).intValue();
                        codigos.put(palabra, (double) (apariciones + 1));
                    } else {
                        codigos.put(palabra, 1.0);
                    }
                    palabra = "";
                    apariciones++;
                }
            }

            reader.close();

            for(String key : codigos.keySet()) {
                codigos.put(key, codigos.get(key) / apariciones);
            }

        } catch (Exception e) {
            System.out.println("Error al leer el archivo");
        }
    }

    // Calcula la información en base a una probabilidad.
    private static double calcularInformacion(double probabilidad) {
        double informacion = 0.0;

        if(probabilidad != 0) {
            informacion = -Math.log(probabilidad) / Math.log(2);
        }

        return informacion;
    }

    // Calcula la entropía de una fuente.
    private static double calcularEntropiaFuente(HashMap<String, Double> codigos) {
        double entropia = 0.0;

        for(String palabra : codigos.keySet()) {
            double probabilidad = codigos.get(palabra);
            entropia += probabilidad * calcularInformacion(probabilidad);
        }

        return entropia;
    }

    // Muestra la informacion y la probabilidad de cada palabra.
    private static void mostrarInformacion(File resultados, HashMap<String, Double> codigos) throws IOException {
        Writer writer = new FileWriter(resultados);

        writer.write("Palabra | Probabilidad | Informacion\n");

        for(String palabra : codigos.keySet()) {
            double probabilidad = codigos.get(palabra);
            writer.write(palabra + ": " + probabilidad + " (" + calcularInformacion(probabilidad) + ")\n");
        }

        writer.write("Las palabras que no aparecen en el archivo tienen probabilidad 0.\n");
        writer.close();
    }

    private static double calcularLongitudMedia(HashMap<String, Double> codigos) {
        double longitudMedia = 0.0;

        for(String palabra : codigos.keySet()) {
            longitudMedia += codigos.get(palabra) * palabra.length();
        }

        return longitudMedia;
    }

    private static boolean esCodigoCompacto(HashMap<String, Double> codigos) {
        boolean esCompacto = true;

        for(String palabra : codigos.keySet()) {
            if(Math.abs(codigos.get(palabra) - Math.pow(CANTIDAD_SIMBOLOS, -palabra.length())) > 0.0001) {
                esCompacto = false;
                break;
            }
        }

        return esCompacto;
    }

    private double calcularRendimiento(double entropia, double lontiudMedia) {
        return entropia / lontiudMedia;
    }

    public double calcularRedundancia(double entropia, double longitudMedia) {
        return (longitudMedia - entropia) / longitudMedia;
    }
}
