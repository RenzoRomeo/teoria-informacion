#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>

#define CANT_SIMBOLOS 3

void calcularProbabilidades(const char *nombreArchivo, int tamanoPalabra, float probabilidades[], int cantPalabras);
void indiceAPalabra(int indice, char *palabra, int tamanoPalabra);
int palabraAIndice(char *palabra);
double calcularInformacion(char *palabra, float probabilidades[]);
double calcularEntropia(float probabilidades[], int cantPalabras);
void mostrarInformacionYEntropia(float probabilidades[], int cantPalabras, int tamanoPalabra);

int main()
{
    int cantPalabras3 = (int)pow(CANT_SIMBOLOS, 3);
    float *probabilidadesLongitud3 = (float *)calloc(cantPalabras3, sizeof(float));
    calcularProbabilidades("datos.txt", 3, probabilidadesLongitud3, cantPalabras3);
    mostrarInformacionYEntropia(probabilidadesLongitud3, cantPalabras3, 3);
    free(probabilidadesLongitud3);

    int cantPalabras5 = (int)pow(CANT_SIMBOLOS, 5);
    float *probabilidadesLongitud5 = (float *)calloc(cantPalabras5, sizeof(float));
    calcularProbabilidades("datos.txt", 5, probabilidadesLongitud5, cantPalabras5);
    mostrarInformacionYEntropia(probabilidadesLongitud5, cantPalabras5, 5);
    free(probabilidadesLongitud5);

    int cantPalabras7 = (int)pow(CANT_SIMBOLOS, 7);
    float *probabilidadesLongitud7 = (float *)calloc(cantPalabras7, sizeof(float));
    calcularProbabilidades("datos.txt", 7, probabilidadesLongitud7, cantPalabras7);
    mostrarInformacionYEntropia(probabilidadesLongitud7, cantPalabras7, 7);
    free(probabilidadesLongitud7);
}

// Calcula el índice de la palabra.
int palabraAIndice(char *palabra)
{
    int indice = 0;
    int tamanoPalabra = strlen(palabra);
    for (int i = 0; i < tamanoPalabra; i++)
    {
        indice += (palabra[i] - 'A') * (int)pow(CANT_SIMBOLOS, tamanoPalabra - i - 1);
    }
    return indice;
}

// Obtiene la palabra en base al índice.
void indiceAPalabra(int indice, char *palabra, int tamanoPalabra)
{
    for (int i = 0; i < tamanoPalabra; i++)
    {
        int potencia = (int)pow(CANT_SIMBOLOS, tamanoPalabra - i - 1);
        int letra = indice / potencia;
        palabra[i] = letra + 'A';
        indice -= letra * potencia;
    }
}

// Calcula las probabilidades de cada palabra.
// Itera sobre el archivo y cuenta la cantidad de veces que aparece cada palabra.
// Luego calcula la probabilidad de cada palabra dividiendo la cantidad de veces que aparece
// por la cantidad total de palabras.
void calcularProbabilidades(const char *nombreArchivo, int tamanoPalabra, float probabilidades[], int cantPalabras)
{
    FILE *archivo = fopen(nombreArchivo, "r");
    if (archivo == NULL)
    {
        printf("No se pudo abrir el archivo.");
        exit(1);
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
            probabilidades[indice]++;
        }
    }
    free(palabra);
    fclose(archivo);

    int suma = 0;
    for (int i = 0; i < cantPalabras; i++)
    {
        suma += probabilidades[i];
    }

    for (int i = 0; i < cantPalabras; i++)
    {
        probabilidades[i] /= suma;
    }
}

// Calcula la información de una palabra.
// Pre: La probabilidad de la palabra es mayor a 0.
double calcularInformacion(char *palabra, float probabilidades[])
{
    int indice = palabraAIndice(palabra);
    return -log2(probabilidades[indice]);
}

// Calcula la entropía de una fuente.
double calcularEntropia(float probabilidades[], int cantPalabras)
{
    double entropia = 0;
    for (int i = 0; i < cantPalabras; i++)
    {
        double informacion = 0.0;
        if (probabilidades[i] != 0.0)
        {
            informacion = -log2(probabilidades[i]);
        }
        entropia += probabilidades[i] * informacion;
    }
    return entropia;
}

// Muestra la información y la probabilidad de cada palabra y la entropia de la fuente.
void mostrarInformacionYEntropia(float probabilidades[], int cantPalabras, int tamanoPalabra)
{
    printf("Palabra | Probabilidad | Informacion\n");
    char *palabra = (char *)malloc(sizeof(char) * (tamanoPalabra + 1));
    for (int i = 0; i < cantPalabras; i++)
    {
        if (probabilidades[i] > 0.0)
        {
            indiceAPalabra(i, palabra, tamanoPalabra);
            printf("%s: %f (%f bits)\n", palabra, probabilidades[i], calcularInformacion(palabra, probabilidades));
        }
    }
    free(palabra);
    printf("\n");
    printf("Entropia: %f bits\n", calcularEntropia(probabilidades, cantPalabras));
    printf("\n");
}