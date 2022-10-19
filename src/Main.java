import java.io.File;
import java.util.ArrayList;

public class Main {
    public static final int CANT_SIMBOLOS = 3;
    public static final int CANT_CARACTERES = 3;
    public static final double DIF = 0.01;
    public static void main(){

        File file = new File("./datos.txt");

        double[] probabilidades = new double[CANT_SIMBOLOS];
        double[][] mat = new double[CANT_SIMBOLOS][CANT_SIMBOLOS];
        for (int i = 0; i < CANT_SIMBOLOS; i++){
            probabilidades[i] = 0;
            for (int j = 0; j < CANT_SIMBOLOS; j++){
                mat[i][j] = 0;
            }
        }

    }
}
