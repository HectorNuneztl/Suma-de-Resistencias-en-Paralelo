
package com.mycompany.resistenciasparalelorec;

public class ResistenciasParaleloRec {

    public static double sumParalel(double r1, double r2) {
        return 1.0 / (1.0 / r1 + 1.0 / r2);
    }

    public static double ResEqu(double[] R, int i, int j) {
        if (i == j) return R[i];

        double min = Double.MAX_VALUE;

        for (int k = i; k < j; k++) {
            double left = ResEqu(R, i, k);
            double right = ResEqu(R, k + 1, j);
            double combined = sumParalel(left, right);

            if (combined < min) {
                min = combined;
            }
        }

        return min;
    }

    public static void main(String[] args) {
        double[] R = {10, 20, 30, 40};
        double result = ResEqu(R, 0, R.length - 1);
        System.out.printf("Resistencia equivalente: " + result);
    }
}
