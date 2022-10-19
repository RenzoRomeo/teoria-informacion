import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

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

    public static Map<String, String> huffman(Map<String, Double> probabilidades) {
        HashMap<String, String> codigoHuffman = new HashMap<>();

        PriorityQueue<NodoHuffman> q = new PriorityQueue<>(probabilidades.size(), new Comparador());

        for (Map.Entry<String, Double> par : probabilidades.entrySet()) {
            NodoHuffman nodo = new NodoHuffman(par.getKey(), par.getValue());
            nodo.izq = null;
            nodo.der = null;
            q.add(nodo);
        }

        NodoHuffman raiz = null;

        while (q.size() > 1) {
            NodoHuffman a = q.peek();
            q.poll();

            NodoHuffman b = q.peek();
            q.poll();

            NodoHuffman f = new NodoHuffman("-", a.probabilidad + b.probabilidad);
            f.izq = null;
            f.der = null;

            raiz = f;

            q.add(f);
        }

        guardarCodigo(raiz, "", codigoHuffman);

        return codigoHuffman;
    }

    private static void guardarCodigo(NodoHuffman raiz, String s, Map<String, String> codigoHuffman) {
        if (raiz.izq == null && raiz.der == null && raiz.palabra != "-") {
            System.out.println(raiz.palabra + ": " + s);
            codigoHuffman.put(raiz.palabra, s);
        } else {
            guardarCodigo(raiz.izq, s + "0", codigoHuffman);
            guardarCodigo(raiz.der, s + "1", codigoHuffman);
        }
    }

}
