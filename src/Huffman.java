import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class Huffman {

    private static class NodoHuffman {
        public double probabilidad;
        public String palabra;
        public NodoHuffman izq, der;

        public NodoHuffman(String palabra, double probabilidad) {
            this.probabilidad = probabilidad;
            this.palabra = palabra;
        }
    }

    private static class Comparador implements Comparator<NodoHuffman> {
        public int compare(NodoHuffman a, NodoHuffman b) {
            return Double.compare(a.probabilidad, b.probabilidad);
        }
    }

    public static TreeMap<String, String> huffman(TreeMap<String, Double> codigos) {
        TreeMap<String, String> codigoHuffman = new TreeMap<>();

        PriorityQueue<NodoHuffman> q = new PriorityQueue<>(codigos.size(), new Comparador());

        for (Map.Entry<String, Double> par : codigos.entrySet()) {
            NodoHuffman nodo = new NodoHuffman(par.getKey(), par.getValue());
            nodo.izq = null;
            nodo.der = null;
            q.add(nodo);
        }

        NodoHuffman raiz = null;

        while (q.size() > 1) {
            NodoHuffman a = q.poll();

            NodoHuffman b = q.poll();

            NodoHuffman f = new NodoHuffman("-", a.probabilidad + b.probabilidad);
            f.izq = a;
            f.der = b;

            q.add(f);
        }
        raiz = q.poll();

        guardarCodigo(raiz, "", codigoHuffman);

        return codigoHuffman;
    }

    private static void guardarCodigo(NodoHuffman raiz, String s, TreeMap<String, String> codigoHuffman) {
        if (raiz.izq == null && raiz.der == null && !raiz.palabra.equalsIgnoreCase("-")) {
            codigoHuffman.put(raiz.palabra, s);
        } else {
            guardarCodigo(raiz.izq, s + "0", codigoHuffman);
            guardarCodigo(raiz.der, s + "1", codigoHuffman);
        }
    }
}
