package QRCode;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QRCode extends Canvas {

    //Modes: 1) Numeric 2) Alphanumeric 3) Byte 4) kanji(Japanese I think?)
    //Error level: 1) Low 2) Medium 3) Q(? Basically 1 up from med) 4) High

    //Remove Kanji compatibility? Depends on difficulty
    //Only add the required one to the carpark app though

    private int version;

    private int pixelSize = 2;
    private Color pixelColour = new Color(0, 0, 0, 1);
    private Color unusedPixel = new Color(1, 0, 0, 1);

    public static void main(String[] args) {
        QRCode code = new QRCode();
        code.generate("HELLO WORLD", 2, 2, 6);
        //new QRCode("HELL", 2, 4);

    }

    private void formatFile() {
        String file = "Inputs/AlphaToInt.txt";
        try (
                Scanner scan = new Scanner(new File(file))
        ) {
            List<String> lines = new ArrayList<>();
            while (scan.hasNext()) {
                lines.add(scan.nextLine());
            }
            for (int pos = lines.size(); pos > 0; pos--) {
                if (lines.get(pos - 1).contentEquals("")) {
                    lines.remove(pos - 1);
                }
            }
            String[] fileLines = lines.toArray(new String[0]);
            for (int pos = 0; pos < fileLines.length; pos++) {
                //fileLines[pos] = fileLines[pos].replace("α[0-9]* = ", "");
                fileLines[pos] = fileLines[pos].replaceAll("[0-9]* = α", "");
            }
            FileWriter writer = new FileWriter(new File(file));
            for (int pos = 0; pos < fileLines.length; pos++) {
                writer.write(fileLines[pos] + (pos != fileLines.length - 1 ? System.lineSeparator() : ""));
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Error formatting file.");
        }
    }

    private void drawQRCode(Pix[][] qr) {
        int shift = 4 * pixelSize;
        int size = (qr.length * pixelSize) + (shift * 2);
        this.setHeight(size);
        this.setWidth(size);
        GraphicsContext g = getGraphicsContext2D();
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        drawGrid(g, size);
        PixelWriter p = g.getPixelWriter();
        for (int y = 0; y < qr.length; y++) {
            for (int x = 0; x < qr[y].length; x++) {
                if (qr[y][x] == null) drawPixel(y * pixelSize, x * pixelSize, p, unusedPixel, shift);
                else if (qr[y][x].getState()) drawPixel(y * pixelSize, x * pixelSize, p, pixelColour, shift);
            }
        }
    }

    private void drawGrid(GraphicsContext g, int size) {
        double height = this.getHeight(), width = this.getWidth();
        for (int pos = 0; pos < size; pos++) {
            g.strokeLine(pos * pixelSize, 0, pos * pixelSize, height);
            g.strokeLine(0, pos * pixelSize, width, pos * pixelSize);
        }
    }

    private void drawPixel(int y, int x, PixelWriter p, Color pixelColour, int shift) {
        for (int i = 0; i < pixelSize; i++) {
            for (int j = 0; j < pixelSize; j++) {
                p.setColor(x + i + shift, y + j + shift, pixelColour);
            }
        }
    }

    public QRCode() {
    }

    public void generate(Object dataToEncode, int mode, int errorLevel, int pixelSize) {
        this.pixelSize = pixelSize;
        //Need to convert to support objects and casting
        if (mode > 4) mode = 4;
        if (mode < 1) mode = 1;
        if (errorLevel > 4) errorLevel = 4;
        if (errorLevel < 1) errorLevel = 1;
        String message = encode(dataToEncode, mode, errorLevel);
        Pix[][] QR = makeQR(message, errorLevel);
        drawQRCode(QR);
    }

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

    private void addFormat(Pix[][] qr, int[] verStor) {
        String formatInfo = getFormatInfo(verStor);
        System.out.println("String used: " + formatInfo);
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

    private Pix[][] copyQR(Pix[][] qr){
        Pix[][] newQR = new Pix[qr.length][qr[0].length];
        for (int y = 0; y < qr.length; y++){
            for (int x = 0; x < qr[y].length; x++){
                if(qr[y][x] == null){
                    System.out.println("Null at (" + y + ", " + x + ")");
                    newQR[y][x] = new Pix(false);
                }else newQR[y][x] = new Pix(qr[y][x].getState());
            }
        }
        return newQR;
    }

    private int scoreQR(Pix[][] qr) {
        int[] score = new int[4];
        score[0] = checkRows(qr) + checkColumns(qr);
        score[1] = checkGroups(qr);
        score[2] = checkPattern(qr);
        score[3] = checkRatio(qr);
        System.out.println("0: " + score[0]);
        System.out.println("1: " + score[1]);
        System.out.println("2: " + score[2]);
        System.out.println("3: " + score[3]);
        System.out.println("Total: " + (score[0] + score[1] + score[2] + score[3]));
        return (score[0] + score[1] + score[2] + score[3]);
    }

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

    private boolean black(Pix pix) {
        return pix.getState();
    }

    private int checkGroups(Pix[][] qr) {
        int score = 0;
        for (int y = 0; y < qr.length - 1; y++) {
            for (int x = 0; x < qr[y].length - 1; x++) {
                if (matchBlob(x, y, qr)) score += 3;
            }
        }
        return score;
    }

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

    private int checkRows(Pix[][] qr) {
        int score = 0;
        for (int y = 0; y < qr.length; y++) {
            System.out.println("Total at row(" + (y-1) + "): " + score);
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

    private boolean[][] createUnMaskables(Pix[][] qr) {
        boolean[][] unMaskables = new boolean[qr.length][qr[0].length];
        for (int x = 0; x < qr.length; x++) {
            for (int y = 0; y < qr[x].length; y++) {
                if (qr[y][x] != null) unMaskables[y][x] = true;
            }
        }
        return unMaskables;
    }

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

    private void addDarkModule(Pix[][] qr) {
        qr[qr.length - 8][8] = new Pix(true);
    }

    private void addTimingBelts(Pix[][] qr) {
        for (int pos = 7; pos < qr[6].length - 7; pos++) {
            qr[6][pos] = new Pix(pos % 2 == 0);
        }
        for (int pos = 7; pos < qr.length - 7; pos++) {
            qr[pos][6] = new Pix(pos % 2 == 0);
        }
    }

    private void makeFinders(Pix[][] qr) {
        addFinder(0, 0, qr);
        addFinder(qr[0].length - 7, 0, qr);
        addFinder(0, qr.length - 7, qr);
        addSpacing(0, 0, qr);
        addSpacing(qr[0].length - 8, 0, qr);
        addSpacing(0, qr.length - 8, qr);
    }

    private void addSpacing(int x, int y, Pix[][] qr) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (qr[y + i][x + j] == null) qr[y + i][x + j] = new Pix(false);
            }
        }
    }

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

    private void addAlignments(Pix[][] qr) {
        int[] positions = getAlignmentCentres();
        for (int position : positions) {
            for (int position1 : positions) {
                addAlignmentPattern(position, position1, qr);
            }
        }
    }

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

    private int getQRSize() {
        return (((version - 1) * 4) + 21);
    }

    private String encode(Object dataIn, int mode, int errLvl) {
        System.out.println("In: " + dataIn);
        version = getVersion((dataIn + "").length(), mode, errLvl);
        if (version == 0) return null;
        String lengthBin = getLengthBin(dataIn, mode);
        if (lengthBin == null) return null;
        lengthBin = getModeBin(mode) + lengthBin;
        String data = encodeData(dataIn, mode);
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

    private String convertToBinary(List<Integer> message) {
        StringBuilder binaryMessage = new StringBuilder();
        for (int num : message) {
            binaryMessage.append(padToFromLeft(Integer.toBinaryString(num), 8));
        }
        return binaryMessage.toString();
    }

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

    private int[][][] fillErrorBlock(int[][][] block, int[][][] message, int numEcs) {
        for (int x = 0; x < block.length; x++) {
            for (int y = 0; y < block[x].length; y++) {
                block[x][y] = getErrorCorrectionCodewords(message[x][y], numEcs);
            }
        }
        return block;
    }

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

    private int[][][] generateBlock(int[] ecLine) {
        int[][][] block = new int[(ecLine[4] != 0 ? 2 : 1)][0][0];
        for (int pos = 1; pos < block.length + 1; pos++) {
            block[pos - 1] = new int[ecLine[(pos * 2)]][ecLine[(pos * 2) + 1]];
        }
        return block;
    }

    private int[] getErrorCorrectionCodewords(int[] vals, int codewords) {
        int[] poly = getPoly(codewords);
        int[] startExp = new int[]{vals.length + codewords};
        int[] alpInt = getAlpIntTable("Inputs/AlphaToInt.txt", true), intAlp = getAlpIntTable("Inputs/IntToAlpha.txt", false);
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

    private int[] alphaAdd(int mult, int[] poly, int[] alpInt, int[] intAlp) {
        int alpMult = intAlp[mult];
        for (int pos = 0; pos < poly.length; pos++) {
            poly[pos] = alpInt[(poly[pos] + alpMult) % 255];
        }
        return poly;
    }

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

    private int[] splitData(String data) {
        int[] vals = new int[(data.length() / 8)];
        for (int pos = 0; pos < vals.length; pos++) {
            vals[pos] = Integer.parseInt(data.substring(pos * 8, (pos + 1) * 8), 2);
        }
        return vals;
    }

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

    private String encodeData(Object data, int mode) {
        String result = "";
        switch (mode) {
            case (1):
                result = numericEncode((long) data);
                break;
            case (2):
                result = alphanumericEncoding(((String) data).toUpperCase());
                break;
            case (3):
                result = null;
                break;
            case (4):
                result = null;
                break;
        }
        return result;
    }

    private String numericEncode(long num) {
        int length = (num + "").length();
        int size = ((length % 3 == 0) ? (length / 3) : ((length / 3) + 1));
        String[] nums = new String[size];
        String number = num + "";
        for (int pos = 0; pos < size; pos++) {
            nums[pos] = number.substring(pos * 3, Math.min((pos + 1) * 3, length));
        }
        for (int pos = 0; pos < nums.length; pos++) {
            nums[pos] = Integer.toBinaryString(Integer.parseInt(nums[pos]));
        }
        StringBuilder result = new StringBuilder();
        for (String str : nums) {
            result.append(str);
        }
        return result.toString();
    }

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

    private String getLengthBin(Object num, int mode) {
        String lengthBin = Long.toBinaryString((num + "").length());
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

    private String padToFromLeft(String string, int length) {
        StringBuilder stringBuilder = new StringBuilder(string);
        while (stringBuilder.length() < length) {
            stringBuilder.insert(0, "0");
        }
        string = stringBuilder.toString();
        return string;
    }

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

    private int getVersion(int length, int mode, int errLvl) {
        try {
            Scanner scan = new Scanner(new File("Inputs/CharLevels.txt"));
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
            return 0;
        }
        return 0;
    }

    private String formatLine(String line) {
        Pattern errLvlLet = Pattern.compile("[LMQH ]");
        Matcher matcher = errLvlLet.matcher(line);
        line = matcher.replaceAll("");
        return line.replaceAll("\t", ",");
    }

    private int[] getPoly(int codewords) {
        int[] polyDef = new int[0];
        try (Scanner scan = new Scanner(new File("Inputs/PolynomialTable.txt"))) {
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

    private int[] getECLine(int errLvl) {
        int[] ecNums = new int[0];
        try {
            ecNums = convertToInt(getLine(errLvl).split(","));
        } catch (Exception e) {
            System.out.println("Unable to get the Error correction line.");
        }
        return ecNums;
    }

    private int[] convertToInt(String[] line) {
        int[] ints = new int[line.length];
        for (int pos = 0; pos < line.length; pos++) {
            ints[pos] = Integer.parseInt(line[pos]);
        }
        return ints;
    }

    private String getLine(int errLvl) throws IOException {
        Scanner scan = new Scanner(new File("Inputs/ErrorCorrection.txt"));
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

    private int[] getAlpIntTable(String file, boolean alpToInt) {
        int[] alpInt = new int[256];
        if (alpToInt) alpInt[0] = 0;
        try (Scanner scan = new Scanner(new File(file))) {
            for (int pos = (alpToInt ? 0 : 1); pos < 256; pos++) {
                alpInt[pos] = scan.nextInt();
            }
        } catch (Exception e) {
            System.out.println("Unable to create map for: " + file);
        }
        return alpInt;
    }

    private int[] getAlignmentCentres() {
        int[] centreLocations = new int[0];
        if (version != 1) {
            try (Scanner scan = new Scanner(new File("Inputs/AlignmentPatternLocations.txt"))) {
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

    private String getFormatInfo(int[] verStor) {
        String bitString = "";
        try (Scanner scan = new Scanner(new File("Inputs/FormatInfo.txt"))) {
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

    private String getVersionInfo() {
        String bitString = "";
        try (Scanner scan = new Scanner(new File("Inputs/VersionInfo.txt"))) {
            for (int pos = 0; pos < version - 7; pos++) {
                scan.nextLine();

            }
            bitString = scan.nextLine();
        } catch (Exception e) {
            System.out.println("Unable to retrieve version info.");
        }
        return bitString;
    }

    private String remainderBits(int version) {
        int rem = 0;
        try (Scanner scan = new Scanner(new File("Inputs/RemainderBits.txt"))) {
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
