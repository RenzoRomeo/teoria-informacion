#include <stdio.h>
#include <stdlib.h>
#include <math.h>

double informacion(const char *nombreArchivo, int tamanoPalabra, float probabilidades[]);
int palabraAIndice(char *palabra);

int main()
{
}

int palabraAIndice(char *palabra)
{
    int tamanoPalabra = strlen(palabra);
    int indice = 0;
    for (int i = 0; i < tamanoPalabra; i++)
    {
        indice += (palabra[i] - 'A') * pow(tamanoPalabra, i);
    }
    return indice;
}

// Probabilidades esta inicializado
double informacion(const char *nombreArchivo, int tamanoPalabra, float probabilidades[])
{
    FILE *archivo = fopen(nombreArchivo, "r");
    if (archivo == NULL)
    {
        printf("No se pudo abrir el archivo.");
        return 0;
    }

    int cont = 0;
    char *palabra = malloc(sizeof(char) * tamanoPalabra);

    while (!feof(archivo))
    {
        palabra[cont] = fgetc(archivo);
        cont++;

        if (cont == tamanoPalabra)
        {
            cont = 0;
            int indice = palabraAIndice(palabra);
        }
    }

    free(palabra);
}