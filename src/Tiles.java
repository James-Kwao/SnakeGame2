public class Tiles {

    public static int tileSize = 35;
    int xPos, yPos, start, bodyShape;

    public Tiles(int xPos, int yPos, int start, int bodyShape) {
        this.xPos = xPos * tileSize;
        this.yPos = yPos * tileSize;
        this.start = start;
        this.bodyShape = bodyShape;
    }
}
