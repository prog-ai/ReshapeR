
import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class SeamCarver {

    private Picture pic;
    private int width, height;
    private double[][] energy;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        validate(picture);
        pic = new Picture(picture);
        width = pic.width();
        height = pic.height();
        energy = new double[width][height];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                energy[x][y] = calcEnergy(x, y);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        int[] seam = new int[width];
        double distance = Double.POSITIVE_INFINITY;
        double[][] distTo = new double[width][height];
        Point[][] edgeTo = new Point[width][height];


        for (int col = 0; col < width; col++)
            for (int row = 0; row < height; row++)
                if (col == 0) distTo[col][row] = 0;
                else distTo[col][row] = Double.POSITIVE_INFINITY;


        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                Point p = new Point(col, row);

                for (Point adj : getAdjHoriz(p)) {
                    if (distTo[adj.x][adj.y] > distTo[p.x][p.y] + energy[p.x][p.y]) {
                        distTo[adj.x][adj.y] = distTo[p.x][p.y] + energy[p.x][p.y];
                        edgeTo[adj.x][adj.y] = p;
                        if (adj.x == width - 1 && distTo[adj.x][adj.y] < distance) {
                            distance = distTo[adj.x][adj.y];
                            for (int count = width; adj != null; adj = edgeTo[adj.x][adj.y])
                                seam[--count] = adj.y;
                        }
                    }
                }

            }
        }
        return seam;
    }

    private class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private ArrayList<Point> getAdjHoriz(Point p) {
        ArrayList<Point> adj = new ArrayList<>(3);
        if (p.x < width - 1) {
            if (p.y > 0) adj.add(new Point(p.x + 1, p.y - 1));
            if (p.y < height - 1) adj.add(new Point(p.x + 1, p.y + 1));
            adj.add(new Point(p.x + 1, p.y));
        }
        return adj;
    }

    private ArrayList<Point> getAdjVert(Point p) {
        ArrayList<Point> adj = new ArrayList<>(3);
        if (p.y < height - 1) {
            if (p.x > 0) adj.add(new Point(p.x - 1, p.y + 1));
            if (p.x < width - 1) adj.add(new Point(p.x + 1, p.y + 1));
            adj.add(new Point(p.x, p.y + 1));
        }
        return adj;
    }

    // current picture
    public Picture picture() {
        return new Picture(pic);
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        validate(x, y);
        return energy[x][y];
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        validateHorizSeam(seam);
        Picture newPic = new Picture(width, --height);
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                if (row < seam[col]) {
                    newPic.set(col, row, pic.get(col, row));
                    energy[col][row] = energy[col][row];
                }
                else {
                    newPic.set(col, row, pic.get(col, row + 1));
                    energy[col][row] = energy[col][row + 1];
                }
            }
        }

        pic = newPic;

        for (int col = 0; col < width; col++) {
            if (seam[col] > 0) energy[col][seam[col] - 1] = calcEnergy(col, seam[col] - 1);
            if (seam[col] < height - 1) energy[col][seam[col]] = calcEnergy(col, seam[col]);
        }
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {

        validateVertSeam(seam);
        Picture newPic = new Picture(--width, height);
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                if (col < seam[row]) {
                    newPic.set(col, row, pic.get(col, row));
                    energy[col][row] = energy[col][row];
                }
                else {
                    newPic.set(col, row, pic.get(col + 1, row));
                    energy[col][row] = energy[col + 1][row];
                }
            }
        }

        pic = newPic;

        for (int row = 0; row < height; row++) {
            if (seam[row] > 0) energy[seam[row] - 1][row] = calcEnergy(seam[row] - 1, row);
            if (seam[row] < width - 1) energy[seam[row]][row] = calcEnergy(seam[row], row);
        }

    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[] seam = new int[height];
        double distance = Double.POSITIVE_INFINITY;
        double[][] distTo = new double[width][height];
        Point[][] edgeTo = new Point[width][height];

        for (int col = 0; col < width; col++)
            for (int row = 0; row < height; row++)
                if (row == 0) distTo[col][row] = 0;
                else distTo[col][row] = Double.POSITIVE_INFINITY;


        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Point p = new Point(col, row);

                for (Point adj : getAdjVert(p)) {
                    if (distTo[adj.x][adj.y] > distTo[p.x][p.y] + energy[p.x][p.y]) {
                        distTo[adj.x][adj.y] = distTo[p.x][p.y] + energy[p.x][p.y];
                        edgeTo[adj.x][adj.y] = p;
                        if (adj.y == height - 1 && distTo[adj.x][adj.y] < distance) {
                            distance = distTo[adj.x][adj.y];
                            for (int count = height; adj != null; adj = edgeTo[adj.x][adj.y])
                                seam[--count] = adj.x;
                        }
                    }
                }

            }
        }
        return seam;

    }

    // to remove
    private void transpose() {
        // StdOut.println("Transposing now:   +++++++++++++++++");
        double[][] enr = energy;
        Picture oldPic = new Picture(pic);
        int tmp = height;
        height = width;
        width = tmp;
        pic = new Picture(width, height);
        energy = new double[width][height];
        for (int col = 0; col < width; col++)
            for (int row = 0; row < height; row++) {
                pic.set(col, row, oldPic.get(row, col));
                energy[col][row] = enr[row][col];
            }
    }


    private double calcEnergy(int x, int y) {
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) return 1000;

        int xRight = pic.getRGB(x + 1, y);
        int xLeft = pic.getRGB(x - 1, y);
        int yUp = pic.getRGB(x, y + 1);
        int yDown = pic.getRGB(x, y - 1);

        double xRed = (xRight >> 16) - (xLeft >> 16);
        double xGreen = ((xRight >> 8) & 255) - ((xLeft >> 8) & 255);
        double xBlue = (xRight & 255) - (xLeft & 255);

        double yRed = (yUp >> 16) - (yDown >> 16);
        double yGreen = ((yUp >> 8) & 255) - ((yDown >> 8) & 255);
        double yBlue = (yUp & 255) - (yDown & 255);

        double xGrad = xRed * xRed + xGreen * xGreen + xBlue * xBlue;
        double yGrad = yRed * yRed + yGreen * yGreen + yBlue * yBlue;

        double energy = Math.sqrt(xGrad + yGrad);

        return energy;
    }


    private void validate(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();
    }

    private void validate(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) throw new IllegalArgumentException();
    }

    private void validateHorizSeam(int[] seam) {
        if (seam == null || height < 2 || seam.length != width || seam[0] < 0 || seam[0] >= height)
            throw new IllegalArgumentException();

        for (int i = 1; i < seam.length; i++)
            if (seam[i] < 0 || seam[i] >= height || Math.abs(seam[i] - seam[i - 1]) > 1)
                throw new IllegalArgumentException();
    }

    private void validateVertSeam(int[] seam) {
        if (seam == null || width < 2 || seam.length != height || seam[0] < 0 || seam[0] >= width)
            throw new IllegalArgumentException();

        for (int i = 1; i < seam.length; i++)
            if (seam[i] < 0 || seam[i] >= width || Math.abs(seam[i] - seam[i - 1]) > 1)
                throw new IllegalArgumentException();
    }

    // test client
    public static void main(String[] args) {

        if (args.length != 3) {
            StdOut.println(
                    "Usage:\njava dima.java.seamcarver.ResizeDemo [image filename] [num cols to remove] [num rows to remove]");
            return;
        }
        Picture inputImg = new Picture(args[0]);
        int removeColumns = Integer.parseInt(args[1]);
        int removeRows = Integer.parseInt(args[2]);
        StdOut.printf("image is %d columns by %d rows\n", inputImg.width(), inputImg.height());
        SeamCarver sc = new SeamCarver(inputImg);
        // Stopwatch sw = new Stopwatch();
        // sc.printp();
        // int[] verticalSeam = { 3, 4, 3, 2, 1 };
        // sc.removeVerticalSeam(verticalSeam);
        // for (int i = 0; i < removeRows; i++) {
        //     int[] horizontalSeam = sc.findHorizontalSeam();
        //     sc.removeHorizontalSeam(horizontalSeam);
        // }
        for (int i = 0; i < removeColumns; i++) {
            StdOut.println("A is equal to : " + i);
            int[] verticalSeam = sc.findVerticalSeam();
            sc.printseam(verticalSeam);
            sc.removeVerticalSeam(verticalSeam);
            StdOut.println("completed removal for : " + i);
        }
        // Picture outputImg = sc.picture();
        // StdOut.printf("new image size is %d columns by %d rows\n", sc.width(), sc.height());
        // StdOut.println("Resizing time: " + sw.elapsedTime() + " seconds.");
        // inputImg.show();
        // outputImg.show();
    }

    private void printp() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // System.out.print(String.format("#%6X ", col[col][row]);
            }
            StdOut.println();
        }
    }

    private void printseam(int[] seam) {
        StdOut.println();
        StdOut.print("seam[] = { ");
        for (int i : seam) {
            StdOut.print(i + ", ");
        }
        StdOut.println("}");

    }
}
