#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>

#define CANT_SIMBOLOS 3
#define DIF 0.01

typedef struct
{
    double probabilidad;
    char *palabra;
} Codigo;

void calcularProbabilidades(const char *nombreArchivo, int tamanoPalabra,  Codigo codigos[], int cantPalabras);
void indiceAPalabra(int indice, char *palabra, int tamanoPalabra);
int palabraAIndice(Codigo codigos[], int cantPalabras, char *palabra);
double calcularInformacion(double probabilidad);
double calcularEntropiaFuente(Codigo codigos[], int cantPalabras);
void mostrarInformacion(FILE *resultados, Codigo codigos[], int cantPalabras, int tamanoPalabra);
int cumpleKraft(int longitudes[], int cantidadPalabras);
double calcularLongitudMedia(Codigo codigos[], int longitudes[], int cantPalabras);
int esCodigoCompacto(Codigo codigos[], int longitudes[], int cantidadPalabras);
void procesarCodigo(FILE *resultados, int longitudExtension, double entropiaOriginal);
double calcularRendimiento(double entropia, double longitudMedia);
double calcularRedundancia(double entropia, double longitudMedia);
void huffman(Codigo probabilidades[], int cantidad, char *codigoHuffman[]);
int compararCodigos(const void *a, const void *b);

void procesarCodigo(FILE *resultados, int longitudExtension, double entropiaOriginal) {
    // Cantidad de palabras del codigo
    int cantPalabras;
    // Almacena la palabra codigo junto con su probabilidad
    Codigo *codigos;
    double entropia;
    // Almacena la longitud de cada palabra codigo
    int *longitudes;
    double longitudMedia;

    fprintf(resultados, "Codigo de longitud %d \n", longitudExtension);
    fprintf(resultados, "\n");

    cantPalabras = (int)pow(CANT_SIMBOLOS, longitudExtension);

    // Armar vector de codigos (probabilidad y palabra)
    codigos = (Codigo*)malloc(sizeof(Codigo) * cantPalabras);
    for (int i = 0; i < cantPalabras; i++)
    {
        codigos[i].probabilidad = 0;
        codigos[i].palabra = (char*)malloc(sizeof(char) * (longitudExtension + 1));
        indiceAPalabra(i, codigos[i].palabra, longitudExtension);
    }
    
    calcularProbabilidades("datos.txt", longitudExtension, codigos, cantPalabras);
    mostrarInformacion(resultados, codigos, cantPalabras, longitudExtension);

    fprintf(resultados, "Resultados para el codigo de longitud %d \n", longitudExtension);
    fprintf(resultados, "\n");
    entropia = calcularEntropiaFuente(codigos, cantPalabras);
    fprintf(resultados, "Entropia: %f \n", entropia);

    longitudes = (int *)malloc(sizeof(int)*cantPalabras);
    for(int i = 0; i < cantPalabras; i++)
    {
        longitudes[i] = longitudExtension;
    }
    fprintf(resultados, "Cumple inecuacion de Kraft y Macmillan para codigo de longitud %d: %s \n", longitudExtension, cumpleKraft(longitudes, cantPalabras) ? "SI" : "NO");
    
    longitudMedia = calcularLongitudMedia(codigos, longitudes, cantPalabras);
    fprintf(resultados, "Longitud media del codigo de longitud %d: %f \n", longitudExtension, longitudMedia);
    
    fprintf(resultados, "El codigo es compacto: %s \n", esCodigoCompacto(codigos, longitudes, cantPalabras) ? "SI" : "NO");
    
    fprintf(resultados, "Rendimiento: %f \n", calcularRendimiento(entropiaOriginal, longitudMedia));
    fprintf(resultados, "Redundancia: %f \n", calcularRedundancia(entropiaOriginal, longitudMedia));
    fprintf(resultados, "\n");
    fprintf(resultados, "\n");


    qsort(codigos, cantPalabras, sizeof(Codigo), compararCodigos);

    char **codigoHuffman = (char**)malloc(sizeof(char*) * cantPalabras);

    huffman(codigos, cantPalabras, codigoHuffman);

    fprintf(resultados, "Codigo Huffman\n\nPalabra | Codigo\n");
    for (int i = 0; i < cantPalabras; i++)
    {
        fprintf(resultados, "%s -> %s\n", codigos[i].palabra, codigoHuffman[i]);
    }
    fprintf(resultados, "\n\n");


    free(longitudes);
    free(probabilidades);

    for (int i = 0; i < cantPalabras; i++)
    {
        free(codigos[i].palabra);
    }
    free(codigos);
    for(int i = 0; i < cantPalabras; i++)
    {
        free(codigoHuffman[i]);
    }
    free(codigoHuffman);
}

// Busca la palabra dentro de los codigos.
int palabraAIndice(Codigo codigos[], int cantPalabras, char *palabra)
{
    for (int i = 0; i < cantPalabras; i++)
    {
        if (strcmp(codigos[i].palabra, palabra) == 0)
        {
            return i;
        }
    }
    return -1;
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
void calcularProbabilidades(const char *nombreArchivo, int tamanoPalabra,  Codigo codigos[], int cantPalabras)
{
    FILE *archivo = fopen(nombreArchivo, "r");
    if (archivo == NULL)
    {
        printf("No se pudo abrir el archivo.");
        exit(1);
    }

    // Cuenta la cantidad de caracteres de la palabra. 
    // Si es igual al tamaño de la palabra, se suma 1 a la cantidad de veces que aparece la palabra.
    int cont = 0;

    // Acumula la cantidad de palabras que aparecen en el archivo.
    int apariciones = 0;
    char *palabra = malloc(sizeof(char) * (tamanoPalabra + 1));

    while (!feof(archivo))
    {
        palabra[cont] = fgetc(archivo);
        cont++;

        if (cont == tamanoPalabra)
        {
            palabra[cont] = '\0';
            cont = 0;
            int indice = palabraAIndice(codigos, cantPalabras, palabra);
            codigos[indice].probabilidad++;
            apariciones++;
        }
    }

    free(palabra);
    fclose(archivo);

    for (int i = 0; i < cantPalabras; i++)
    {
        codigos[i].probabilidad /= apariciones;
    }
}

// Calcula la información en base a una probabilidad.
double calcularInformacion(double probabilidad)
{
    double informacion = 0.0;
    
    if (probabilidad > 0.0)
    {
        informacion = -log2(probabilidad);
    }

    return informacion;
}

// Calcula la entropía de una fuente.
double calcularEntropiaFuente(Codigo codigos[], int cantPalabras)
{
    double entropia = 0;
    for (int i = 0; i < cantPalabras; i++)
    {
        Codigo codigo = codigos[i];
        double informacion = calcularInformacion(codigo.probabilidad);
        entropia += codigo.probabilidad * informacion;
    }
    return entropia;
}

// Muestra la información y la probabilidad de cada palabra.
void mostrarInformacion(FILE *resultados, Codigo codigos[], int cantPalabras, int tamanoPalabra)
{
    fprintf(resultados, "Palabra | Probabilidad | Informacion\n");

    for (int i = 0; i < cantPalabras; i++)
    {
        Codigo codigo = codigos[i];
        if (codigo.probabilidad > 0.0)
        {
            fprintf(resultados, "%s: %f (%f bits)\n", codigo.palabra, codigo.probabilidad, calcularInformacion(codigo.probabilidad));
        }
    }

    fprintf(resultados, "Las palabras que no aparecen tienen probabilidad 0.0 \n");
    fprintf(resultados, "\n");
}

int cumpleKraft(int longitudes[], int cantPalabras)
{
    double kraft = 0.0;
    for(int i = 0; i < cantPalabras; i++)
    {
        kraft += pow(CANT_SIMBOLOS, (-longitudes[i])); 
    }
    
    return kraft <= 1.0 + DIF;
}

double calcularLongitudMedia(Codigo codigos[], int longitudes[], int cantPalabras)
{
    double longitudMedia = 0.0;
    for (int i = 0; i < cantPalabras; i++)
    {
        longitudMedia += codigos[i].probabilidad * longitudes[i];
    }

    return longitudMedia;
}


// Verifica si el codigo es compacto.
int esCodigoCompacto(Codigo codigos[], int longitudes[], int cantidadPalabras)
{
    for(int i = 0; i < cantidadPalabras; i++)
    {
        if(fabs(codigos[i].probabilidad - pow(CANT_SIMBOLOS, (-longitudes[i]))) > 0.001)
        {
            return 0;
        }
    }

    return 1;
}

double calcularRendimiento(double entropiaFuente, double longitudMedia)
{
    return entropiaFuente / longitudMedia;
}

double calcularRedundancia(double entropiaFuente, double longitudMedia)
{
    return (longitudMedia - entropiaFuente) / longitudMedia;
}

// Pre: Las probabilidades están ordenadas descendentemente
void huffman(Codigo probabilidades[], int cantidad, char *codigoHuffman[])
{
    if (cantidad == 2)
    {
        char *palabra1 = (char *)malloc(sizeof(char) * 2);
        char *palabra2 = (char *)malloc(sizeof(char) * 2);
        sprintf(palabra1, "%s", "0");
        sprintf(palabra2, "%s", "1");
        codigoHuffman[0] = palabra1;
        codigoHuffman[1] = palabra2;
    }
    else
    {
        double ultimaProbabilidad = probabilidades[cantidad - 1].probabilidad;
        probabilidades[cantidad - 2].probabilidad += ultimaProbabilidad;
        huffman(probabilidades, cantidad - 1, codigoHuffman);
        probabilidades[cantidad - 2].probabilidad -= ultimaProbabilidad;

        char *palabra1 = (char *)malloc(sizeof(char) * (strlen(codigoHuffman[cantidad - 2]) + 2));
        char *palabra2 = (char *)malloc(sizeof(char) * (strlen(codigoHuffman[cantidad - 2]) + 2));

        sprintf(palabra1, "%s%s", codigoHuffman[cantidad - 2], "0");
        sprintf(palabra2, "%s%s", codigoHuffman[cantidad - 2], "1");

        free(codigoHuffman[cantidad - 2]);

        codigoHuffman[cantidad - 2] = palabra1;
        codigoHuffman[cantidad - 1] = palabra2;
    }
}

int compararCodigos(const void *a, const void *b)
{
    int resultado = 0;
    double probabilidadA = ((Codigo*)a)->probabilidad;
    double probabilidadB = ((Codigo*)b)->probabilidad;

    if (probabilidadA < probabilidadB)
    {
        resultado = 1;
    }
    else if (probabilidadA > probabilidadB)
    {
        resultado = -1;
    }
    return resultado;
}