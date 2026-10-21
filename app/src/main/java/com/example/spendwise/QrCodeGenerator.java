package com.example.spendwise;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QrCodeGenerator {

    private static final int[] EXP_TABLE = new int[256];
    private static final int[] LOG_TABLE = new int[256];

    static {
        int val = 1;
        for (int i = 0; i < 255; i++) {
            EXP_TABLE[i] = val;
            LOG_TABLE[val] = i;
            val <<= 1;
            if ((val & 0x100) != 0) {
                val ^= 0x11D;
            }
        }
    }

    private static class VersionSpec {
        int version;
        int dataCapacity;
        int ecBytesPerBlock;
        int blocks;
        int[] alignCoords;

        VersionSpec(int version, int dataCapacity, int ecBytesPerBlock, int blocks, int[] alignCoords) {
            this.version = version;
            this.dataCapacity = dataCapacity;
            this.ecBytesPerBlock = ecBytesPerBlock;
            this.blocks = blocks;
            this.alignCoords = alignCoords;
        }
    }

    private static final VersionSpec[] VERSIONS = new VersionSpec[]{
            new VersionSpec(1, 19, 7, 1, new int[]{}),
            new VersionSpec(2, 34, 10, 1, new int[]{6, 18}),
            new VersionSpec(3, 55, 15, 1, new int[]{6, 22}),
            new VersionSpec(4, 80, 20, 1, new int[]{6, 26}),
            new VersionSpec(5, 108, 26, 1, new int[]{6, 30}),
            new VersionSpec(6, 136, 18, 2, new int[]{6, 34}),
            new VersionSpec(7, 156, 20, 2, new int[]{6, 22, 38}),
            new VersionSpec(8, 194, 24, 2, new int[]{6, 24, 42}),
            new VersionSpec(9, 232, 30, 2, new int[]{6, 26, 46}),
            new VersionSpec(10, 274, 18, 4, new int[]{6, 28, 50})
    };

    public static Bitmap encodeToBitmap(String content, int width, int height) {
        boolean[][] modules = generateQrMatrix(content);
        int matrixSize = modules.length;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int quietZone = 4;
        int totalModules = matrixSize + quietZone * 2;
        float moduleSize = Math.min((float) width / totalModules, (float) height / totalModules);

        int offsetX = (int) ((width - totalModules * moduleSize) / 2);
        int offsetY = (int) ((height - totalModules * moduleSize) / 2);

        int[] pixels = new int[width * height];
        Arrays.fill(pixels, Color.WHITE);

        for (int r = 0; r < matrixSize; r++) {
            for (int c = 0; c < matrixSize; c++) {
                if (modules[r][c]) {
                    int startX = (int) (offsetX + (c + quietZone) * moduleSize);
                    int endX = (int) (offsetX + (c + quietZone + 1) * moduleSize);
                    int startY = (int) (offsetY + (r + quietZone) * moduleSize);
                    int endY = (int) (offsetY + (r + quietZone + 1) * moduleSize);

                    for (int y = startY; y < endY && y < height; y++) {
                        for (int x = startX; x < endX && x < width; x++) {
                            pixels[y * width + x] = Color.BLACK;
                        }
                    }
                }
            }
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static boolean[][] generateQrMatrix(String content) {
        byte[] utf8Bytes = content.getBytes(StandardCharsets.UTF_8);

        VersionSpec spec = null;
        for (VersionSpec v : VERSIONS) {
            if (utf8Bytes.length <= v.dataCapacity - 3) {
                spec = v;
                break;
            }
        }
        if (spec == null) {
            spec = VERSIONS[VERSIONS.length - 1];
        }

        List<Boolean> bitStream = new ArrayList<>();
        // Mode indicator 0100 (Byte mode)
        addBits(bitStream, 0x4, 4);
        // Character count indicator (8 bits for V1-V9, 16 bits for V10+)
        addBits(bitStream, utf8Bytes.length, spec.version < 10 ? 8 : 16);
        // UTF-8 bytes
        for (byte b : utf8Bytes) {
            addBits(bitStream, b & 0xFF, 8);
        }

        // Terminator bits up to 4
        int totalDataBits = spec.dataCapacity * 8;
        int termLen = Math.min(4, totalDataBits - bitStream.size());
        addBits(bitStream, 0, termLen);

        // Pad to byte boundary
        while (bitStream.size() % 8 != 0) {
            bitStream.add(false);
        }

        // Pad bytes
        int[] padBytes = new int[]{0xEC, 0x11};
        int padIdx = 0;
        while (bitStream.size() < totalDataBits) {
            addBits(bitStream, padBytes[padIdx], 8);
            padIdx = (padIdx + 1) % 2;
        }

        // Convert bit stream to bytes
        int[] dataBytes = new int[spec.dataCapacity];
        for (int i = 0; i < spec.dataCapacity; i++) {
            int b = 0;
            for (int bit = 0; bit < 8; bit++) {
                if (bitStream.get(i * 8 + bit)) {
                    b |= (1 << (7 - bit));
                }
            }
            dataBytes[i] = b;
        }

        // Partition into blocks & compute RS Error Correction
        int blockCount = spec.blocks;
        int dataPerBlock = spec.dataCapacity / blockCount;
        int[][] dataBlocks = new int[blockCount][dataPerBlock];
        int[][] ecBlocks = new int[blockCount][spec.ecBytesPerBlock];

        for (int i = 0; i < blockCount; i++) {
            System.arraycopy(dataBytes, i * dataPerBlock, dataBlocks[i], 0, dataPerBlock);
            ecBlocks[i] = calcEcBytes(dataBlocks[i], spec.ecBytesPerBlock);
        }

        // Interleave data and EC bytes
        List<Integer> interleaved = new ArrayList<>();
        for (int i = 0; i < dataPerBlock; i++) {
            for (int b = 0; b < blockCount; b++) {
                interleaved.add(dataBlocks[b][i]);
            }
        }
        for (int i = 0; i < spec.ecBytesPerBlock; i++) {
            for (int b = 0; b < blockCount; b++) {
                interleaved.add(ecBlocks[b][i]);
            }
        }

        // Convert interleaved bytes to final bit sequence
        List<Boolean> finalBits = new ArrayList<>();
        for (int val : interleaved) {
            addBits(finalBits, val, 8);
        }

        int size = 17 + 4 * spec.version;
        boolean[][] modules = new boolean[size][size];
        boolean[][] reserved = new boolean[size][size];

        // 1. Finder patterns & separators
        placeFinderPattern(modules, reserved, 0, 0);
        placeFinderPattern(modules, reserved, 0, size - 7);
        placeFinderPattern(modules, reserved, size - 7, 0);

        // 2. Alignment patterns
        for (int r : spec.alignCoords) {
            for (int c : spec.alignCoords) {
                if (!reserved[r][c]) {
                    placeAlignmentPattern(modules, reserved, r - 2, c - 2);
                }
            }
        }

        // 3. Timing patterns
        for (int i = 8; i < size - 8; i++) {
            if (!reserved[6][i]) {
                modules[6][i] = (i % 2 == 0);
                reserved[6][i] = true;
            }
            if (!reserved[i][6]) {
                modules[i][6] = (i % 2 == 0);
                reserved[i][6] = true;
            }
        }

        // 4. Dark module
        modules[size - 8][8] = true;
        reserved[size - 8][8] = true;

        // 5. Reserve format info modules
        for (int i = 0; i <= 8; i++) {
            reserved[8][i] = true;
            reserved[i][8] = true;
        }
        for (int i = size - 8; i < size; i++) {
            reserved[8][i] = true;
            reserved[i][8] = true;
        }

        // 6. Data placement (zig-zag)
        int bitIdx = 0;
        int totalBits = finalBits.size();
        boolean upward = true;

        for (int right = size - 1; right > 0; right -= 2) {
            if (right == 6) right--; // Skip vertical timing column

            int rowStart = upward ? size - 1 : 0;
            int rowEnd = upward ? -1 : size;
            int rowStep = upward ? -1 : 1;

            for (int r = rowStart; r != rowEnd; r += rowStep) {
                for (int col = right; col >= right - 1; col--) {
                    if (!reserved[r][col]) {
                        boolean bit = bitIdx < totalBits && finalBits.get(bitIdx++);
                        // Apply Mask 0: (r + c) % 2 == 0
                        if ((r + col) % 2 == 0) {
                            bit = !bit;
                        }
                        modules[r][col] = bit;
                    }
                }
            }
            upward = !upward;
        }

        // 7. Place Format Info (Error Correction L = 01b, Mask = 000b)
        int formatBits = getFormatBits();
        placeFormatBits(modules, formatBits, size);

        return modules;
    }

    private static void addBits(List<Boolean> bitStream, int val, int len) {
        for (int i = len - 1; i >= 0; i--) {
            bitStream.add(((val >> i) & 1) == 1);
        }
    }

    private static int gfMul(int a, int b) {
        if (a == 0 || b == 0) return 0;
        return EXP_TABLE[(LOG_TABLE[a] + LOG_TABLE[b]) % 255];
    }

    private static int[] calcEcBytes(int[] data, int numEcBytes) {
        int[] gen = getRsGenerator(numEcBytes);
        int[] res = new int[numEcBytes];
        for (int b : data) {
            int factor = b ^ res[0];
            System.arraycopy(res, 1, res, 0, numEcBytes - 1);
            res[numEcBytes - 1] = 0;
            if (factor != 0) {
                for (int i = 0; i < numEcBytes; i++) {
                    res[i] ^= gfMul(gen[i], factor);
                }
            }
        }
        return res;
    }

    private static int[] getRsGenerator(int numEcBytes) {
        int[] poly = new int[]{1};
        for (int i = 0; i < numEcBytes; i++) {
            int[] next = new int[poly.length + 1];
            int root = EXP_TABLE[i];
            for (int j = 0; j < poly.length; j++) {
                next[j] ^= gfMul(poly[j], root);
                next[j + 1] ^= poly[j];
            }
            poly = next;
        }
        return poly;
    }

    private static void placeFinderPattern(boolean[][] modules, boolean[][] reserved, int r, int c) {
        for (int y = -1; y <= 7; y++) {
            for (int x = -1; x <= 7; x++) {
                int mr = r + y;
                int mc = c + x;
                if (mr >= 0 && mr < modules.length && mc >= 0 && mc < modules.length) {
                    reserved[mr][mc] = true;
                    if (y >= 0 && y <= 6 && x >= 0 && x <= 6) {
                        modules[mr][mc] = (y == 0 || y == 6 || x == 0 || x == 6 || (y >= 2 && y <= 4 && x >= 2 && x <= 4));
                    } else {
                        modules[mr][mc] = false; // Quiet border separator
                    }
                }
            }
        }
    }

    private static void placeAlignmentPattern(boolean[][] modules, boolean[][] reserved, int r, int c) {
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                int mr = r + y;
                int mc = c + x;
                reserved[mr][mc] = true;
                modules[mr][mc] = (y == 0 || y == 4 || x == 0 || x == 4 || (y == 2 && x == 2));
            }
        }
    }

    private static int getFormatBits() {
        int data = (1 << 3); // Error Correction L = 01b, Mask = 000b
        int rem = data << 10;
        for (int i = 4; i >= 0; i--) {
            if ((rem & (1 << (i + 10))) != 0) {
                rem ^= (0x537 << i);
            }
        }
        return ((data << 10) | rem) ^ 0x5412;
    }

    private static void placeFormatBits(boolean[][] modules, int formatBits, int size) {
        int[] bitOrderRow = new int[]{8, 8, 8, 8, 8, 8, 8, 8, 7, 5, 4, 3, 2, 1, 0};
        int[] bitOrderCol = new int[]{0, 1, 2, 3, 4, 5, 7, 8, 8, 8, 8, 8, 8, 8, 8};

        int[] copy2Row = new int[]{
                size - 1, size - 2, size - 3, size - 4, size - 5, size - 6, size - 7,
                8, 8, 8, 8, 8, 8, 8, 8
        };
        int[] copy2Col = new int[]{
                8, 8, 8, 8, 8, 8, 8,
                size - 8, size - 7, size - 6, size - 5, size - 4, size - 3, size - 2, size - 1
        };

        for (int i = 0; i < 15; i++) {
            boolean bit = ((formatBits >> (14 - i)) & 1) == 1;
            modules[bitOrderRow[i]][bitOrderCol[i]] = bit;
            modules[copy2Row[i]][copy2Col[i]] = bit;
        }
    }
}
