import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class PrimeraParte {

    public static final int CANT_SIMBOLOS = 3;
    public static final int CANT_CARACTERES = 10000;
    public static final double DIF = 0.01;

    public static void leerArchivo(File file, double[] probabilidades, double[][] mat) throws FileNotFoundException {
        try {
            Reader reader = new FileReader(file);
            int ant = reader.read();
            int sig;
            while ((sig = reader.read()) != -1) {
                probabilidades[ant - 65]++;

                mat[sig - 65][ant - 65]++;

                ant = sig;
            }

            if (ant != -1){
                probabilidades[ant - 65]++;
            }

            int[] sumas = new int[CANT_SIMBOLOS];
            Arrays.fill(sumas, 0);
            for (int i = 0; i < CANT_SIMBOLOS; i++)
            {
                for (int j = 0; j < CANT_SIMBOLOS; j++)
                {
                    sumas[j] += mat[i][j];
                }
            }

            for (int i = 0; i < CANT_SIMBOLOS; i++)
            {
                for (int j = 0; j < CANT_SIMBOLOS; j++)
                {
                    mat[i][j] /= (double)(sumas[j]);
                }
            }

            for (int i = 0; i < CANT_SIMBOLOS; i++)
            {
                probabilidades[i] /= (double)CANT_CARACTERES;
            }
            reader.close();
        }catch(Exception e){
            System.out.println("Error al leer archivo");
        }

    }

}
