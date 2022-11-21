package codigo;

import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class ParteUno {
    public static TreeMap<String, Double> leerArchivo(String nombreArchivo) throws FileNotFoundException {
        TreeMap<String, Double> probabilidades = new TreeMap<>();
        int cantTotal = 0;

        File archivo = new File(nombreArchivo);
        Scanner sc = new Scanner(archivo);

        while (sc.hasNextLine()) {
            Scanner sc2 = new Scanner(sc.nextLine());
            while (sc2.hasNext()) {
                String s = sc2.next();
                if (probabilidades.containsKey(s)) {
                    probabilidades.put(s, probabilidades.get(s) + 1.0);
                } else {
                    probabilidades.put(s, 1.0);
                }
                cantTotal++;
            }
            sc2.close();
        }

        sc.close();

        for (String s : probabilidades.keySet()) {
            probabilidades.put(s, probabilidades.get(s) / cantTotal);
        }

        return probabilidades;
    }

    /**
     * La tabla se almacena de la siguiente manera:
     * int con tamano de tabla
     * String con palabra, termina en \0
     * byte con tamano del codigo en bits
     * bit[] con codigo
     * el ultimo byte se rellena con 0s
     */
    public static void almacenarTabla(FileOutputStream writer, TreeMap<String, String> codigos) throws IOException {
        // Almacenar el tamaño de la tabla como 4 bytes
        int tamanoTabla = codigos.size();
        writer.write((tamanoTabla >> 24) & 0xFF);
        writer.write((tamanoTabla >> 16) & 0xFF);
        writer.write((tamanoTabla >> 8) & 0xFF);
        writer.write(tamanoTabla & 0xFF);

        byte b = 0;
        int cantBits = 0;
        for (Map.Entry<String, String> par : codigos.entrySet()) {
//            String palabra = par.getKey();
            byte[] palabraBytes = par.getKey().getBytes();
            String codigo = par.getValue();
            for (int i = 0; i < palabraBytes.length; i++) {
//                char c = palabra.charAt(i);
                byte c = palabraBytes[i];
                for (int j = 0; j < 8; j++) {
                    b = (byte) (b << 1);
                    b = (byte) (b | ((c & 0x80) >> 7)); // 0x80 = 1000 0000
                    c = (byte) (c << 1);
                    cantBits++;
                    if (cantBits == 8) {
                        writer.write(b);
                        b = 0;
                        cantBits = 0;
                    }
                }
            }

            // Para el \0
            for (int i = 0; i < 8; i++) {
                b = (byte) (b << 1);
                cantBits++;
                if (cantBits == 8) {
                    writer.write(b);
                    b = 0;
                    cantBits = 0;
                }
            }

            byte logitudCodigo = (byte) codigo.length();

            for (int i = 0; i < 8; i++) {
                b = (byte) (b << 1);
                b = (byte) (b | ((logitudCodigo & 0x80) >> 7));
                logitudCodigo = (byte) (logitudCodigo << 1);
                cantBits++;
                if (cantBits == 8) {
                    writer.write(b);
                    b = 0;
                    cantBits = 0;
                }
            }

            for (int i = 0; i < codigo.length(); i++) {
                b = (byte) (b << 1);
                if (codigo.charAt(i) == '1') {
                    b = (byte) (b | 1);
                }
                cantBits++;
                if (cantBits == 8) {
                    writer.write(b);
                    b = 0;
                    cantBits = 0;
                }
            }
        }
        if (cantBits != 0) {
            b = (byte) (b << (8 - cantBits));
            writer.write(b);
        }
    }

    public static TreeMap<String, String> leerTabla(FileInputStream reader) throws IOException {
        TreeMap<String, String> codigos = new TreeMap<>();
        byte[] tamanoTablaBytes = reader.readNBytes(4);

        int tamanoTabla = 0;
        for (int i = 0; i < 4; i++) {
            tamanoTabla = tamanoTabla << 8;
            tamanoTabla = tamanoTabla | (tamanoTablaBytes[i] & 0xFF);
        }

        byte b = 0;
        int cantBits = 0;

        for (int i = 0; i < tamanoTabla; i++) {
            String palabra = "";
            byte[] palabraBytes = new byte[50];
            int indice = 0;

            byte c;
            do {
                c = 0;
                for (int j = 0; j < 8; j++) {
                    if (cantBits == 0) {
                        cantBits = 8;
                        b = reader.readNBytes(1)[0];
                    }
                    c = (byte) (c << 1);
                    c = (byte) (c | ((b & 0x80) >> 7));
                    b = (byte) (b << 1);
                    cantBits--;
                }
                if (c != 0) {
                    palabraBytes[indice] = c;
                    indice++;
//                    palabra += c;
                }
            } while (c != 0);
            palabra = new String(palabraBytes, 0, indice);

            byte logitudCodigo = 0;
            for (int j = 0; j < 8; j++) {
                if (cantBits == 0) {
                    cantBits = 8;
                    b = reader.readNBytes(1)[0];
                }
                logitudCodigo = (byte) (logitudCodigo << 1);
                logitudCodigo = (byte) (logitudCodigo | ((b & 0x80) >> 7));
                b = (byte) (b << 1);
                cantBits--;
            }

            String codigo = "";
            for (int j = 0; j < logitudCodigo; j++) {
                if (cantBits == 0) {
                    cantBits = 8;
                    b = reader.readNBytes(1)[0];
                }

                codigo += (b & 0x80) == 0 ? '0' : '1';
                b = (byte) (b << 1);
                cantBits--;
            }
            codigos.put(codigo, palabra);
        }

        return codigos;
    }

    public static void comprimir(String nombreArchivo, String nombreArchivoSalida, TreeMap<String, String> codigos) throws IOException {
        File archivo = new File(nombreArchivo);
        Scanner sc = new Scanner(archivo);
        FileOutputStream writer = new FileOutputStream("salidas/" + nombreArchivoSalida);

        almacenarTabla(writer, codigos);

        byte b = 0;
        int cantBits = 0;
        while (sc.hasNextLine()) {
            Scanner sc2 = new Scanner(sc.nextLine());
            while (sc2.hasNext()) {
                String palabra = sc2.next();
                String codigo = codigos.get(palabra);
                for (int i = 0; i < codigo.length(); i++) {
                    b = (byte) (b << 1);
                    if (codigo.charAt(i) == '1') {
                        b = (byte) (b | 1);
                    }
                    cantBits++;
                    if (cantBits == 8) {
                        writer.write(b);
                        b = 0;
                        cantBits = 0;
                    }
                }
            }
            sc2.close();
        }

        if (cantBits != 0) {
            b = (byte) (b << (8 - cantBits));
            writer.write(b);
        }

        sc.close();
        writer.close();
    }

    public static void descomprimir(String nombreComprimido, String nombreDescomprimido) throws IOException {
        FileInputStream reader = new FileInputStream("salidas/" + nombreComprimido);
        TreeMap<String, String> codigos = leerTabla(reader);

        FileOutputStream writer = new FileOutputStream("salidas/" + nombreDescomprimido);

        byte b = 0;
        int cantBits = 0;
        String codigo = "";
        while (reader.available() > 0) {
            if (cantBits == 0) {
                cantBits = 8;
                b = reader.readNBytes(1)[0];
            }

            codigo += (b & 0x80) == 0 ? '0' : '1';
            b = (byte) (b << 1);
            cantBits--;

            if (codigos.containsKey(codigo)) {
                String palabra = codigos.get(codigo);
                writer.write(palabra.getBytes());
                writer.write(' ');
                codigo = "";
            }
        }

        writer.close();
        reader.close();
    }

    public static TreeMap<String, Double> probabilidadesCodigos(TreeMap<String, Double> probabilidadesOriginales, TreeMap<String, String> codigos) {
        TreeMap<String, Double> probabilidadesCodigos = new TreeMap<>();
        for (Map.Entry<String, String> entry : codigos.entrySet()) {
            probabilidadesCodigos.put(entry.getValue(), probabilidadesOriginales.get(entry.getKey()));
        }

        return probabilidadesCodigos;
    }

    public static void propiedades(String nombreArchivo, TreeMap<String, Double> probabilidades, int cantSimbolos) {
        double entropia = calcularEntropiaFuente(probabilidades, cantSimbolos);
        double longitudMedia = calcularLongitudMedia(probabilidades);
        double rendimiento = calcularRendimiento(entropia, longitudMedia);
        double redundancia = calcularRedundancia(entropia, longitudMedia);

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("salidas/" + nombreArchivo));
            writer.write("Entropia: " + entropia);
            writer.newLine();
            writer.write("Longitud media: " + longitudMedia);
            writer.newLine();
            writer.write("Rendimiento: " + rendimiento);
            writer.newLine();
            writer.write("Redundancia: " + redundancia);
            writer.newLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static double calcularInformacion(double probabilidad, int cantSimbolos) {
        double informacion = 0.0;

        if (probabilidad != 0) {
            informacion = -Math.log(probabilidad) / Math.log(cantSimbolos);
        }

        return informacion;
    }

    private static double calcularEntropiaFuente(TreeMap<String, Double> probabilidades, int cantSimbolos) {
        double entropia = 0.0;

        for (String palabra : probabilidades.keySet()) {
            double probabilidad = probabilidades.get(palabra);
            entropia += probabilidad * calcularInformacion(probabilidad, cantSimbolos);
        }

        return entropia;
    }

    private static double calcularLongitudMedia(TreeMap<String, Double> probabilidades) {
        double longitudMedia = 0.0;

        for (String palabra : probabilidades.keySet()) {
            int longitud = 0;
            for (int i = 0; i < palabra.length(); i++) {
                if (palabra.charAt(i) < 256) {
                    longitud++;
                }
            }
            longitudMedia += probabilidades.get(palabra) * longitud;
        }

        return longitudMedia;
    }

    private static double calcularRendimiento(double entropia, double lontiudMedia) {
        return entropia / lontiudMedia;
    }

    private static double calcularRedundancia(double entropia, double longitudMedia) {
        return (longitudMedia - entropia) / longitudMedia;
    }
}
