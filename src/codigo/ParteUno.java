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
        }

        for (String s : probabilidades.keySet()) {
            probabilidades.put(s, probabilidades.get(s) / cantTotal);
        }

        return probabilidades;
    }
}
