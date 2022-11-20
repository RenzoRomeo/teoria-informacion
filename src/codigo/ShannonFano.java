package codigo;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ShannonFano {

    public static TreeMap<String, String> shannonFano(TreeMap<String, Double> probabilidades) {

        TreeMap<String, String> codigos = new TreeMap<>();
        Map<String, Double> probabilidadesOrdenadas = probabilidades.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        generarCodigos(probabilidadesOrdenadas, codigos);
        return codigos;
    }


    public static void generarCodigos(Map<String, Double> probabilidades, Map<String, String> codigos) {
        if (probabilidades.size() == 2) {
            String[] palabras = probabilidades.keySet().toArray(new String[0]);
            codigos.put(palabras[0], "0");
            codigos.put(palabras[1], "1");
            return;
        }

        double probabilidadTotal = 0.0;
        for (String palabra : probabilidades.keySet()) {
            probabilidadTotal += probabilidades.get(palabra);
        }

        double p1 = 0.0;
        double p2 = probabilidadTotal;
        int k = 0;

        while (p1 <= p2) {
            p1 += probabilidades.get(probabilidades.keySet().toArray()[k]);
            p2 -= probabilidades.get(probabilidades.keySet().toArray()[k]);
            k++;
        }

        Map<String, Double> probabilidades1 = new LinkedHashMap<>();
        Map<String, Double> probabilidades2 = new LinkedHashMap<>();

        for (int i = 0; i < probabilidades.size(); i++) {
            String palabra = probabilidades.keySet().toArray()[i].toString();
            if (i < k) {
                probabilidades1.put(palabra, probabilidades.get(palabra));
            } else {
                probabilidades2.put(palabra, probabilidades.get(palabra));
            }
        }

        if (probabilidades1.size() > 1)
            generarCodigos(probabilidades1, codigos);

        if (probabilidades2.size() > 1)
            generarCodigos(probabilidades2, codigos);

        for (String palabra : probabilidades1.keySet()) {
            codigos.put(palabra, "0" + codigos.getOrDefault(palabra, ""));
        }

        for (String palabra : probabilidades2.keySet()) {
            codigos.put(palabra, "1" + codigos.getOrDefault(palabra, ""));
        }
    }

}
