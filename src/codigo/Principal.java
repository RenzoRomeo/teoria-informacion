package codigo;

import java.io.IOException;
import java.util.TreeMap;

public class Principal {
    public static void main(String[] args) throws IOException {
        TreeMap<String, Double> probabilidades = ParteUno.leerArchivo("datos.txt");

        if (!new java.io.File("salidas").exists()) {
            new java.io.File("salidas").mkdir();
        }

        // El valor de 71 símbolos para el alfabeto del texto original
        // está hardcodeado porque tuvimos que contar a mano el número de
        // símbolos que aparecen en el texto original, ya que en el archivo
        // las palabras con tilde están codificadas como 2 caracteres.
        ParteUno.propiedades("propiedadesOriginal.txt", probabilidades, 71);

        TreeMap<String, String> codigosHuffman = Huffman.huffman(probabilidades);

        TreeMap<String, Double> probabilidadesHuffman = ParteUno.probabilidadesCodigos(probabilidades, codigosHuffman);

        ParteUno.propiedades("propiedadesHuffman.txt", probabilidadesHuffman, 2);

        TreeMap<String, String> codigosShannon = ShannonFano.shannonFano(probabilidades);

        TreeMap<String, Double> probabilidadesShannon = ParteUno.probabilidadesCodigos(probabilidades, codigosShannon);

        ParteUno.propiedades("propiedadesShannon.txt", probabilidadesShannon, 2);

        ParteUno.comprimir("datos.txt", "comprimido.Huf", codigosHuffman);
        ParteUno.comprimir("datos.txt", "comprimido.Fan", codigosShannon);

        ParteUno.descomprimir("comprimido.Huf", "descomprimidoHuffman.txt");
        ParteUno.descomprimir("comprimido.Fan", "descomprimidoShannon.txt");
    }
}
