import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class Principal {
    public static final int CANT_SIMBOLOS = 3;
    public static final int CANT_CARACTERES = 3;
    public static final double DIF = 0.01;
    public static void main() throws FileNotFoundException {

        File file = new File("./datos.txt");

        double[] probabilidades = new double[CANT_SIMBOLOS];
        Arrays.fill(probabilidades, 0);
        double[][] mat = new double[CANT_SIMBOLOS][CANT_SIMBOLOS];
        for (int i = 0; i < CANT_SIMBOLOS; i++){
            for (int j = 0; j < CANT_SIMBOLOS; j++){
                mat[i][j] = 0;
            }
        }
        PrimeraParte.leerArchivo(file, probabilidades, mat);
        System.out.println(probabilidades);


    }
}
