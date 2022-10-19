import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;

public class SegundaParte {
    private static void calcularProbabilidades(String nombreArchivo, int tamanoPalabra, HashMap<String, Double> codigos) {
        try {
            File archivo = new File(nombreArchivo);
            Reader reader = new FileReader(archivo);

            // Acumula la cantidad de palabras que aparecen en el archivo.
            int apariciones = 0;
            String palabra = "";
            char c;

            while((c = (char) reader.read()) != -1) {
                palabra += c;

                if(palabra.length() == tamanoPalabra) {
                    if(codigos.containsKey(palabra)) {
                        apariciones = codigos.get(palabra).intValue();
                        codigos.put(palabra, (double) (apariciones + 1));
                    } else {
                        codigos.put(palabra, 1.0);
                    }
                    palabra = "";
                    apariciones++;
                }
            }

            reader.close();

            for(String key : codigos.keySet()) {
                codigos.put(key, codigos.get(key) / apariciones);
            }

        } catch (Exception e) {
            System.out.println("Error al leer el archivo");
        }
    }

    // Calcula la información en base a una probabilidad.
    private double calcularInformacion(double probabilidad) {
        double informacion = 0.0;

        if(probabilidad != 0) {
            informacion = -Math.log(probabilidad) / Math.log(2);
        }

        return informacion;
    }

    // Calcula la entropía de una fuente.
    private double calcularEntropiaFuente(HashMap<String, Double> codigos) {
        double entropia = 0.0;

        for(String key : codigos.keySet()) {
            double probabilidad = codigos.get(key);
            entropia += probabilidad * calcularInformacion(probabilidad);
        }

        return entropia;
    }

    // Muestra la informacion y la probabilidad de cada palabra.
    private void mostrarInformacion(File resultados, HashMap<String, Double> codigos, int tamanoPalabra) {

        for(String key : codigos.keySet()) {
            System.out.println(key + " - " + codigos.get(key) + " - " + calcularInformacion(codigos.get(key)));
        }
    }
}
