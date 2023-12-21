import java.util.Random;
import java.util.Scanner;
import java.io.IOException;
import java.lang.System;

public class Main {
    public static final char treasure = '$', path = ' ', player = '@', wall = '#';
    public static boolean lose = false, win = false;
    public static String playerOrder, green = "\u001B[32m", yellow = "\u001B[33m", white = "\u001B[47m", resetColor = "\u001B[0m";
    public static char playerOrderChar;
    public static long elapsedTime, startTime, endTime;
    public static boolean[][] visitedCells = new boolean[21][41];
    public static int mapShown = 0, order = 0, lives = 3, newPlayerX = 1, newPlayerY = 1, playerX = 1, playerY = 1, moved = 0, treasureX, treasureY, minute = 0, second = 0;
    public static final char[] directions = {'w', 'a', 's', 'd'};
    public static final char[][] map = { //map is 21*41
            "#########################################".toCharArray(),
            "#     ##      #######     ####    ###  ##".toCharArray(),
            "# ###    ## #         ###     ###       #".toCharArray(),
            "#   # # ### ##### ###   ##### ### ##### #".toCharArray(),
            "### ### ###  #### ##### ##    ###  #### #".toCharArray(),
            "#   ### #### ####     #### ####    #### #".toCharArray(),
            "# ## #     # #### ### ####      ####### #".toCharArray(),
            "#    # ## ##  ### ### ####### ######### #".toCharArray(),
            "## #   ## ###     #   ##             ## #".toCharArray(),
            "#    ## # ### ##### #### ######### #### #".toCharArray(),
            "# ## ## #     #####    #      #### ###  #".toCharArray(),
            "# ## ## ## ######## ## ###### ####     ##".toCharArray(),
            "# ##       #######  ## ####   ######## ##".toCharArray(),
            "# ## ### ######### ###   ## #  ####    ##".toCharArray(),
            "# ## ###   ##      ######## ## #### #####".toCharArray(),
            "#        ###### ## ####     ## #### #####".toCharArray(),
            "### ##       ## ## ###### ##     #   ####".toCharArray(),
            "### ##### ##    ## ###### #####   #  ####".toCharArray(),
            "##  #  ## #  ## ##      # ###### ## #####".toCharArray(),
            "#  ###    ## ## #######   ######    #####".toCharArray(),
            "#########################################".toCharArray(),
    };


    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scan = new Scanner(System.in);

//        Menu
        do {
            menu();
            order = scan.nextInt();
            if (order == 1) {
                continue;
            } else if (order == 2) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                System.out.println("**********");
                System.out.println("Come back soon and challenge me again ;)");
                System.exit(0);
            } else {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                System.out.println("Error: Wrong order number entered please try again");
                pressEnter();
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
        } while (order > 2 || order == 0);

//        System.out.println("Choose Start location X:");
//        newPlayerX= scan.nextInt();
//        System.out.println("Loation Y:");
//        newPlayerY= scan.nextInt();


//        Treasure
        setTreasure();
        findTreasure();


//        Start Game

        game();

        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();


//        After Game
        for (int i = 0; i < 21; i++) {
            for (int j = 0; j < 41; j++) {
                if (map[i][j] == treasure) {
                    System.out.print(green + map[i][j] + resetColor);
                } else if (map[i][j] == player) {
                    System.out.print(yellow + map[i][j] + resetColor);
                }else if (visitedCells[i][j]) {
                    System.out.print(white + map[i][j] + resetColor); // White background
                } else {
                    System.out.print(map[i][j]);
                }
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("Lives Left = " + lives);
        System.out.println("Time = " + minute + " Minutes, " + second + " Seconds");
        System.out.println("Moves Count = " + moved);


//        Outcome
        if (win) {
            System.out.println();
            System.out.println("Congratulations! You Won");
            pressEnter();
        } else if (lose) {
            System.out.println();
            System.out.println("You Lost! Come back again soon:)");
            pressEnter();
        }

    }

    public static void menu() {
        System.out.println("**********");
        System.out.println("Welcome to the TOMB OF THE MASK!\n");
        System.out.println("Do you think you can win??");
        System.out.println("DO YOU DARE TO CHALLENGE THE TOMB??\n");
        System.out.println("1- I challenge you! (Start the game)");
        System.out.println("2- I'm not in the mood. (Exit)\n");
    }

    public static void game() {
        do {
            setPlayer();
            try {
                printGame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            startTime = System.nanoTime();
            movePlayer();
            winCheck();
            loseCheck();
            endTime = System.nanoTime();
            elapsedTime = endTime - startTime;
            calculateTime();
        } while (!lose && !win);
    }

    public static void setTreasure() {
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(20);
            y = rand.nextInt(40);
        } while (map[x][y] != path && x != 1 && y != 1 && x < 9 && y < 16);

        map[x][y] = treasure;
    }

    public static void setPlayer() {

        map[playerX][playerY] = path;
        visitedCells[playerX][playerY] = true;
        playerX = newPlayerX;
        playerY = newPlayerY;
        map[newPlayerX][newPlayerY] = player;
    }

    public static void findTreasure() {
        for (int i = 0; i < 21; i++) {
            for (int j = 0; j < 41; j++) {
                if (map[i][j] == treasure) {
                    treasureX = i;
                    treasureY = j;
                }
            }
        }
    }

    public static void movePlayer() {
        Scanner scan = new Scanner(System.in);

        playerOrder = scan.next();
        playerOrderChar = playerOrder.charAt(0);
        if (playerOrderChar == directions[0]) {
            newPlayerX -= 1;
            if (map[newPlayerX][newPlayerY] == wall) {
                lives--;
                newPlayerX += 1;
                if (lives == 0) {
                    lose = true;
                }
            } else {
                moved++;
            }
        } else if (playerOrderChar == directions[1]) {
            newPlayerY -= 1;
            if (map[newPlayerX][newPlayerY] == wall) {
                lives--;
                newPlayerY += 1;
                if (lives == 0) {
                    lose = true;
                }
            } else {
                moved++;
            }
        } else if (playerOrderChar == directions[2]) {
            newPlayerX += 1;
            if (map[newPlayerX][newPlayerY] == wall) {
                lives--;
                newPlayerX -= 1;
                if (lives == 0) {
                    lose = true;
                }
            } else {
                moved++;
            }
        } else if (playerOrderChar == directions[3]) {
            newPlayerY += 1;
            if (map[newPlayerX][newPlayerY] == wall) {
                lives--;
                newPlayerY -= 1;
                if (lives == 0) {
                    lose = true;
                }
            } else {
                moved++;
            }
        }
    }

    public static void calculateTime() {
        long elapsedSeconds = elapsedTime / 1000000000;
        minute += (int) (elapsedSeconds / 60);
        second += (int) (elapsedSeconds % 60);
    }

    public static void printGame() throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        System.out.println("Time Passed: " + minute + " Minutes  " + second + " Seconds");
        System.out.println();
        System.out.println("Lives = " + lives + "      " + "Moves count = " + moved);
        System.out.println();
        System.out.println("Hints:\n#: Walls - @: Player - $: Treasure\n");

        //Print map first time
        if (mapShown == 0) {
            for (int i = 0; i < 21; i++) {
                for (int j = 0; j < 41; j++) {
                    if (map[i][j] == treasure) {
                        System.out.print(green + map[i][j] + resetColor);
                    } else if (map[i][j] == player) {
                        System.out.print(yellow + map[i][j] + resetColor);
                    } else {
                        System.out.print(map[i][j]);
                    }
                }
                System.out.println();
            }
        } else { //Hidden Map
            for (int i = 0; i < 21; i++) {
                for (int j = 0; j < 41; j++) {
                    if (i - playerX <= 2 && i >= playerX - 2 && j - playerY <= 4 && j >= playerY - 4) {
                        if (map[i][j] == treasure) {
                            System.out.print(green + map[i][j] + resetColor);
                        } else if (map[i][j] == player) {
                            System.out.print(yellow + map[i][j] + resetColor);
                        } else if (visitedCells[i][j]) {
                            System.out.print(white + map[i][j] + resetColor); // White background
                        } else {
                            System.out.print(map[i][j]);
                        }
                    } else {
                        System.out.print('?');
                    }
                }
                System.out.println();
            }

        }
        mapShown++;
    }

    public static void winCheck() {
        if (newPlayerY == treasureY) {
            if (newPlayerX == treasureX) {
                win = true;
            }
        }
    }

    public static void loseCheck() {
        if (lives == 0) {
            lose = true;
        }
    }

    //  Not Part of the Game Methods
    public static void pressEnter() {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Press Enter to continue...");
        scanner.nextLine(); // Wait for Enter key press
    }
}