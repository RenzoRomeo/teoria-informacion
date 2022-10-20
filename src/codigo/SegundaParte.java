package codigo;

import java.io.*;
import java.util.TreeMap;

public class SegundaParte {
    private static final String RUTA_BASE = "./salidas/parte2_longitud";

    public static void procesarCodigo(int longitudExtension) throws IOException {
        File archivo;
        BufferedWriter writer;

        TreeMap<String, Double> codigos = new TreeMap<>();
        calcularProbabilidades("datos.txt", longitudExtension, codigos);

        archivo = new File(RUTA_BASE + longitudExtension + "_incisoA.txt");

        mostrarInformacion(archivo, codigos);
        double entropia = calcularEntropiaFuente(codigos);

        writer = new BufferedWriter(new FileWriter(archivo, true));
        writer.write("Entropia: " + entropia);
        writer.close();

        archivo = new File(RUTA_BASE + longitudExtension + "_incisoC.txt");

        double longitudMedia = calcularLongitudMedia(codigos);

        writer = new BufferedWriter(new FileWriter(archivo));
        writer.write("Cumple inecuaciones de Kraft y McMillan: " + (cumpleKraft(codigos) ? "SI" : "NO") + "\n");
        writer.write("Longitud media: " + longitudMedia + "\n");
        writer.write("Es codigo compacto: " + (esCodigoCompacto(codigos) ? "SI" : "NO") + "\n");
        writer.close();

        archivo = new File(RUTA_BASE + longitudExtension + "_incisoD.txt");
        writer = new BufferedWriter(new FileWriter(archivo));
        writer.write("Rendimiento: " + String.format("%.5f", calcularRendimiento(entropia, longitudMedia)) + "\n");
        writer.write("Redundancia: " + String.format("%.5f", calcularRedundancia(entropia, longitudMedia)) + "\n");
        writer.close();

        TreeMap<String, String> codigosHuffman = Huffman.huffman(codigos);

        archivo = new File(RUTA_BASE + longitudExtension + "_incisoE.txt");
        writer = new BufferedWriter(new FileWriter(archivo));
        writer.write("Codigo codigo.Huffman\n\n");
        writer.write("Palabra | Codigo\n");
        for (String palabra : codigosHuffman.keySet()) {
            writer.write(String.format("%-7s", palabra) + " | " + codigosHuffman.get(palabra) + "\n");
        }
        writer.close();

        regenerarArchivo("datos.txt", RUTA_BASE + longitudExtension + "_incisoE_regenerado.dat", codigosHuffman, longitudExtension);
    }

    private static void calcularProbabilidades(String nombreArchivo, int tamanoPalabra, TreeMap<String, Double> codigos) {
        try {
            File archivo = new File(nombreArchivo);
            Reader reader = new FileReader(archivo);

            // Acumula la cantidad de palabras que aparecen en el archivo.
            int apariciones = 0;
            String palabra = "";
            int c;

            while ((c = reader.read()) != -1) {

                palabra += (char) c;

                if (palabra.length() == tamanoPalabra) {
                    if (codigos.containsKey(palabra)) {
                        double aparicionesPalabra = codigos.get(palabra);
                        codigos.put(palabra, aparicionesPalabra + 1);
                    } else {
                        codigos.put(palabra, 1.0);
                    }
                    palabra = "";
                    apariciones++;
                }
            }

            reader.close();

            for (String key : codigos.keySet()) {
                codigos.put(key, codigos.get(key) / apariciones);
            }

        } catch (Exception e) {
            System.out.println("Error al leer el archivo");
        }
    }

    // Calcula la información en base a una probabilidad.
    private static double calcularInformacion(double probabilidad) {
        double informacion = 0.0;

        if (probabilidad != 0) {
            informacion = -Math.log(probabilidad) / Math.log(Principal.CANT_SIMBOLOS);
        }

        return informacion;
    }

    // Calcula la entropía de una fuente.
    private static double calcularEntropiaFuente(TreeMap<String, Double> codigos) {
        double entropia = 0.0;

        for (String palabra : codigos.keySet()) {
            double probabilidad = codigos.get(palabra);
            entropia += probabilidad * calcularInformacion(probabilidad);
        }

        return entropia;
    }

    // Muestra la informacion y la probabilidad de cada palabra.
    private static void mostrarInformacion(File resultados, TreeMap<String, Double> codigos) throws IOException {
        Writer writer = new FileWriter(resultados);

        writer.write("Palabra | Probabilidad | Informacion\n");

        for (String palabra : codigos.keySet()) {
            double probabilidad = codigos.get(palabra);
            writer.write(String.format("%-8s", palabra) + ": " + String.format("%.11f", probabilidad) + " (" + String.format("%.13f", calcularInformacion(probabilidad)) + ")\n");
        }

        writer.write("Las palabras que no aparecen en el archivo tienen probabilidad 0.\n");
        writer.close();
    }

    private static double calcularLongitudMedia(TreeMap<String, Double> codigos) {
        double longitudMedia = 0.0;

        for (String palabra : codigos.keySet()) {
            longitudMedia += codigos.get(palabra) * palabra.length();
        }

        return longitudMedia;
    }

    private static boolean esCodigoCompacto(TreeMap<String, Double> codigos) {
        boolean esCompacto = true;

        for (String palabra : codigos.keySet()) {
            if (Math.abs(codigos.get(palabra) - Math.pow(Principal.CANT_SIMBOLOS, -palabra.length())) > 0.0001) {
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

    private static boolean cumpleKraft(TreeMap<String, Double> codigos) {
        double kraft = 0.0;

        for (String palabra : codigos.keySet()) {
            kraft += Math.pow(Principal.CANT_SIMBOLOS, -palabra.length());
        }

        return kraft <= 1.0 + Principal.DIF;
    }

    private static void regenerarArchivo(String nombreOriginal, String nombreRegenerado, TreeMap<String, String> codigosHuffman, int tamanoPalabra) throws IOException {
        File archivo = new File(nombreOriginal);
        Reader reader = new FileReader(archivo);

        archivo = new File(nombreRegenerado);
        FileOutputStream writer = new FileOutputStream(archivo);

        int b = 0;
        int cantBit = 0;
        String palabra = "";
        int c;

        while ((c = reader.read()) != -1) {
            palabra += (char) c;

            if (palabra.length() == tamanoPalabra) {
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
                palabra = "";
            }
        }

        if (cantBit != 0) {
            b = b << (8 - cantBit);
            writer.write(b);
        }

        writer.close();
    }
}
