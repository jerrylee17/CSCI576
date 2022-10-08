import java.util.Stack;
// I'm not going to use this
public class NumberOfIslands {
    int width = 640;
    int height = 480;
    int[][] bitmap_;
    NumberOfIslands() {
        bitmap_ = new int[height][width];
    }
    public void mapBitmapTo2D(int[] bitmap) {
        for (int y = 0; y < height; y++) {
            int row_loc = y * width;
            for (int x = 0; x < width; x++) {
                int index = row_loc + x;
                bitmap_[y][x] = bitmap[index];
            }
        }
    }
    public int[] map2DToBitmap() {
        int[] bitmap = new int[height*width];
        for (int y = 0; y < height; y++) {
            int row_loc = y * width;
            for (int x = 0; x < width; x++) {
                int index = row_loc + x;
                bitmap[index] = bitmap_[y][x];
            }
        }
        return bitmap;
    }

    public int maxAreaOfIsland(int[][] grid) {
        boolean[][] seen = new boolean[grid.length][grid[0].length];
        int[] dr = new int[]{1, -1, 0, 0};
        int[] dc = new int[]{0, 0, 1, -1};

        int ans = 0;
        for (int r0 = 0; r0 < grid.length; r0++) {
            for (int c0 = 0; c0 < grid[0].length; c0++) {
                if (grid[r0][c0] == 1 && !seen[r0][c0]) {
                    int shape = 0;
                    Stack<int[]> stack = new Stack();
                    stack.push(new int[]{r0, c0});
                    seen[r0][c0] = true;
                    while (!stack.empty()) {
                        int[] node = stack.pop();
                        int r = node[0], c = node[1];
                        shape++;
                        for (int k = 0; k < 4; k++) {
                            int nr = r + dr[k];
                            int nc = c + dc[k];
                            if (0 <= nr && nr < grid.length &&
                                    0 <= nc && nc < grid[0].length &&
                                    grid[nr][nc] == 1 && !seen[nr][nc]) {
                                stack.push(new int[]{nr, nc});
                                seen[nr][nc] = true;
                            }
                        }
                    }
                    ans = Math.max(ans, shape);
                }
            }
        }
        return ans;
    }

}
