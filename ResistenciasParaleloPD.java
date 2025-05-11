
package com.mycompany.resistenciasparalelopd;

public class ResistenciasParaleloPD {
    
    //Clase para el almacenamiento de los resultados obtenidos de los subproblemas
    static class Results {
        double resistance; //Variable para la resistencia equivalente
        String path; //Variable para mostrar la agrupación óptima para obtener la resistencia equivalente
        int cost; // Variable para la complejidad del cálculo 

        Results(double resistance, String path, int cost) {
            this.resistance = resistance;
            this.path = path;
            this.cost = cost;
        }
    }
    
    //Función para la fórmula utilizada en el cálculo de suma de resistencias en paralelo
    public static double sumParalel(double r1, double r2) {
        return 1.0 / (1.0 / r1 + 1.0 / r2);
    }
    
    /* Función para la estimación de complejidad de un resultado, es decir, 
    dificultad de una fracción para ser operada manualmente. Es la función
    clave para el correcto funcionamiento del algoritmo */
    public static int computeComplexity(double value) {
        int maxDeno = 100; // Denominador máximo a provar, sirve para evitar fracciones complejas (denominador > 100)
        int bestNume = 1; // Variable para almacenar numerador de la mejor fracción encontrada
        int bestDen = 1; //Variable ara almacenar denominador de la mejor fracción encontrada
        double minError = Double.MAX_VALUE; //Variable para el error mínimo encontrado, mediante esto se determina que aproximación de fracción resulta más útil
        
        //Ciclo para la búsqueda de la fracción más óptima
        for (int deno = 1; deno <= maxDeno; deno++) {
            int nume = (int) Math.round(value * deno); //Variable para la elección de un númerador cuya combinación con el denominador de un resultado cercano o igual al original
            double error = Math.abs(value - ((double) nume / deno)); // Calculo del error de aproximación entre valor original y fracción actual
            //Si el error de la fracción actual es menor al de otra calculada previamente, entonces la fracción actual se guarda como la más óptima al momento
            if (error < minError) {
                minError = error;
                bestNume = nume;
                bestDen = deno;
            }
        }
        //Clasificación de complejidades de fracciones
        if (bestDen == 1) return 0; //Número entero complejidad fácil
        if (bestDen <= 9) return 1; // Fracciones con denominadores de una unidad complejidad media
        return 2; //Fracciones con denominadores grandes complejidad alta 
    }
    
    //Función para el almacenamiento de resultados (Programación dinámica) 
    public static Results[][] computePD(double[] R) {
        int n = R.length; 
        Results[][] dp = new Results[n][n]; //Creación de una matriz bidimencional tamaño nxn, cada celda almacena la mejor forma de agrupar las resistencia entre índices i y j
        
        //Ciclo para el llenado de la diagonal de la tabla, donde se almacenan los valores propios de las resistencias (casos base) 
        for (int i = 0; i < n; i++) {
            int cost = computeComplexity(R[i]);
            dp[i][i] = new Results(R[i], String.format("%.1f", R[i]), cost);
        }
        
        //Ciclos anadidado para la construcción de soluciones para los subproblemas, es decir, el rellenado de la parte superior de la tabla
        for (int len = 2; len <= n; len++) { //Primer ciclo, base para generar soluciones de subproblemas de longitud 2 a n, len es la longitud de un subarreglo
            for (int i = 0; i <= n - len; i++) { //Segundo ciclo, elección de conjuntos a evaluar en cada celda 
                int j = i + len - 1;
                dp[i][j] = new Results(Double.MAX_VALUE, "", Integer.MAX_VALUE); //Se inicializa una celda con valores no óptimos, sirve para comenzar la búsqueda de la mejor opción

                for (int k = i; k < j; k++) { //Se prueban cada una de las formas de dividir un subarreglo (parte izquierda y derecha) 
                    Results left = dp[i][k]; //Recursión para partición izquierda
                    Results right = dp[k + 1][j]; //Recursión para partición derecha
                    double combined = sumParalel(left.resistance, right.resistance); //Calculo de la resistencia equivalente
                    int currentCost = computeComplexity(combined) + left.cost + right.cost; //Calculo de la dificultad/costo acumulado (dificultad de fracción combinada + costo de agrupaciones previas)
                    
                    //Veificar si el costo de la agrupación actual es mejor que el costo guardado en dp[i][j] que en un inicio es el peor de los casos
                    if (currentCost < dp[i][j].cost) {
                        String newPath = "[" + left.path + " + " + right.path + "]"; //Variable para mostrar la agrupación de resistencias realizada
                        dp[i][j] = new Results(combined, newPath, currentCost); //Se actualiza el resultado en la celda dp[i][j]
                    }
                }
            }
        }

        return dp;
    }
    
    //Clase principal para la inicialización del algoritmo
    public static void main(String[] args) {
        //Definición de las resistencias en el circuito e inicialización de las funciones mediante estos valores
        double[] R = {10,20,30,40};
        Results[][] dp = computePD(R);
        Results finalResult = dp[0][R.length - 1]; //Recuperación de la solución óptima para el arreglo de resistencias
        
        //Impresión de resultados: Resistencia equivalente, agrupación óptima, dficultad/coste de las operaciones en la agrupación
        System.out.println("Resistencia equivalente: " + finalResult.resistance);
        System.out.println("Camino óptimo (agrupación de menor complejidad): " + finalResult.path);
        System.out.println("Costo de complejidad total: " + finalResult.cost);
    }
}
