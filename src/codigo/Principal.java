package codigo;

import java.io.IOException;
import java.util.TreeMap;

public class Principal {
    public static void main(String[] args) throws IOException {
        TreeMap<String, Double> probabilidades = ParteUno.leerArchivo("datos.txt");

        TreeMap<String, String> codigos = Huffman.huffman(probabilidades);
        ParteUno.comprimir("datos.txt", "comprimido.huff", codigos);
        ParteUno.descomprimir("comprimido.huff", "descomprimido.txt");
    }
}
