package ru.swetophor;


public class ResoGrid {
    private Astra[] takenPoints;
    private Resonance[][] grid;
    private int orbis;

    public ResoGrid(Astra[] points, int orbis){
        takenPoints = points;
        this.orbis = orbis;
        grid = new Resonance[takenPoints.length][takenPoints.length];
        for (int i = 0; i < takenPoints.length; i++)
            for (int j = 0; j < takenPoints.length; j++)
                grid[i][j] = new Resonance(takenPoints[i], takenPoints[j]);
    }

    private void printArcs() {
        for (int i = 0; i < takenPoints.length; i++)
            for (int j = 0; j < takenPoints.length; j++)
                if (i == j) System.out.println("- \t -");
                else grid[i][j].printArc();
    }

    public static void main(String[] args) {
        Astra[] data = new Astra[3];
        data[0] = new Astra("Sol", 283, 15, 44);
        data[1] = new Astra("Luna", 253, 54, 5);
        data[2] = new Astra("Mars", 302, 58, 12);
        ResoGrid thisGrid = new ResoGrid(data, 12);
        thisGrid.printArcs();
    }

}
