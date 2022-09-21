#include "parte1.c"
#include "parte2.c"

int main() {
    FILE *resultados = fopen("resultados.txt", "w");

    // PARTE 1
    fprintf(resultados, "---Parte 1--- \n");
    fprintf(resultados, "\n");
    
    double probabilidades[CANT_SIMBOLOS];
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

    // Inciso B
    double entropia = calcularEntropia(probabilidades);
    fprintf(resultados, "Entropia (Fuente Original): %f bits\n", entropia);
    fprintf(resultados, "Entropia (Extension Orden 20, n * H(S)): %f bits\n", entropia * 20);
    // fprintf(resultados, "Entropia (Extension Orden 20, calculada por extension): %f bits\n", entropiaExtension(probabilidades, 20));
    fprintf(resultados, "\n");

    
    // PARTE 2
    fprintf(resultados, "---Parte 2--- \n");
    fprintf(resultados, "\n");
    procesarCodigo(resultados, 3, entropia);
    procesarCodigo(resultados, 5, entropia);
    procesarCodigo(resultados, 7, entropia);
    
    fclose(resultados);
    return 0;
}