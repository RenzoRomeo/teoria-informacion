package codigo;

import java.io.FileNotFoundException;
import java.util.TreeMap;

public class Principal {
    public static void main(String[] args) throws FileNotFoundException {
        TreeMap<String, Double> probabilidades = ParteUno.leerArchivo("datos.txt");
        for (String s : probabilidades.keySet()) {
            System.out.println(s + " " + probabilidades.get(s));
        }
    }


}
