import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Arrays;

public class Principal {
    public static final int CANT_SIMBOLOS = 3;
    public static final int CANT_CARACTERES = 10000;
    public static final double DIF = 0.01;
    public static void main(String[] args) throws FileNotFoundException {
        File directorio = new File("./salidas");
        directorio.mkdir();

        File archivo = new File("./datos.txt");
        File resultados = new File("./salidas/parte1_incisoA.txt");

        // Verificar Flag -o

        // PARTE 1
            double[] probabilidades = new double[CANT_SIMBOLOS];
            Arrays.fill(probabilidades, 0);
            double[][] mat = new double[CANT_SIMBOLOS][CANT_SIMBOLOS];
            for (int i = 0; i < CANT_SIMBOLOS; i++){
                for (int j = 0; j < CANT_SIMBOLOS; j++){
                    mat[i][j] = 0;
                }
            }

        try {
            // Inciso A
            Writer writer = new FileWriter(resultados);
            PrimeraParte.leerArchivo(archivo, probabilidades, mat);

            writer.write("Probabilidades de cada simbolo: \n");
            for (int i = 0; i < CANT_SIMBOLOS; i++) {
                writer.write("P(" + (char) (i + 'A') + ") = " + probabilidades[i] + "\n");
            }

            writer.write("\nMatriz de transicion: \n");
            for (int i = 0; i < CANT_SIMBOLOS; i++) {
                for (int j = 0; j < CANT_SIMBOLOS; j++) {
                    writer.write(mat[i][j] + " ");
                }
                writer.write("\n");
            }
            writer.write("\n");

            System.out.println("Es memoria nula? lo sabremos aqui -> " + PrimeraParte.esMemoriaNula(probabilidades, mat));
            writer.write("La fuente es de memoria nula: " + (PrimeraParte.esMemoriaNula(probabilidades, mat) ? "SI" : "NO") + "\n");

            writer.close();

            // Inciso B
            resultados = new File("./salidas/parte1_incisoB.txt");
            writer = new FileWriter(resultados);

            double entropia = PrimeraParte.calcularEntropia(probabilidades);
            writer.write("Entropia (Fuente Original): " + entropia + "\n");
            writer.write("Entropia (Extension Orden 20, n * H(S)):)" + entropia * 20 + "\n");
            writer.write("Entropia (Extension Orden 20, calculada por extension): " + PrimeraParte.entropiaExtension(probabilidades, 20) + "\n");

            writer.close();

            // PARTE 2
        } catch (Exception e) {
            System.out.println("No se encontro el archivo");
        }


    }
}
