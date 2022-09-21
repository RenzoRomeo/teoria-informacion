#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>

#define CANT_SIMBOLOS 3
#define DIF 0.01

void calcularProbabilidades(const char *nombreArchivo, int tamanoPalabra,  double probabilidades[], int cantPalabras);
void indiceAPalabra(int indice, char *palabra, int tamanoPalabra);
int palabraAIndice(char *palabra);
double calcularInformacion(char *palabra,  double probabilidades[]);
double calcularEntropia( double probabilidades[], int cantPalabras);
void mostrarInformacionYEntropia( double probabilidades[], int cantPalabras, int tamanoPalabra);
int cumpleKraft(int longitudes[], int cantidadPalabras);
 double calcularLongitudMedia( double probabilidades[],int longitudes[],int cantidadPalabras);

int main()
{
    int cantPalabras;
    double *probabilidadesLongitud;
    double entropia;
    int *longitudes;
    double longitudMedia;


    cantPalabras = (int)pow(CANT_SIMBOLOS, 3);
    probabilidadesLongitud = (double *)calloc(cantPalabras, sizeof(double));
    calcularProbabilidades("datos.txt", 3, probabilidadesLongitud, cantPalabras);
    entropia = calcularEntropia(probabilidadesLongitud, cantPalabras);
    mostrarInformacionYEntropia(probabilidadesLongitud, cantPalabras, 3);
    

    longitudes = (int *)malloc(sizeof(int)*cantPalabras);
    for(int i = 0; i < cantPalabras; i++)
        longitudes[i] = 3;
    printf("Cumple inecuacion de Kraft y Macmillan para codigo de longitud 3: %s \n", cumpleKraft(longitudes, cantPalabras) ? "SI" : "NO");
    
    longitudMedia = calcularLongitudMedia(probabilidadesLongitud, longitudes, cantPalabras);
    printf("Longitud media del codigo: %f \n", longitudMedia);
    
    printf("El codigo es compacto: %s \n", (longitudMedia >= entropia) ? "SI" : "NO");
    free(longitudes);
    free(probabilidadesLongitud);

    // Pi = (1/r) ^ li -> r = 3 y li la longitud de la palabra

    cantPalabras  = (int)pow(CANT_SIMBOLOS, 5);
    probabilidadesLongitud  = (double *)calloc(cantPalabras , sizeof(double));
    calcularProbabilidades("datos.txt", 5, probabilidadesLongitud , cantPalabras );
    // mostrarInformacionYEntropia(probabilidadesLongitud , cantPalabras , 5);

    longitudes = (int *)malloc(sizeof(int)*cantPalabras );
    for(int i = 0; i < cantPalabras ; i++)
    {
       longitudes[i] = 5;
    }
    printf("Cumple inecuacion de Kraft para codigo de longitud 5: %s \n", cumpleKraft(longitudes, cantPalabras ) ? "SI" : "NO"); 
    
    printf("Longitud media del codigo: %f \n", calcularLongitudMedia(probabilidadesLongitud , longitudes, cantPalabras ));

    free(longitudes);
    free(probabilidadesLongitud);

    cantPalabras  = (int)pow(CANT_SIMBOLOS, 7);
    probabilidadesLongitud  = ( double *)calloc(cantPalabras , sizeof( double));
    calcularProbabilidades("datos.txt", 7, probabilidadesLongitud , cantPalabras );
    // mostrarInformacionYEntropia(probabilidadesLongitud , cantPalabras , 7);

    longitudes = (int *)malloc(sizeof(int)*cantPalabras );
    for(int i = 0; i < cantPalabras ; i++)
        longitudes[i] = 7;
    printf("Cumple inecuacion de Kraft para codigo de longitud 7: %s \n", cumpleKraft(longitudes, cantPalabras ) ? "SI" : "NO"); 
    
    printf("Longitud media del codigo: %f \n", calcularLongitudMedia(probabilidadesLongitud , longitudes, cantPalabras ));
    
    
    free(longitudes);
    free(probabilidadesLongitud );
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

    palabra[tamanoPalabra] = '\0'; 
}

// Calcula las probabilidades de cada palabra.
// Itera sobre el archivo y cuenta la cantidad de veces que aparece cada palabra.
// Luego calcula la probabilidad de cada palabra dividiendo la cantidad de veces que aparece
// por la cantidad total de palabras.
void calcularProbabilidades(const char *nombreArchivo, int tamanoPalabra,  double probabilidades[], int cantPalabras)
{
    FILE *archivo = fopen(nombreArchivo, "r");
    if (archivo == NULL)
    {
        printf("No se pudo abrir el archivo.");
        exit(1);
    }

    int cont = 0;
    char *palabra = malloc(sizeof(char) * (tamanoPalabra + 1));

    while (!feof(archivo))
    {
        palabra[cont] = fgetc(archivo);
        cont++;

        if (cont == tamanoPalabra)
        {
            palabra[cont] = '\0';
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
double calcularInformacion(char *palabra,  double probabilidades[])
{
    int indice = palabraAIndice(palabra);
    return -log2(probabilidades[indice]);
}

// Calcula la entropía de una fuente.
double calcularEntropia( double probabilidades[], int cantPalabras)
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
void mostrarInformacionYEntropia( double probabilidades[], int cantPalabras, int tamanoPalabra)
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

int cumpleKraft(int longitudes[], int cantidadPalabras)
{
    double kraft = 0.0;
    for(int i = 0; i < cantidadPalabras; i++)
    {
        kraft += pow(CANT_SIMBOLOS, (-longitudes[i])); 
    }
    
    return kraft <= 1.0 + DIF;
}



 double calcularLongitudMedia( double probabilidades[],int longitudes[],int cantidadPalabras)
{
    double longitudMedia = 0.0;
    for (int i = 0; i < cantidadPalabras; i++)
    {
        longitudMedia += probabilidades[i] * longitudes[i];
    }

    return longitudMedia;
}