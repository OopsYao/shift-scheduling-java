package utils;

import java.util.Collection;
import java.util.HashMap;

/**
 * Dynamic sample space enabled
 */
public class Mahalanobis {
    private int n;

    private double[][] covInvMat;

    private double[] mean;

    private double[][] meanMat;

    private boolean isCovInvMatUpdated;

    //Intended for generating the matrix
    private int sampleSpaceSize;

    public Mahalanobis(Collection<double[]> sampleSpace) {
        for (double[] s : sampleSpace) {
            add(s);
        }
    }

    public Mahalanobis() {
    }

    public void add(double[] sample) {
        if (mean == null) {
            mean = new double[sample.length];
            meanMat = new double[sample.length][sample.length];
        } else if (mean.length != sample.length) {
            return;
        }

        //n
        n++;

        //update covariance matrix and mean
        sampleSpaceSize++;
        for (int i = 0; i < sample.length; i++) {
            mean[i] += (sample[i] - mean[i]) / sampleSpaceSize;
            for (int j = i; j < sample.length; j++) {
                meanMat[i][j] += (sample[i] * sample[j] - meanMat[i][j]) / sampleSpaceSize;
            }
        }

        //update flag
        isCovInvMatUpdated = false;
    }

    /**
     * Must be called after the sample space (or covariance matrix) is settled
     *
     * @param sample1 Sample 1
     * @param sample2 Sample 2
     * @return Mahalanobis distance
     */
    public double getDistance(double[] sample1, double[] sample2) {
        if (!isCovInvMatUpdated) generateInvCovMat();
        int len = sample1.length;
        if (len != sample2.length || !isCovInvMatUpdated) {
            return -1;
        }
        if (len == 1) {
            return (sample1[0] - sample2[0]) * (sample1[0] - sample2[0]);
        }

        double[] delta = new double[len];
        for (int i = 0; i < len; i++) {
            delta[i] = sample1[i] - sample2[i];
        }


        double quadratics = 0;
        for (int i = 0; i < len; i++) {
            for (int j = i; j < len; j++) {
                quadratics += delta[i] * delta[j] * covInvMat[i][j] * (j == i ? 1 : 2);
            }
        }
        return Math.sqrt(quadratics);
    }

    /**
     * Cal the distance between a sample and the mean
     *
     * @param sample The sample
     * @return The distance between the sample and mean
     */
    public double getDistance(double[] sample) {
        if (mean != null) {
            return getDistance(sample, mean);
        } else {
            return -1;
        }
    }

    /**
     * Generate the covariance matrix inverse
     */
    private void generateInvCovMat() {
        double bias = n / (double) (n - 1);
        int len = mean.length;
        //generate cov matrix
        double[][] covMat = new double[len][len];
        for (int i = 0; i < covMat.length; i++) {
            for (int j = 0; j < covMat.length; j++) {
                if (i > j) {
                    covMat[i][j] = bias * (meanMat[j][i] - mean[i] * mean[j]);
                } else {
                    covMat[i][j] = bias * (meanMat[i][j] - mean[i] * mean[j]);
                }
            }
        }
        //the inverse
        covInvMat = getInverseMatrix(covMat);
        //update flag
        isCovInvMatUpdated = true;
    }

    /**
     * Attention: the matrix passed in will be changed!
     *
     * @param m Matrix
     * @return Its inverse
     */
    private double[][] getInverseMatrix(double[][] m) {
        int len = m.length;
        //generate identity matrix
        double[][] identityMat = new double[len][len];
        for (int i = 0; i < identityMat.length; i++) {
            identityMat[i][i] = 1;
        }

        //order of sorting
        HashMap<Integer, Integer> realIndex = new HashMap<>();

        for (int i = 0; i < len; i++) {
            //column i
            for (int j = 0; j < len; j++) {
                if (!realIndex.containsValue(j) && m[j][i] != 0) {
                    //row j have a non-zero-i_th
                    realIndex.put(i, j);
                    //unit
                    double d = m[j][i];
                    divide(d, m[j]);
                    divide(d, identityMat[j]);

                    //zero other rows
                    for (int k = 0; k < len; k++) {
                        if (k != j) {
                            double dd = -m[k][i];
                            rowAddition(dd, m[k], m[j]);
                            rowAddition(dd, identityMat[k], identityMat[j]);
                        }
                    }
                    break;
                } else if (j == len - 1) {
                    return null;
                }
            }
        }

        //Remapping (row invert)
        double[][] inv = new double[len][len];
        for (int i = 0; i < len; i++) {
            inv[i] = identityMat[realIndex.get(i)];
        }
        return inv;
    }

    /**
     * Add a row multiply a double to another row
     *
     * @param k         The ratio
     * @param rowAugend The row will be changed
     * @param rowAddend The row won't be changed
     */
    private void rowAddition(double k, double[] rowAugend, double[] rowAddend) {
        for (int i = 0; i < rowAugend.length; i++) {
            rowAugend[i] += rowAddend[i] * k;
        }
    }

    /**
     * @param divisor The divisor
     * @param row     The row changed
     */
    private void divide(double divisor, double[] row) {
        if (divisor == 0 || row == null) return;
        for (int i = 0; i < row.length; i++) {
            row[i] /= divisor;
        }
    }


    private double[] convert(double[][] mat, double[] vec) {
        if (mat.length != mat[0].length && mat.length != vec.length) {
            return null;
        }
        int len = vec.length;
        double[] vecCopy = new double[len];
        for (int i = 0; i < len; i++) {
            vecCopy[i] = vec[i];
        }
        double[][] matCopy = new double[len][len];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                matCopy[i][j] = mat[i][j];
            }
        }

        HashMap<Integer, Integer> realIndex = new HashMap<>();

        for (int i = 0; i < len; i++) {
            //column i
            for (int j = 0; j < len; j++) {
                if (!realIndex.containsValue(j) && matCopy[j][i] != 0) {
                    //row j have a non-zero-i_th
                    realIndex.put(i, j);
                    //unit
                    double d = matCopy[j][i];
                    divide(d, matCopy[j]);
                    vecCopy[j] /= d;

                    //zero other rows
                    for (int k = 0; k < len; k++) {
                        if (k != j) {
                            double dd = -matCopy[k][i];
                            rowAddition(dd, matCopy[k], matCopy[j]);
                            vecCopy[k] += dd * vecCopy[j];
                        }
                    }
                    break;
                } else if (j == len - 1) {
                    return null;
                }
            }
        }

        //Remapping (row invert)
        double[] inv = new double[len];
        for (int i = 0; i < len; i++) {
            inv[i] = vecCopy[realIndex.get(i)];
        }

        return inv;
    }
}
