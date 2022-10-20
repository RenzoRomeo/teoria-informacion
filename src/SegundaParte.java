import java.io.*;
import java.util.HashMap;

public class SegundaParte {
    private static String RUTA_BASE = "./salidas/parte2_longitud";

    public static void procesarCodigo(int longitudExtension) throws IOException {
        File archivo;
        BufferedWriter writer;

        HashMap<String, Double> codigos = new HashMap<>();
        calcularProbabilidades("datos.txt", longitudExtension, codigos);

        archivo = new File(RUTA_BASE + longitudExtension + "_incisoA.txt");

        mostrarInformacion(archivo, codigos);
        double entropia = calcularEntropiaFuente(codigos);

        writer = new BufferedWriter(new FileWriter(archivo));
        writer.write("Entropia: " + entropia);
        writer.close();

        archivo = new File(RUTA_BASE + longitudExtension + "_incisoC.txt");

        double longitudMedia = calcularLongitudMedia(codigos);

        writer = new BufferedWriter(new FileWriter(archivo));
        writer.write("Cumple inecuaciones de Kraft y McMillan: " + (cumpleKraft(codigos) ? "SI" : "NO") + "\n");
        writer.write("Longitud media: " + longitudMedia);
        writer.write("Es codigo compacto: " + (esCodigoCompacto(codigos) ? "SI" : "NO") + "\n");
        writer.close();

        archivo = new File(RUTA_BASE + longitudExtension + "_incisoD.txt");
        writer = new BufferedWriter(new FileWriter(archivo));
        writer.write("Rendimiento: " + calcularRendimiento(entropia, longitudMedia));
        writer.write("Redundancia: " + calcularRedundancia(entropia, longitudMedia));
        writer.close();

        HashMap<String, String> codigosHuffman = Huffman.huffman(codigos);

        archivo = new File(RUTA_BASE + longitudExtension + "_incisoE.txt");
        writer = new BufferedWriter(new FileWriter(archivo));
        writer.write("Codigo Huffman\n\n");
        writer.write("Palabra | Codigo\n");
        for (String palabra : codigosHuffman.keySet()) {
            writer.write(palabra + " | " + codigosHuffman.get(palabra) + "\n");
        }
        writer.close();

        regenerarArchivo("datos.txt", RUTA_BASE + longitudExtension + "_incisoE_regenerado.txt", codigosHuffman, longitudExtension);
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
            if(Math.abs(codigos.get(palabra) - Math.pow(Principal.CANT_SIMBOLOS, -palabra.length())) > 0.0001) {
                esCompacto = false;
                break;
            }
        }

        return esCompacto;
    }

    private static double calcularRendimiento(double entropia, double lontiudMedia) {
        return entropia / lontiudMedia;
    }

    private static double calcularRedundancia(double entropia, double longitudMedia) {
        return (longitudMedia - entropia) / longitudMedia;
    }

    private static boolean cumpleKraft(HashMap<String, Double> codigos) {
        double kraft = 0.0;

        for(String palabra : codigos.keySet()) {
            kraft += Math.pow(Principal.CANT_SIMBOLOS, -palabra.length());
        }

        return kraft <= 1.0 + Principal.DIF;
    }

    private static void regenerarArchivo(String nombreOriginal, String nombreRegenerado, HashMap<String, String> codigosHuffman, int tamanoPalabra) throws IOException {
        File archivo = new File(nombreOriginal);
        Reader reader = new FileReader(archivo);

        archivo = new File(nombreRegenerado);
        BufferedWriter writer = new BufferedWriter(new FileWriter(archivo));

        int b = 0;
        int cantBit = 0;
        String palabra = "";
        char c;
        int cont = 0;

        while((c = (char)reader.read()) != -1) {
            palabra += c;
            cont++;

            if (cont == tamanoPalabra) {
                cont = 0;
                String codigo = codigosHuffman.get(palabra);
                for (int i = 0; i < codigo.length(); i++) {
                    b = b << 1;
                    b += codigo.charAt(i) - '0';
                    cantBit++;
                    if (cantBit == 8) {
                        writer.write(b);
                        b = 0;
                        cantBit = 0;
                    }
                }
            }
        }
    }
}
