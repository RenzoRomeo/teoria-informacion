#include <stdio.h>
#include <math.h>
#include <stdlib.h>

#define CANT_SIMBOLOS 3
#define CANT_CARACTERES 10000
#define DIF 0.01

double entropiaExtension(float probabilidades[], int orden);

void leerArchivo(char *nombreArchivo, float probabilidad[], float mat[][CANT_SIMBOLOS]);

int esMemoriaNula(float probabilidades[], float mat[][CANT_SIMBOLOS]);

float calcularEntropia(float probabilidades[]);

int main()
{
    float probabilidades[CANT_SIMBOLOS];
    float mat[CANT_SIMBOLOS][CANT_SIMBOLOS] = {0};

    // Inciso A
    leerArchivo("datos.txt", probabilidades, mat);

    printf("Probabilidades de cada simbolo: \n");
    for (int i = 0; i < CANT_SIMBOLOS; i++)
        printf("%c: %f\n", i + 'A', probabilidades[i]);
    printf("\n");

    printf("Matriz de transicion: \n");
    for (int i = 0; i < CANT_SIMBOLOS; i++)
    {
        for (int j = 0; j < CANT_SIMBOLOS; j++)
        {
            printf("%f ", mat[i][j]);
        }
        printf("\n");
    }
    printf("\n");

    printf("La fuente es de memoria nula: %s\n\n", esMemoriaNula(probabilidades, mat) ? "SI" : "NO");

    // Inciso B
    float entropia = calcularEntropia(probabilidades);
    printf("Entropia (Fuente Original): %f bits\n", entropia);
    printf("Entropia (Extension Orden 20, n * H(S)): %f bits\n", entropia * 20);
    printf("Entropia (Extension Orden 20, calculada por extension): %f bits\n", entropiaExtension(probabilidades, 20));
}

double entropiaExtension(float probabilidades[], int orden)
{
    char *extension = (char *)malloc(sizeof(char) * orden);

    for (int i = 0; i < orden; i++)
    {
        extension[i] = 0;
    }

    unsigned int cantCiclos = (unsigned int)pow(CANT_SIMBOLOS, orden);

    double entropiaOrdenN = 0.0;

    // Para cada extension
    for (unsigned int i = 0; i < cantCiclos; i++)
    {
        double probabilidadExtension = 1.0;
        for (int j = 0; j < orden; j++)
        {
            probabilidadExtension *= probabilidades[extension[j]];
        }
        entropiaOrdenN += probabilidadExtension * (-log2(probabilidadExtension));

        // Incremento de la extension
        for (int j = 0; j < orden; j++)
        {
            if (extension[j] < CANT_SIMBOLOS - 1)
            {
                extension[j]++;
                break;
            }
            else
            {
                extension[j] = 0;
            }
        }
    }

    free(extension);

    return entropiaOrdenN;
}

void leerArchivo(char *nombreArchivo, float probabilidad[], float mat[][CANT_SIMBOLOS])
{
    FILE *arch = fopen(nombreArchivo, "r");
    if (arch == NULL)
    {
        printf("No se pudo abrir el archivo");
        return;
    }

    char ant = fgetc(arch);
    while (!feof(arch))
    {
        probabilidad[ant - 'A']++;

        char sig = fgetc(arch);
        mat[sig - 'A'][ant - 'A']++;

        ant = sig;
    }

    int sumas[3] = {0};
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
            mat[i][j] /= (float)(sumas[j]);
        }
    }

    for (int i = 0; i < CANT_SIMBOLOS; i++)
    {
        probabilidad[i] /= (float)CANT_CARACTERES;
    }

    fclose(arch);
}

int esMemoriaNula(float probabilidades[], float mat[][CANT_SIMBOLOS])
{
    for (int i = 0; i < CANT_SIMBOLOS; i++)
    {
        for (int j = 0; j < CANT_SIMBOLOS; j++)
        {
            if (fabs(mat[i][j] - probabilidades[i]) > DIF)
                return 0;
        }
    }
    return 1;
}

float calcularEntropia(float probabilidades[])
{
    float entropia = 0;
    for (int i = 0; i < CANT_SIMBOLOS; i++)
    {
        float prob = probabilidades[i];
        entropia += prob * (log(prob) / log(2.0));
    }
    return -entropia;
}
