package ru.swetophor;


public class ResoGrid {
    private final AstraOld[] takenPoints;
    private final ResonanceOld[][] grid;
    private final int orb;

    public ResoGrid(AstraOld[] points, int orb){
        takenPoints = points;
        this.orb = orb;
        grid = new ResonanceOld[takenPoints.length][takenPoints.length];
        for (int i = 0; i < takenPoints.length; i++)
            for (int j = 0; j < takenPoints.length; j++)
                grid[i][j] = new ResonanceOld(takenPoints[i], takenPoints[j]);
    }

    private void printArcs() {
        for (int i = 0; i < takenPoints.length; i++)
            for (int j = 0; j < takenPoints.length; j++)
                if (i == j) System.out.println("- \t -");
                else grid[i][j].printArc();
    }

    public static void main(String[] args) {
        AstraOld[] data = new AstraOld[3];
        data[0] = new AstraOld("Sol", 283, 15, 44);
        data[1] = new AstraOld("Luna", 253, 54, 5);
        data[2] = new AstraOld("Mars", 302, 58, 12);
        ResoGrid thisGrid = new ResoGrid(data, 12);
        thisGrid.printArcs();
    }

}
