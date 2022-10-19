#include <stdio.h>
#include <math.h>
#include <stdlib.h>

#define CANT_SIMBOLOS 3
#define CANT_CARACTERES 10000
#define DIF 0.01

double entropiaExtension(double probabilidades[], int orden);

void leerArchivo(const char *nombreArchivo, double probabilidades[], double mat[][CANT_SIMBOLOS]);

int esMemoriaNula(double probabilidades[], double mat[][CANT_SIMBOLOS]);

double calcularEntropia(double probabilidades[]);

double entropiaExtension(double probabilidades[], int orden)
{
    // Calloc es equivalente a malloc, pero inicializa en 0 el vector.
    char *extension = (char *)calloc(orden, sizeof(char));

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
        entropiaOrdenN += probabilidadExtension * (-(log(probabilidadExtension) / log(CANT_SIMBOLOS)));

        // Incrementar extension
        int j = 0;
        while (j < orden && !(extension[j] < CANT_SIMBOLOS - 1))
        {
            extension[j] = 0;
            j++;
        }
        if (j < orden)
        {
            extension[j]++;
        }
    }

    free(extension);

    return entropiaOrdenN;
}

void leerArchivo(const char *nombreArchivo, double probabilidades[], double mat[][CANT_SIMBOLOS])
{
    FILE *arch = fopen(nombreArchivo, "r");
    if (arch == NULL)
    {
        printf("No se pudo abrir el archivo");
        return;
    }

    char ant = fgetc(arch);
    char sig;
    while ((sig = fgetc(arch)) != EOF)
    {
        probabilidades[ant - 'A']++;

        mat[sig - 'A'][ant - 'A']++;

        ant = sig;
    }
    if (ant != EOF)
    {
        probabilidades[ant - 'A']++;
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
            mat[i][j] /= (double)(sumas[j]);
        }
    }

    for (int i = 0; i < CANT_SIMBOLOS; i++)
    {
        probabilidades[i] /= (double)CANT_CARACTERES;
    }

    fclose(arch);
}

int esMemoriaNula(double probabilidades[], double mat[][CANT_SIMBOLOS])
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

double calcularEntropia(double probabilidades[])
{
    double entropia = 0;
    for (int i = 0; i < CANT_SIMBOLOS; i++)
    {
        double prob = probabilidades[i];
        entropia += prob * (log(prob) / log(CANT_SIMBOLOS));
    }
    return -entropia;
}