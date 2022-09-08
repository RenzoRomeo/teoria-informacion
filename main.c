#include <stdio.h>
#include <math.h>

#define CANT_SIMBOLOS 3
#define CANT_CARACTERES 10000
#define DIF 0.01

void leerArchivo(char* nombreArchivo, double probabilidad[], double mat[][CANT_SIMBOLOS]) {
    FILE *arch = fopen(nombreArchivo, "r");
    if (arch == NULL) {
        printf("No se pudo abrir el archivo");
        return;
    }

    char ant = fgetc(arch);
    while (!feof(arch)) {
        probabilidad[ant - 'A']++;

        char sig = fgetc(arch);
        mat[sig - 'A'][ant - 'A']++;

        ant = sig;
    }

    for (int i = 0; i < CANT_SIMBOLOS; i++) {
        probabilidad[i] /= (double)CANT_CARACTERES;
    }

    for (int i = 0; i < CANT_SIMBOLOS; i++) {
        for (int j = 0; j < CANT_SIMBOLOS; j++) {
            mat[i][j] /= (double)(CANT_CARACTERES - 1);
        }
    }

    fclose(arch);
}

int esMemoriaNula(double probabilidades[] ,double mat[][CANT_SIMBOLOS]) {
    for (int i = 0; i < CANT_SIMBOLOS; i++) {
        for (int j = 0; j < CANT_SIMBOLOS; j++) {
            if (fabs(mat[j][i] - probabilidades[i] * probabilidades[j]) > DIF)
                return 0;
        }
    }
    return 1;
}

double calcularEntropia(double probabilidades[]) {
    double entropia = 0;
    for (int i = 0; i < CANT_SIMBOLOS; i++) {
        double prob = probabilidades[i];
        entropia += prob * (log(prob) / log(2.0));
    }
    return -entropia;
}

int main() {
    double probabilidades[CANT_SIMBOLOS];
    double mat[CANT_SIMBOLOS][CANT_SIMBOLOS] = {0};

    // Inciso A
    leerArchivo("datos.txt", probabilidades, mat);

    printf("Probabilidades de cada simbolo: \n");
    for (int i = 0; i < CANT_SIMBOLOS; i++)
        printf("%c: %f\n", i + 'A', probabilidades[i]);
    printf("\n");

    printf("Matriz de transicion: \n");
    for (int i = 0; i < CANT_SIMBOLOS; i++) {
        for (int j = 0; j < CANT_SIMBOLOS; j++) {
            printf("%f ", mat[i][j]);
        }
        printf("\n");
    }
    printf("\n");

    printf("La fuente es de memoria nula: %s\n", esMemoriaNula(probabilidades, mat) ? "SI" : "NO");

    // Inciso B
    double entropia = calcularEntropia(probabilidades);
    printf("Entropia (Fuente Original): %f bits\n", entropia);
    printf("Entropia (Extension Orden 20): %f bits\n", entropia * 20);
}