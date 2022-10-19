#include <sys/stat.h>
#include <sys/types.h>

#include "parte1.c"
#include "parte2.c"

int mkdir(const char *pathname, int mode);

int main(int argc, char *argv[])
{
    // Si no existe la carpeta "salida", crearla
    struct stat st = {0};
    if (stat("salidas", &st) == -1)
    {
        mkdir("salidas", 0777);
    }

    FILE *resultados = fopen("./salidas/parte1_incisoA.txt", "w");

    int calcularEntropiaOrden20 = 0;

    // Buscar flag -o
    for (int i = 1; i < argc; i++)
    {
        if (strcmp(argv[i], "-o") == 0)
        {
            calcularEntropiaOrden20 = 1;
        }
    }

    // PARTE 1
    double probabilidades[CANT_SIMBOLOS] = {0};
    double mat[CANT_SIMBOLOS][CANT_SIMBOLOS] = {0};

    // Inciso A
    leerArchivo("datos.txt", probabilidades, mat);

    fprintf(resultados, "Probabilidades de cada simbolo: \n");
    for (int i = 0; i < CANT_SIMBOLOS; i++)
        fprintf(resultados, "%c: %f\n", i + 'A', probabilidades[i]);
    fprintf(resultados, "\n");

    fprintf(resultados, "Matriz de transicion: \n");
    for (int i = 0; i < CANT_SIMBOLOS; i++)
    {
        for (int j = 0; j < CANT_SIMBOLOS; j++)
        {
            fprintf(resultados, "%f ", mat[i][j]);
        }
        fprintf(resultados, "\n");
    }
    fprintf(resultados, "\n");

    fprintf(resultados, "La fuente es de memoria nula: %s\n\n", esMemoriaNula(probabilidades, mat) ? "SI" : "NO");

    fclose(resultados);

    // Inciso B
    resultados = fopen("./salidas/parte1_incisoB.txt", "w");
    double entropia = calcularEntropia(probabilidades);
    fprintf(resultados, "Entropia (Fuente Original): %f\n", entropia);
    fprintf(resultados, "Entropia (Extension Orden 20, n * H(S)): %f\n", entropia * 20);

    if (calcularEntropiaOrden20)
    {
        fprintf(resultados, "Entropia (Extension Orden 20, calculada por extension): %f\n", entropiaExtension(probabilidades, 20));
    }

    fclose(resultados);

    // PARTE 2
    procesarCodigo(3);
    procesarCodigo(5);
    procesarCodigo(7);

    printf("Programa ejecutado con exito \n");

    return 0;
}