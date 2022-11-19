package codigo;

import java.io.IOException;
import java.util.TreeMap;

public class Principal {
    public static void main(String[] args) throws IOException {
        TreeMap<String, Double> probabilidades = ParteUno.leerArchivo("datos.txt");
        for (String s : probabilidades.keySet()) {
            System.out.println(s + " " + probabilidades.get(s));
        }

        TreeMap<String, String> codigos = Huffman.huffman(probabilidades);
        ParteUno.almacenarTabla("tabla.dat", codigos);
        ParteUno.leerTabla("tabla.dat");
        for (String s : codigos.keySet()) {
            System.out.println(s + " " + codigos.get(s));
        }
    }
}
