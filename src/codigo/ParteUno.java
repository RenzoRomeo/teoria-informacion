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
            String palabra = par.getKey();
            String codigo = par.getValue();
            for (int i = 0; i < palabra.length(); i++) {
                char c = palabra.charAt(i);
                for (int j = 0; j < 8; j++) {
                    b = (byte) (b << 1);
                    b = (byte) (b | ((c & 0x80) >> 7)); // 0x80 = 1000 0000
                    c = (char) (c << 1);
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

            char c;
            do {
                c = 0;
                for (int j = 0; j < 8; j++) {
                    if (cantBits == 0) {
                        cantBits = 8;
                        b = reader.readNBytes(1)[0];
                    }
                    c = (char) (c << 1);
                    c = (char) (c | ((b & 0x80) >> 7));
                    b = (byte) (b << 1);
                    cantBits--;
                }
                if (c != 0) {
                    palabra += c;
                }
            } while (c != 0);

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
            codigos.put(palabra, codigo);
        }

        return codigos;
    }

    public static void comprimir(String nombreArchivo, String nombreArchivoSalida, TreeMap<String, String> codigos) throws IOException {
        File archivo = new File(nombreArchivo);
        Scanner sc = new Scanner(archivo);
        FileOutputStream writer = new FileOutputStream(nombreArchivoSalida);

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
        FileInputStream reader = new FileInputStream(nombreComprimido);
        TreeMap<String, String> codigos = leerTabla(reader);

        FileOutputStream writer = new FileOutputStream(nombreDescomprimido);

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

            for (Map.Entry<String, String> entry : codigos.entrySet()) {
                if (entry.getValue().equals(codigo)) {
                    writer.write(entry.getKey().getBytes());
                    writer.write(' ');
                    codigo = "";
                    break;
                }
            }
        }

        writer.close();
        reader.close();
    }
}
