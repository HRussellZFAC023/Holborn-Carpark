package QRCode;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generates a QR code from an inputted alphanumeric string converts characters to
 * capitals and any unknown characters are converted to 0's
 *
 * @author Cameron
 * @version 1.0.0
 */
public class QRCode extends Canvas {

    //Modes: 1) Numeric 2) Alphanumeric 3) Byte(Not supported yet) 4) kanji(Not supported yet)
    //Error level: 1) Low 2) Medium 3) Q(Basically 1 up from med) 4) High

    private int version;

    private int pixelSize = 2;
    private Color pixelColour = new Color(0, 0, 0, 1);
    private Color unusedPixel = new Color(1, 0, 0, 1);
    private ClassLoader cl;

    /**
     * Constructor which creates a class loader to read the resources
     *
     * @since 1.0.0
     */
    public QRCode(){
        cl = getClass().getClassLoader();
    }

    /**
     * Method which takes in the qr code array and writes the QR code onto a canvas
     *
     * @param qr the generated 2D {@link Pix} array
     * @since 1.0.0
     */
    private void drawQRCode(Pix[][] qr) {
        int shift = 4 * pixelSize;
        int size = (qr.length * pixelSize) + (shift * 2);
        this.setHeight(size);
        this.setWidth(size);
        GraphicsContext g = getGraphicsContext2D();
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        //drawGrid(g, size);
        PixelWriter p = g.getPixelWriter();
        for (int y = 0; y < qr.length; y++) {
            for (int x = 0; x < qr[y].length; x++) {
                if (qr[y][x] == null) drawPixel(y * pixelSize, x * pixelSize, p, unusedPixel, shift);
                else if (qr[y][x].getState()) drawPixel(y * pixelSize, x * pixelSize, p, pixelColour, shift);
            }
        }
    }

    //TODO remove this
    private void drawGrid(GraphicsContext g, int size) {
        double height = this.getHeight(), width = this.getWidth();
        for (int pos = 0; pos < size; pos++) {
            g.strokeLine(pos * pixelSize, 0, pos * pixelSize, height);
            g.strokeLine(0, pos * pixelSize, width, pos * pixelSize);
        }
    }

    /**
     * Method draws a pixel to the canvas of the desired size
     *
     * @param y The Row of the pixel
     * @param x The Column of the pixel
     * @param p The Pixel writer
     * @param pixelColour The colour that the pixel should be draws
     * @param shift The shift for the spacing round the QR code
     * @since 1.0.0
     */
    private void drawPixel(int y, int x, PixelWriter p, Color pixelColour, int shift) {
        for (int i = 0; i < pixelSize; i++) {
            for (int j = 0; j < pixelSize; j++) {
                p.setColor(x + i + shift, y + j + shift, pixelColour);
            }
        }
    }

    /**
     * The method that takes in the alphanumeric string and converts it to the QR code
     *
     * @param dataToEncode The alphanumeric string to convert to a QR code.
     * @param errorLevel The error level used in the QR code (1-4).
     * @param pixelSize The number of pixels used int he height and width of the QR code positions.
     * @since 1.0.0
     */
    public String generate(String dataToEncode, int errorLevel, int pixelSize) {
        this.pixelSize = pixelSize;
        if (errorLevel > 4) errorLevel = 4;
        if (errorLevel < 1) errorLevel = 1;
        String message = encode(dataToEncode, 2, errorLevel);
        Pix[][] QR = makeQR(message, errorLevel);
        drawQRCode(QR);
        return storeQR();
    }

    /**
     * Stores the QR code into a file to be accesd by the PDF writer
     *
     * @return The location of the QR code
     * @since 1.0.0
     */
    private String storeQR(){
        int pos = 0;
        String location = "Tickets/QR/QR" + pos + ".png";
        while(new File(location).exists()){
            pos++;
            location = "Tickets/QR/QR" + pos + ".png";
        }
        File file = new File(location);
        WritableImage image = this.snapshot(new SnapshotParameters(), null);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (Exception s) {
            s.printStackTrace();
        }
        return ("Tickets/QR/QR" + pos + ".png");
    }

    /**
     * Method which takes in the alphanumeric String and converts it to a 2D {@link Pix} array
     *
     * @param message The alphanumeric string which is being converted to the QR code
     * @param errLvl The level of error that the QR code is being generated to.
     * @return A 2D {@link Pix} array
     * @since 1.0.0
     */
    private Pix[][] makeQR(String message, int errLvl) {
        int[] versionStorage = new int[2];
        versionStorage[0] = errLvl;
        Pix[][] qr = new Pix[getQRSize()][getQRSize()];
        makeFinders(qr);
        addTimingBelts(qr);
        addDarkModule(qr);
        addAlignments(qr);
        reserveFormatArea(qr);
        boolean[][] unMaskable = createUnMaskables(qr);
        addData(message, qr);
        qr = applyMasks(qr, unMaskable, versionStorage);
        return qr;
    }

    /**
     * Method which adds the version pixels to the QR code
     *
     * @param qr The current 2D {@link Pix} array
     * @since 1.0.0
     */
    private void addVersion(Pix[][] qr) {
        String version = getVersionInfo();
        int dig = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 6; j++) {
                qr[qr.length - 9 - i][j] = new Pix(version.charAt(dig++) == '0');
            }
        }
        dig = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 6; j++) {
                qr[j][qr.length - 9 - i] = new Pix(version.charAt(dig++) == '0');
            }
        }
    }

    /**
     * Method which adds the format pixels to the QR code
     *
     * @param qr The current 2D {@link Pix} array
     * @param verStor An int array that contains the error correction value and the mask version used
     * @since 1.0.0
     */
    private void addFormat(Pix[][] qr, int[] verStor) {
        String formatInfo = getFormatInfo(verStor);
        //System.out.println("String used: " + formatInfo);
        for (int pos = 0; pos < 6; pos++) {//Top left bottom format
            qr[8][pos] = new Pix(formatInfo.charAt(pos) == '1');
        }
        qr[8][7] = new Pix(formatInfo.charAt(6) == '1');
        qr[8][8] = new Pix(formatInfo.charAt(7) == '1');
        for (int pos = 0; pos < 7; pos++) {//Bottom left format
            qr[qr.length - 1 - pos][8] = new Pix(formatInfo.charAt(pos) == '1');
        }
        for (int pos = 0; pos < 8; pos++) {//Top right format
            qr[8][qr.length - 8 + pos] = new Pix(formatInfo.charAt(pos + 7) == '1');
        }
        for (int pos = 0; pos < 6; pos++) {//Top left right format
            qr[5 - pos][8] = new Pix(formatInfo.charAt(pos + 9) == '1');
        }
        qr[7][8] = new Pix(formatInfo.charAt(8) == '1');
    }

    /**
     * Method which applies the masks and gets the least likely to be misinterpreted QR code
     *
     * @param qr The current 2D {@link Pix} array
     * @param unmaskable A 2D boolean array that has which positions can't be masked
     * @param verCont An int array that contains the error correction value and is used to store/pass out the mask used
     * @return The 2D {@link Pix} array of the masked QR code
     * @since 1.0.0
     */
    private Pix[][] applyMasks(Pix[][] qr, boolean[][] unmaskable, int[] verCont) {
        Pix[][][] maskedQRs = new Pix[8][qr.length][qr[0].length];
        for (int pos = 0; pos < maskedQRs.length; pos++) {
            maskedQRs[pos] = copyQR(qr);
            addFormat(maskedQRs[pos], new int[]{verCont[0], pos});
            if (version >= 7) {
                addVersion(qr);
            }
        }
        for (int x = 0; x < qr.length; x++) {
            for (int y = 0; y < qr[x].length; y++) {
                if (!unmaskable[y][x]) {
                    for (int pos = 0; pos < maskedQRs.length; pos++) {
                        applyMask(pos, y, x, maskedQRs[pos][y][x]);
                    }
                }
            }
        }
        int debug = 0;
        verCont[1] = debug;
        scoreQR(maskedQRs[debug]);
        return maskedQRs[debug];

        /*int[] score = new int[8];
        int min = 0;
        double minScore = Double.POSITIVE_INFINITY;
        for (int pos = 0; pos < maskedQRs.length; pos++) {
            score[pos] = scoreQR(maskedQRs[pos]);
            if (score[pos] < minScore) {
                minScore = score[pos];
                min = pos;
            }
        }
        verCont[1] = min;
        return maskedQRs[min];*/
    }//Hello world -> 6

    /**
     * Method which makes a copy of the inputted QR code that can be edited without changing the inputted one
     *
     * @param qr The current 2D {@link Pix} array
     * @return The 2D {@link Pix} array copy of the input
     * @since 1.0.0
     */
    private Pix[][] copyQR(Pix[][] qr){
        Pix[][] newQR = new Pix[qr.length][qr[0].length];
        for (int y = 0; y < qr.length; y++){
            for (int x = 0; x < qr[y].length; x++){
                if(qr[y][x] == null){
                    //System.out.println("Null at (" + y + ", " + x + ")");
                    newQR[y][x] = new Pix(false);
                }else newQR[y][x] = new Pix(qr[y][x].getState());
            }
        }
        return newQR;
    }

    /**
     * Creates the score of the inputted QR code, the score corresponds to the likelyhood of the QR code being misunderstood
     *
     * @param qr The current 2D {@link Pix} array
     * @return the score generated
     * @since 1.0.0
     */
    private int scoreQR(Pix[][] qr) {
        int[] score = new int[4];
        score[0] = checkRows(qr) + checkColumns(qr);
        score[1] = checkGroups(qr);
        score[2] = checkPattern(qr);
        score[3] = checkRatio(qr);
        //System.out.println("0: " + score[0]);
        //System.out.println("1: " + score[1]);
        //System.out.println("2: " + score[2]);
        //System.out.println("3: " + score[3]);
        //System.out.println("Total: " + (score[0] + score[1] + score[2] + score[3]));
        return (score[0] + score[1] + score[2] + score[3]);
    }

    /**
     * Creates the score using ratios of black to white positions
     *
     * @param qr The current 2D {@link Pix} array
     * @return the score generated
     * @since 1.0.0
     */
    private int checkRatio(Pix[][] qr) {
        int w = 0, b = 0;
        for (int y = 0; y < qr.length - 1; y++) {
            for (int x = 0; x < qr[y].length - 1; x++) {
                if (qr[y][x].getState()) b++;
                else w++;
            }
        }
        double pDark = (b / (double) (qr.length * qr[0].length)) * 100;
        int min = (int) Math.floor(pDark);
        while (min % 5 != 0) {
            min--;
        }
        int max = (int) Math.ceil(pDark);
        while (max % 5 != 0) {
            max++;
        }
        min -= 50;
        max -= 50;
        min = Math.abs(min);
        max = Math.abs(max);
        min /= 5;
        max /= 5;
        return (Math.min(min, max) * 10);
    }

    /**
     * Creates the score for the two patterns that are similar to the corner position codes
     *
     * @param qr The current 2D {@link Pix} array
     * @return the score generated
     * @since 1.0.0
     */
    private int checkPattern(Pix[][] qr) {
        int score = 0;
        for (int y = 0; y < qr.length - 1; y++) {
            for (int x = 0; x < qr[y].length - 1; x++) {
                if (matchPattern(x, y, qr, true)) score += 40;
                if (matchPattern(x, y, qr, false)) score += 40;
            }
        }
        return score;
    }

    /**
     * Finds if the specified area contains the pattern similar to the positional patterns
     *
     * @param qr The current 2D {@link Pix} array
     * @return true if teh area contains the pattern, false otherwise
     * @since 1.0.0
     */
    private boolean matchPattern(int col, int row, Pix[][] qr, boolean orientation) {
        int pos = 0;
        if ((orientation ? col : row) + 10 >= qr.length) return false;
        if (black(qr[row + (orientation ? 0 : pos++)][col + (orientation ? pos++ : 0)])) {//Scans for the two patterns that increase chances of mis-scanning
            if (black(qr[row + (orientation ? 0 : pos++)][col + (orientation ? pos++ : 0)])) return false;
            if (!black(qr[row + (orientation ? 0 : pos++)][col + (orientation ? pos++ : 0)])) return false;
            if (!black(qr[row + (orientation ? 0 : pos++)][col + (orientation ? pos++ : 0)])) return false;
            if (!black(qr[row + (orientation ? 0 : pos++)][col + (orientation ? pos++ : 0)])) return false;
            if (black(qr[row + (orientation ? 0 : pos++)][col + (orientation ? pos++ : 0)])) return false;
            if (!black(qr[row + (orientation ? 0 : pos++)][col + (orientation ? pos++ : 0)])) return false;
            if (black(qr[row + (orientation ? 0 : pos++)][col + (orientation ? pos++ : 0)])) return false;
            if (black(qr[row + (orientation ? 0 : pos++)][col + (orientation ? pos++ : 0)])) return false;
            if (black(qr[row + (orientation ? 0 : pos++)][col + (orientation ? pos++ : 0)])) return false;
            if (black(qr[row + (orientation ? 0 : pos)][col + (orientation ? pos : 0)])) return false;
        } else {
            if (black(qr[row + (orientation ? 0 : pos++)][col + (orientation ? pos++ : 0)])) return false;
            if (black(qr[row + (orientation ? 0 : pos++)][col + (orientation ? pos++ : 0)])) return false;
            if (black(qr[row + (orientation ? 0 : pos++)][col + (orientation ? pos++ : 0)])) return false;
            if (!black(qr[row + (orientation ? 0 : pos++)][col + (orientation ? pos++ : 0)])) return false;
            if (black(qr[row + (orientation ? 0 : pos++)][col + (orientation ? pos++ : 0)])) return false;
            if (!black(qr[row + (orientation ? 0 : pos++)][col + (orientation ? pos++ : 0)])) return false;
            if (!black(qr[row + (orientation ? 0 : pos++)][col + (orientation ? pos++ : 0)])) return false;
            if (!black(qr[row + (orientation ? 0 : pos++)][col + (orientation ? pos++ : 0)])) return false;
            if (black(qr[row + (orientation ? 0 : pos++)][col + (orientation ? pos++ : 0)])) return false;
            if (!black(qr[row + (orientation ? 0 : pos)][col + (orientation ? pos : 0)])) return false;
        }
        return true;
    }

    /**
     * Checks if the specified Pix is black or white
     *
     * @param pix the position being checked
     * @return true if the position is black, false otherwise
     * @since 1.0.0
     */
    private boolean black(Pix pix) {
        return pix.getState();
    }

    /**
     * Creates the score for areas with 2 by 2 blocks of the same colour
     *
     * @param qr The current 2D {@link Pix} array
     * @return the score generated
     * @since 1.0.0
     */
    private int checkGroups(Pix[][] qr) {
        int score = 0;
        for (int y = 0; y < qr.length - 1; y++) {
            for (int x = 0; x < qr[y].length - 1; x++) {
                if (matchBlob(x, y, qr)) score += 3;
            }
        }
        return score;
    }

    /**
     * Gets whether the specified area contains the 2 by 2 pattern of the same coloured pixels
     *
     * @param qr The current 2D {@link Pix} array
     * @param x the x position of the top left pixel
     * @param y the y position of the top left pixel
     * @return whether the area contains a 2by 2 pattern of the same colour
     * @since 1.0.0
     */
    private boolean matchBlob(int x, int y, Pix[][] qr) {
        return (
                (
                        qr[y][x].getState() &&
                                qr[y + 1][x].getState() &&
                                qr[y][x + 1].getState() &&
                                qr[y + 1][x + 1].getState()
                )
                        || (
                        !qr[y][x].getState() &&
                                !qr[y + 1][x].getState() &&
                                !qr[y][x + 1].getState() &&
                                !qr[y + 1][x + 1].getState()
                )
        );
    }

    /**
     * Creates the score based on the number of same pixels in a row in each column
     *
     * @param qr The current 2D {@link Pix} array
     * @return the score generated
     * @since 1.0.0
     */
    private int checkColumns(Pix[][] qr) {
        int score = 0;
        for (int x = 0; x < qr[0].length; x++) {
            boolean last = qr[0][x].getState();
            int total = 1;
            for (int y = 1; y < qr.length; y++) {
                if (qr[y][x].getState() == last) {
                    total++;
                } else {
                    if (total > 4) {
                        score += total - 2;
                    }
                    last = qr[y][x].getState();
                    total = 1;
                }
            }
            if (total > 4) {
                score += total - 2;
            }
        }
        return score;
    }

    /**
     * Creates the score based on the number of same pixels in a row in each row
     *
     * @param qr The current 2D {@link Pix} array
     * @return the score generated
     * @since 1.0.0
     */
    private int checkRows(Pix[][] qr) {
        int score = 0;
        for (int y = 0; y < qr.length; y++) {
            //System.out.println("Total at row(" + (y-1) + "): " + score);
            boolean last = qr[y][0].getState();
            int total = 1;
            for (int x = 1; x < qr[y].length; x++) {
                if (qr[y][x].getState() == last) {
                    total++;
                } else {
                    if (total > 4) {
                        score += total - 2;
                    }
                    last = qr[y][x].getState();
                    total = 1;
                }
            }
            if (total > 4) {
                score += total - 2;
            }
        }
        return score;
    }

    /**
     * Applies the specified mask to the inputted pixel based on its position
     *
     * @param pix the position
     * @param mask The mask being used on the position
     * @param row The row that the position is on.
     * @param column The column that the position is on.
     * @since 1.0.0
     */
    private void applyMask(int mask, int row, int column, Pix pix) {
        switch (mask) {
            case (0):
                if ((row + column) % 2 == 0) pix.mask();
                break;
            case (1):
                if ((row) % 2 == 0) pix.mask();
                break;
            case (2):
                if ((column) % 3 == 0) pix.mask();
                break;
            case (3):
                if ((row + column) % 3 == 0) pix.mask();
                break;
            case (4):
                if ((Math.floor(row / 2.0) + Math.floor(column / 3.0)) % 2 == 0) pix.mask();
                break;
            case (5):
                if (((row * column) % 2) + ((row * column) % 3) == 0) pix.mask();
                break;
            case (6):
                if ((((row * column) % 2) + ((row * column) % 3)) % 2 == 0) pix.mask();
                break;
            case (7):
                if ((((row + column) % 2) + ((row * column) % 3)) % 2 == 0) pix.mask();
                break;
        }

    }

    /**
     * Adds the data to the inputted 2D{@link Pix} array
     *
     * @param qr The current 2D {@link Pix} array
     * @param data The binary data to be placed onto the QR code
     * @since 1.0.0
     */
    private void addData(String data, Pix[][] qr) {
        int dig = 0;
        //0 = up, 1 = left
        boolean[] shift = new boolean[]{true, false};
        int[] pos = new int[]{qr.length - 1, qr.length - 1};
        while (dig < data.length()) {
            if (qr[pos[1]][pos[0]] == null) qr[pos[1]][pos[0]] = new Pix(data.charAt(dig++) == '1');
            else return;
            if (dig < data.length()) {
                getNextPos(pos, qr, shift);
            }
        }
    }

    /**
     * Gets the next position to write the data to, changes the value in the inputted int array
     *
     * @param qr The current 2D {@link Pix} array
     * @param pos The Int position to be edited
     * @param shift The array that holds the two boolean values that define the place to look for the next piece of data to be placed
     * @since 1.0.0
     */
    private void getNextPos(int[] pos, Pix[][] qr, boolean[] shift) {
        //0 = up, 1 = left
        while (qr[pos[1]][pos[0]] != null) {
            if (shift[0]) {
                if (shift[1]) {
                    pos[0] += 1;//Shift up and to the right
                    pos[1] -= 1;
                    shift[1] = false;
                } else {
                    pos[0] -= 1;//Shift left
                    shift[1] = true;
                }
                if (pos[1] < 0) {
                    shift[0] = false;//Flip the direction
                    pos[0] -= 2;//shift to be ready to go down
                    pos[1] += 1;
                    if (pos[0] == 6) {
                        pos[0] -= 1;
                    }
                }
            } else {
                if (shift[1]) {
                    shift[1] = false;
                    pos[0] += 1;//Shift down and right
                    pos[1] += 1;
                } else {
                    shift[1] = true;
                    pos[0] -= 1;//Shift left
                }
                if (pos[1] > qr.length - 1) {
                    shift[0] = true;//Flip the direction
                    pos[0] -= 2;//shift to be ready to go down
                    pos[1] -= 1;
                    if (pos[0] == 6) {
                        pos[0] -= 1;
                    }
                }
            }
        }
    }

    /**
     * Creates a 2D boolean array thatd efines which positions can and can't be masked
     *
     * @param qr The current 2D {@link Pix} array
     * @return The 2D array that defines which positions can and can't be masked.
     * @since 1.0.0
     */
    private boolean[][] createUnMaskables(Pix[][] qr) {
        boolean[][] unMaskables = new boolean[qr.length][qr[0].length];
        for (int x = 0; x < qr.length; x++) {
            for (int y = 0; y < qr[x].length; y++) {
                if (qr[y][x] != null) unMaskables[y][x] = true;
            }
        }
        return unMaskables;
    }

    /**
     * Adds temporary positions to the 2D{@link Pix} array to reserve them to be written to later
     *
     * @param qr The current 2D {@link Pix} array
     * @since 1.0.0
     */
    private void reserveFormatArea(Pix[][] qr) {
        for (int pos = 0; pos < 9; pos++) {
            if (qr[8][pos] == null) qr[8][pos] = new Pix(false);
        }
        for (int pos = 0; pos < 8; pos++) {
            if (qr[pos][8] == null) qr[pos][8] = new Pix(false);
        }
        for (int pos = 1; pos < 8; pos++) {
            if (qr[qr.length - pos][8] == null) qr[qr.length - pos][8] = new Pix(false);
        }
        for (int pos = 1; pos < 9; pos++) {
            if (qr[8][qr.length - pos] == null) qr[8][qr.length - pos] = new Pix(false);
        }
        if (version >= 7) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 6; j++) {
                    qr[qr.length - 9 - i][j] = new Pix(false);
                }
            }
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 6; j++) {
                    qr[j][qr.length - 9 - i] = new Pix(false);
                }
            }
        }
    }

    /**
     * Adds the dark module pixel to the 2D{@link Pix} array
     *
     * @param qr The current 2D {@link Pix} array
     * @since 1.0.0
     */
    private void addDarkModule(Pix[][] qr) {
        qr[qr.length - 8][8] = new Pix(true);
    }

    /**
     * Adds the timing belts to the 2D{@link Pix} array
     *
     * @param qr The current 2D {@link Pix} array
     * @since 1.0.0
     */
    private void addTimingBelts(Pix[][] qr) {
        for (int pos = 7; pos < qr[6].length - 7; pos++) {
            qr[6][pos] = new Pix(pos % 2 == 0);
        }
        for (int pos = 7; pos < qr.length - 7; pos++) {
            qr[pos][6] = new Pix(pos % 2 == 0);
        }
    }

    /**
     * Adds the positional patterns to the 2D{@link Pix} array
     *
     * @param qr The current 2D {@link Pix} array
     * @since 1.0.0
     */
    private void makeFinders(Pix[][] qr) {
        addFinder(0, 0, qr);
        addFinder(qr[0].length - 7, 0, qr);
        addFinder(0, qr.length - 7, qr);
        addSpacing(0, 0, qr);
        addSpacing(qr[0].length - 8, 0, qr);
        addSpacing(0, qr.length - 8, qr);
    }

    /**
     * Adds the spacing areas to the 2D{@link Pix} array around the positional patterns
     *
     * @param qr The current 2D {@link Pix} array
     * @since 1.0.0
     */
    private void addSpacing(int x, int y, Pix[][] qr) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (qr[y + i][x + j] == null) qr[y + i][x + j] = new Pix(false);
            }
        }
    }

    /**
     * Adds the positional pattern to the 2D{@link Pix} array
     *
     * @param qr The current 2D {@link Pix} array
     * @param y the row of the top left position of the positional pattern
     * @param x the cloumn of the top left position of the positional pattern
     * @since 1.0.0
     */
    private void addFinder(int x, int y, Pix[][] qr) {
        for (int pos = 0; pos < 7; pos++) {
            qr[y][x + pos] = new Pix(true);
        }
        for (int pos = 0; pos < 7; pos++) {
            qr[y + 6][x + pos] = new Pix(true);
        }
        for (int pos = 1; pos < 6; pos++) {
            qr[y + pos][x] = new Pix(true);
        }
        for (int pos = 1; pos < 6; pos++) {
            qr[y + pos][x + 6] = new Pix(true);
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                qr[y + 2 + i][x + 2 + j] = new Pix(true);
            }
        }
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (qr[y + i][x + j] == null) qr[y + i][x + j] = new Pix(false);
            }
        }
    }

    /**
     * Adds the alignment patterns to the 2D{@link Pix} array
     *
     * @param qr The current 2D {@link Pix} array
     * @since 1.0.0
     */
    private void addAlignments(Pix[][] qr) {
        int[] positions = getAlignmentCentres();
        for (int position : positions) {
            for (int position1 : positions) {
                addAlignmentPattern(position, position1, qr);
            }
        }
    }

    /**
     * Adds the alignment pattern to the 2D{@link Pix} array at the specified position
     *
     * @param qr The current 2D {@link Pix} array
     * @param y the row of the top left position of the positional pattern
     * @param x the cloumn of the top left position of the positional pattern
     * @since 1.0.0
     */
    private void addAlignmentPattern(int x, int y, Pix[][] qr) {
        if ((x == 6 && y == 6) || (x <= 6 && y >= qr.length - 7) || (x >= qr.length - 7 && y <= 6)) {
            return;
        }//Hello worldHello world2341
        x -= 2;
        y -= 2;
        for (int pos = 0; pos < 5; pos++) {
            qr[y][x + pos] = new Pix(true);
        }
        for (int pos = 0; pos < 5; pos++) {
            qr[y + 4][x + pos] = new Pix(true);
        }
        for (int pos = 1; pos < 4; pos++) {
            qr[y + pos][x] = new Pix(true);
        }
        for (int pos = 1; pos < 4; pos++) {
            qr[y + pos][x + 4] = new Pix(true);
        }
        qr[y + 2][x + 2] = new Pix(true);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (qr[y + i][x + j] == null) qr[y + i][x + j] = new Pix(false);
            }
        }
    }

    /**
     * gets the width and height of the QR code based on the version used.
     *
     * @return the size of the QR code
     * @since 1.0.0
     */
    private int getQRSize() {
        return (((version - 1) * 4) + 21);
    }

    /**
     * Encodes the data to a binary string
     *
     * @param dataIn The alphanumerical string to encode
     * @param errLvl The error correction level being used
     * @param mode the mode that the QR code is using
     * @return The binary string of the encoded data
     * @since 1.0.0
     */
    private String encode(String dataIn, int mode, int errLvl) {
        //System.out.println("In: " + dataIn);
        version = getVersion(dataIn.length(), mode, errLvl);
        if (version == 0) return null;
        String lengthBin = getLengthBin(dataIn, mode);
        if (lengthBin == null) return null;
        lengthBin = getModeBin(mode) + lengthBin;
        String data = alphanumericEncoding(dataIn.toUpperCase());
        if (data == null) return null;
        int[] ecLine = getECLine(errLvl);
        int reqBits = ecLine[0] * 8;
        data = padData(new StringBuilder(lengthBin + data), reqBits);
        int[] vals = splitData(data);
        int[][][] messageBlock = fillMessageBlock(generateBlock(ecLine), vals);
        int[][][] errorCodeBlock = fillErrorBlock(generateBlock(ecLine), messageBlock, ecLine[1]);

        List<Integer> messageInterleave = interleave(messageBlock);
        messageInterleave.addAll(interleave(errorCodeBlock));
        return convertToBinary(messageInterleave) + remainderBits(version);
    }

    /**
     * COnverts the inputted Integer list into a binary string
     *
     * @param message The list of integer values to be translated
     * @return The binary String
     * @since 1.0.0
     */
    private String convertToBinary(List<Integer> message) {
        StringBuilder binaryMessage = new StringBuilder();
        for (int num : message) {
            binaryMessage.append(padToFromLeft(Integer.toBinaryString(num), 8));
        }
        return binaryMessage.toString();
    }

    /**
     * Interleaves the data in the block into a list
     *
     * @param block the data block to be interleaved
     * @return The interleaved list of values from the inputted block
     * @since 1.0.0
     */
    private List<Integer> interleave(int[][][] block) {
        List<Integer> vals = new ArrayList<>();
        int loopTo = block[0][0].length;
        if (block.length > 1) {
            loopTo = Math.max(block[0][0].length, block[1][0].length);
        }
        for (int pos = 0; pos < loopTo; pos++) {
            for (int[][] ints : block) {
                for (int[] anInt : ints) {
                    if (anInt.length > pos) {
                        vals.add(anInt[pos]);
                    }
                }
            }
        }
        return vals;
    }

    /**
     * Fills the error block based off of the message
     *
     * @param block the data block to be filled with the error data
     * @param message The block with the message in
     * @param numEcs The number of error correction words for the version being used
     * @return The filled error block
     * @since 1.0.0
     */
    private int[][][] fillErrorBlock(int[][][] block, int[][][] message, int numEcs) {
        for (int x = 0; x < block.length; x++) {
            for (int y = 0; y < block[x].length; y++) {
                block[x][y] = getErrorCorrectionCodewords(message[x][y], numEcs);
            }
        }
        return block;
    }

    /**
     * Fills the message block
     *
     * @param block the data block to be filled with the error data
     * @param message The block with the message in
     * @return The filled message block
     * @since 1.0.0
     */
    private int[][][] fillMessageBlock(int[][][] block, int[] message) {
        int pos = 0;
        for (int x = 0; x < block.length; x++) {
            for (int y = 0; y < block[x].length; y++) {
                for (int z = 0; z < block[x][y].length; z++) {
                    block[x][y][z] = message[pos++];
                }
            }
        }
        return block;
    }

    /**
     * Creates an empty block based off of the error correction values
     *
     * @param ecLine The defenitions of the block based off of the error correction values being used
     * @return The empty block of the correct size
     * @since 1.0.0
     */
    private int[][][] generateBlock(int[] ecLine) {
        int[][][] block = new int[(ecLine[4] != 0 ? 2 : 1)][0][0];
        for (int pos = 1; pos < block.length + 1; pos++) {
            block[pos - 1] = new int[ecLine[(pos * 2)]][ecLine[(pos * 2) + 1]];
        }
        return block;
    }

    /**
     * Creates the error correction values for the inputted values
     *
     * @param vals The values in the section of the block for which teh error correction words are being generated
     * @param codewords The number of words required in the output
     * @return The int array with the error correction numbers in
     * @since 1.0.0
     */
    private int[] getErrorCorrectionCodewords(int[] vals, int codewords) {
        int[] poly = getPoly(codewords);
        int[] startExp = new int[]{vals.length + codewords};
        int[] alpInt = getAlpIntTable("QRInputs/AlphaToInt.txt", true), intAlp = getAlpIntTable("QRInputs/IntToAlpha.txt", false);
        int alpMult = intAlp[vals[0]];
        int[] prev = Arrays.copyOf(poly, poly.length);
        for (int pos = 0; pos < poly.length; pos++) {
            prev[pos] = alpInt[((alpMult + poly[pos]) % 255)];
        }
        prev = xor(prev, vals, startExp);
        int[] prevXor = Arrays.copyOf(prev, prev.length);
        for (int pos = 0; pos < vals.length - 1; pos++) {
            prev = alphaAdd(prev[0], Arrays.copyOf(poly, poly.length), alpInt, intAlp);
            prev = xor(prev, prevXor, startExp);
            prevXor = Arrays.copyOf(prev, prev.length);
        }
        return prev;
    }

    /**
     * Adds the polynomial together using alpha values
     *
     * @param mult The first value of the previous equation
     * @param poly The defenitions for the multiplication polynomial
     * @param alpInt The map for the alpha to integer values
     * @param intAlp The map for the integer to alpha values
     * @return The integer defenition of the new polynomial line
     * @since 1.0.0
     */
    private int[] alphaAdd(int mult, int[] poly, int[] alpInt, int[] intAlp) {
        int alpMult = intAlp[mult];
        for (int pos = 0; pos < poly.length; pos++) {
            poly[pos] = alpInt[(poly[pos] + alpMult) % 255];
        }
        return poly;
    }

    /**
     * Xors the two inputted polynoials
     *
     * @param first The first polynomial
     * @param second The second polynomial
     * @return the integer defenition of the polynomial line
     * @since 1.0.0
     */
    private int[] xor(int[] first, int[] second, int[] startExp) {
        if (second.length < first.length) {
            second = Arrays.copyOf(second, first.length);
        }
        for (int pos = 0; pos < first.length; pos++) {
            first[pos] = first[pos] ^ second[pos];
        }
        int remove = 0;
        while (first[remove] == 0) {
            remove++;
        }
        startExp[0] -= remove;
        int diff = 0;
        if (second.length > first.length) {
            diff = second.length - first.length;
        }
        int[] newEq = new int[first.length - remove + diff];
        for (int pos = 0; pos < newEq.length; pos++) {
            newEq[pos] = ((pos < (first.length - remove)) ? first[pos + remove] : second[pos + remove]);
        }
        return newEq;
    }

    /**
     * Translates the inputted string into 8 bit integer arrays
     *
     * @param data The binary input string
     * @return The integer 8 bit equivalent of the inputted string
     * @since 1.0.0
     */
    private int[] splitData(String data) {
        int[] vals = new int[(data.length() / 8)];
        for (int pos = 0; pos < vals.length; pos++) {
            vals[pos] = Integer.parseInt(data.substring(pos * 8, (pos + 1) * 8), 2);
        }
        return vals;
    }

    /**
     * Pads the data to the required length
     *
     * @param data The binary input string
     * @param reqBits The required number of bits to pad the binary string to
     * @return The padded data
     * @since 1.0.0
     */
    private String padData(StringBuilder data, int reqBits) {
        if (data.length() < reqBits - 4) {//Probably where the error is
            data.append("0000");
        } else {
            while (data.length() < reqBits) {
                data.append("0");
            }
        }
        while (data.length() % 8 != 0) {
            data.append("0");
        }
        if (data.length() < reqBits) {
            String[] padData = new String[]{"11101100", "00010001"};
            int flip = 0;
            while (data.length() < reqBits) {
                data.append(padData[(flip++) % 2]);
            }
        }
        return data.toString();
    }

    /**
     * Gets the numeric equivalent of the inputter character
     *
     * @param let The character being encoded
     * @return The numerical equivalent of the int
     * @since 1.0.0
     */
    private int getVal(char let) {
        int val = 0;
        if (let >= 48 && let <= 57) {
            val = let - 48;
        } else if (let >= 65 && let <= 90) {
            val = let - 55;
        } else {
            if (let == 32) {
                val = 36;
            } else if (let == '$') {
                val = 37;
            } else if (let == '%') {
                val = 38;
            } else if (let == '*') {
                val = 39;
            } else if (let == '+') {
                val = 40;
            } else if (let == '-') {
                val = 41;
            } else if (let == '.') {
                val = 42;
            } else if (let == '/') {
                val = 43;
            } else if (let == ':') {
                val = 44;
            }
        }
        return val;
    }

    /**
     * Encodes the inputted string into a binary string equivalent
     *
     * @param input The alphanumeric data to encode
     * @return The binary string equivalent of the data to encode
     * @since 1.0.0
     */
    private String alphanumericEncoding(String input) {
        int length = input.length();
        int size = (length % 2 == 0) ? (length / 2) : ((length / 2) + 1);
        String[] parts = new String[size];
        for (int pos = 0; pos < size; pos++) {
            parts[pos] = input.substring(pos * 2, Math.min((pos + 1) * 2, length));
        }
        for (int pos = 0; pos < parts.length; pos++) {
            if (parts[pos].length() == 2) {
                int val = (getVal(parts[pos].charAt(0)) * 45) + getVal(parts[pos].charAt(1));
                parts[pos] = padToFromLeft(Integer.toBinaryString(val), 11);
            } else {
                parts[pos] = padToFromLeft(Integer.toBinaryString(getVal(parts[pos].charAt(0))), 6);
            }
        }
        StringBuilder result = new StringBuilder();
        for (String str : parts) {
            result.append(str);
        }
        return result.toString();
    }

    /**
     * Returns the binary code that corresponds to the
     *
     * @param data the data to be encoded
     * @return The encodeing mode
     * @since 1.0.0
     */
    private String getLengthBin(String data, int mode) {
        String lengthBin = Long.toBinaryString(data.length());
        int lengthRequired;
        if (version < 10) {
            switch (mode) {
                case (1):
                    lengthRequired = 10;
                    break;
                case (2):
                    lengthRequired = 9;
                    break;
                case (3):
                    lengthRequired = 8;
                    break;
                case (4):
                    lengthRequired = 8;
                    break;
                default:
                    return null;
            }
        } else if (version < 27) {
            switch (mode) {
                case (1):
                    lengthRequired = 12;
                    break;
                case (2):
                    lengthRequired = 11;
                    break;
                case (3):
                    lengthRequired = 16;
                    break;
                case (4):
                    lengthRequired = 10;
                    break;
                default:
                    return null;
            }
        } else {
            switch (mode) {
                case (1):
                    lengthRequired = 14;
                    break;
                case (2):
                    lengthRequired = 13;
                    break;
                case (3):
                    lengthRequired = 16;
                    break;
                case (4):
                    lengthRequired = 12;
                    break;
                default:
                    return null;
            }
        }
        return padToFromLeft(lengthBin, lengthRequired);
    }

    /**
     * Pads inputted binary string with zero's to the required length from the left
     *
     * @param string the data to encode
     * @param length The length to pad to
     * @return The padded binary string
     * @since 1.0.0
     */
    private String padToFromLeft(String string, int length) {
        StringBuilder stringBuilder = new StringBuilder(string);
        while (stringBuilder.length() < length) {
            stringBuilder.insert(0, "0");
        }
        string = stringBuilder.toString();
        return string;
    }

    /**
     * Gets the binary code for the mode being used
     *
     * @param mode The mode being used
     * @return The binary code equivalent of the mode
     * @since 1.0.0
     */
    private String getModeBin(int mode) {
        switch (mode) {
            case (1):
                return "0001";
            case (2):
                return "0010";
            case (3):
                return "0100";
            case (4):
                return "1000";
            default:
                return null;
        }
    }

    /**
     * Gets the QR code version to be used based off of the defenitions in the CharLevels file
     *
     * @param mode The mode being used
     * @param length Length of the data to encode
     * @param errLvl The error correction level being used
     * @return The version that needs to be used
     * @since 1.0.0
     */
    private int getVersion(int length, int mode, int errLvl) {
        try {
            Scanner scan = new Scanner(new File(cl.getResource("QRInputs/CharLevels.txt").getFile()));
            while (scan.hasNext()) {
                int version = scan.nextInt();
                scan.nextLine();
                String errNums = "";
                for (int pos = 1; pos < 5; pos++) {
                    if (pos == errLvl) {
                        errNums = scan.nextLine();
                    } else {
                        scan.nextLine();
                    }
                }
                errNums = formatLine(errNums);
                if (Integer.parseInt(errNums.split(",")[mode]) > length) {
                    return version;
                }
            }
        } catch (Exception e) {
            System.out.println("Unable to read version sizes, cannot create QRCode.");
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    /**
     * Formats the read line
     *
     * @param line The line to format
     * @return The formatted line
     * @since 1.0.0
     */
    private String formatLine(String line) {
        Pattern errLvlLet = Pattern.compile("[LMQH ]");
        Matcher matcher = errLvlLet.matcher(line);
        line = matcher.replaceAll("");
        return line.replaceAll("\t", ",");
    }

    /**
     * Gets the polynomial defenition from the polynomials file for the codeword length
     *
     * @param codewords The number of codewords required
     * @return The polygon defenition
     * @since 1.0.0
     */
    private int[] getPoly(int codewords) {
        int[] polyDef = new int[0];
        try (Scanner scan = new Scanner(new File(cl.getResource("QRInputs/PolynomialTable.txt").getFile()))) {
            for (int pos = 0; pos < codewords - 7; pos++) {
                scan.nextLine();
            }
            String line = scan.nextLine();
            line = line.split(":")[1];
            polyDef = convertToInt(line.split(","));
        } catch (Exception e) {
            System.out.println("Unable to read error correction size file, cannot create QRCode.");
        }
        return polyDef;
    }

    /**
     * Gets the error correction definition line and formats it into an int array
     *
     * @param errLvl The error correction level being used
     * @return The error correction defenitions
     * @since 1.0.0
     */
    private int[] getECLine(int errLvl) {
        int[] ecNums = new int[0];
        try {
            ecNums = convertToInt(getLine(errLvl).split(","));
        } catch (Exception e) {
            System.out.println("Unable to get the Error correction line.");
        }
        return ecNums;
    }

    /**
     * Converts the inputted string array into the int equivalents and converts to an int array
     *
     * @param line the line of String numbers to be converted
     * @return The int equivalents in an array
     * @since 1.0.0
     */
    private int[] convertToInt(String[] line) {
        int[] ints = new int[line.length];
        for (int pos = 0; pos < line.length; pos++) {
            ints[pos] = Integer.parseInt(line[pos]);
        }
        return ints;
    }

    /**
     * Gets the error correction line form the error correction defenitions text file
     *
     * @param errLvl The error correction level being used
     * @return The error correction defenitions
     * @since 1.0.0
     */
    private String getLine(int errLvl) throws IOException {
        Scanner scan = new Scanner(new File(cl.getResource("QRInputs/ErrorCorrection.txt").getFile()));
        while (Integer.parseInt(scan.nextLine()) < version) {
            for (int pos = 0; pos < 4; pos++) {
                scan.nextLine();
            }
        }
        for (int pos = 0; pos < errLvl - 1; pos++) {
            scan.nextLine();
        }
        return formatLine(scan.nextLine()).substring(1);
    }

    /**
     * Gets the alpha integer map
     *
     * @param file The file to access for the defenitions
     * @param alpToInt whether the file to be read is the alpha to integer map or otherwise
     * @return the map retrieved
     * @since 1.0.0
     */
    private int[] getAlpIntTable(String file, boolean alpToInt) {
        int[] alpInt = new int[256];
        if (alpToInt) alpInt[0] = 0;
        try (Scanner scan = new Scanner(new File(cl.getResource(file).getFile()))) {
            for (int pos = (alpToInt ? 0 : 1); pos < 256; pos++) {
                alpInt[pos] = scan.nextInt();
            }
        } catch (Exception e) {
            System.out.println("Unable to create map for: " + file);
        }
        return alpInt;
    }

    /**
     * Gets the positions of the positional patterns in the QR code
     *
     * @return The positional pattern positions
     * @since 1.0.0
     */
    private int[] getAlignmentCentres() {
        int[] centreLocations = new int[0];
        if (version != 1) {
            try (Scanner scan = new Scanner(new File(cl.getResource("QRInputs/AlignmentPatternLocations.txt").getFile()))) {
                for (int pos = 0; pos < version - 1; pos++) {
                    scan.nextLine();
                }
                String[] locatStr = scan.nextLine().split(",");
                centreLocations = convertToInt(locatStr);
            } catch (Exception e) {
                System.out.println("Unable to find alignment centres.");
            }
        }
        return centreLocations;
    }

    /**
     * Gets the format info from the format info file
     *
     * @param verStor The int with the version information
     * @return The format information
     * @since 1.0.0
     */
    private String getFormatInfo(int[] verStor) {
        String bitString = "";
        try (Scanner scan = new Scanner(new File(cl.getResource("QRInputs/FormatInfo.txt").getFile()))) {
            for (int pos = 1; pos < verStor[0]; pos++) {
                scan.nextLine();
                for (int matVer = 0; matVer < 8; matVer++) {
                    scan.nextLine();
                }
            }
            scan.nextLine();
            for (int matVer = 1; matVer < verStor[1] + 1; matVer++) {
                scan.nextLine();
            }
            bitString = scan.nextLine();
        } catch (Exception e) {
            System.out.println("Unable to retrieve format info.");
        }
        return bitString;
    }

    /**
     * Gets the version info from the format info file
     *
     * @return The version information
     * @since 1.0.0
     */
    private String getVersionInfo() {
        String bitString = "";
        try (Scanner scan = new Scanner(new File(cl.getResource("QRInputs/VersionInfo.txt").getFile()))) {
            for (int pos = 0; pos < version - 7; pos++) {
                scan.nextLine();

            }
            bitString = scan.nextLine();
        } catch (Exception e) {
            System.out.println("Unable to retrieve version info.");
        }
        return bitString;
    }

    /**
     * Gets the required number of remainder bits for the Qr code being used
     *
     * @param version The QR code version being used
     * @return The string with the bits to append to the data
     * @since 1.0.0
     */
    private String remainderBits(int version) {
        int rem = 0;
        try (Scanner scan = new Scanner(new File(cl.getResource("QRInputs/RemainderBits.txt").getFile()))) {
            for (int pos = 0; pos < version - 1; pos++) {
                scan.nextLine();
            }
            rem = scan.nextInt();
        } catch (Exception e) {
            System.out.println("Unable to retrieve remainder bytes file.");
        }
        StringBuilder remBits = new StringBuilder();
        for (int pos = 0; pos < rem; pos++) {
            remBits.append("0");
        }
        return remBits.toString();
    }
}
