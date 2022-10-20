import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Arrays;

public class PrimeraParte {

    public static double entropiaExtension(double[] probabilidades, int orden){
        int[] extension = new int[orden];
        Arrays.fill(extension, 0);

        long cantCiclos = (long)Math.pow(Principal.CANT_SIMBOLOS, orden);

        double entropiaOrdenN = 0.0;

        //Para cada extension

        for (long i = 0; i < cantCiclos; i++) {
            double probabilidadExtension = 1.0;
            for (int j = 0; j < orden; j++) {
                probabilidadExtension *= probabilidades[extension[j]];
            }
            entropiaOrdenN += probabilidadExtension * (-(Math.log(probabilidadExtension) / Math.log(Principal.CANT_SIMBOLOS)));

            // Incrementar extension
            int j = 0;
            while (j < orden && !(extension[j] < Principal.CANT_SIMBOLOS - 1)) {
                extension[j] = 0;
                j++;
            }
            if (j < orden) {
                extension[j]++;
            }
        }

        return entropiaOrdenN;
    }
    public static void leerArchivo(File file, double[] probabilidades, double[][] mat) {
        try {
            Reader reader = new FileReader(file);
            int ant = reader.read();
            int sig;
            while ((sig = reader.read()) != -1) {
                probabilidades[ant - 'A']++;

                mat[sig - 'A'][ant - 'A']++;

                ant = sig;
            }

            if (ant != -1){
                probabilidades[ant - 65]++;
            }

            int[] sumas = new int[Principal.CANT_SIMBOLOS];
            Arrays.fill(sumas, 0);
            for (int i = 0; i < Principal.CANT_SIMBOLOS; i++)
            {
                for (int j = 0; j < Principal.CANT_SIMBOLOS; j++)
                {
                    sumas[j] += mat[i][j];
                }
            }

            for (int i = 0; i < Principal.CANT_SIMBOLOS; i++)
            {
                for (int j = 0; j < Principal.CANT_SIMBOLOS; j++)
                {
                    mat[i][j] /= sumas[j];
                }
            }

            for (int i = 0; i < Principal.CANT_SIMBOLOS; i++)
            {
                probabilidades[i] /= Principal.CANT_CARACTERES;
            }
            reader.close();
        }catch(Exception e) {
            System.out.println("Error al leer archivo");
        }
    }
    public static boolean esMemoriaNula(double[] probabilidades, double[][] mat){
        for (int i = 0; i < Principal.CANT_SIMBOLOS; i++)
        {
            for (int j = 0; j < Principal.CANT_SIMBOLOS; j++)
            {
                if (Math.abs(mat[i][j] - probabilidades[i]) > Principal.DIF)
                    return false;
            }
        }
        return true;
    }

    public static double calcularEntropia(double[] probabilidades){
        double entropia = 0;
        for (int i = 0; i < Principal.CANT_SIMBOLOS; i++)
        {
            double prob = probabilidades[i];
            entropia += prob * (Math.log(prob) / Math.log(Principal.CANT_SIMBOLOS));
        }
        return -entropia;
    }

}
