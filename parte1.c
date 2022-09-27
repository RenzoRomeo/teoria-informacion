#include <stdio.h>
#include <math.h>
#include <stdlib.h>

#define CANT_SIMBOLOS 3
#define CANT_CARACTERES 10000
#define DIF 0.01

double entropiaExtension(double probabilidades[], int orden);

void leerArchivo(const char *nombreArchivo, double probabilidad[], double mat[][CANT_SIMBOLOS]);

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
        for (int i = 0; i < orden; i++)
        {
            printf("%c", 'A' + extension[i]);
        }
        printf("\n");
        
        double probabilidadExtension = 1.0;
        for (int j = 0; j < orden; j++)
        {
            probabilidadExtension *= probabilidades[extension[j]];
        }
        entropiaOrdenN += probabilidadExtension * (-log2(probabilidadExtension));

        // Incrementar extension
        int j = 0;
        while (j < orden && !(extension[j] < CANT_SIMBOLOS - 1))
        {
            extension[j] = 0;
            j++;
        }
        extension[j]++;

        // Incremento de la extension
        //for (int j = 0; j < orden; j++)
        //{
        //    if (extension[j] < CANT_SIMBOLOS - 1)
        //    {
        //        extension[j]++;
        //        break;
        //    }
        //    else
        //    {
        //        extension[j] = 0;
        //    }
        //}
    }

    free(extension);

    return entropiaOrdenN;
}

void leerArchivo(const char *nombreArchivo, double probabilidad[], double mat[][CANT_SIMBOLOS])
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
            mat[i][j] /= (double)(sumas[j]);
        }
    }

    for (int i = 0; i < CANT_SIMBOLOS; i++)
    {
        probabilidad[i] /= (double)CANT_CARACTERES;
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
        entropia += prob * log2(prob);
    }
    return -entropia;
}