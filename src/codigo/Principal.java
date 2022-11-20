package codigo;

import java.io.IOException;
import java.util.TreeMap;

public class Principal {
    public static void main(String[] args) throws IOException {
        TreeMap<String, Double> probabilidades = ParteUno.leerArchivo("datos.txt");

        TreeMap<String, String> codigosHuffman = Huffman.huffman(probabilidades);
        TreeMap<String, String> codigosShannon = ShannonFano.shannonFano(probabilidades);

        ParteUno.comprimir("datos.txt", "comprimido.huff", codigosHuffman);
        ParteUno.comprimir("datos.txt", "comprimido.shan", codigosShannon);

        ParteUno.descomprimir("comprimido.huff", "descomprimidoHuffman.txt");
        ParteUno.descomprimir("comprimido.shan", "descomprimidoShannon.txt");
    }
}
