/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpinapl.core;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Code {

    public Bitmap codeimg;
    public Bitmap dispimg;
    public Random random = new Random();
    public int x, y;
    public boolean clockwise = true;
    public int direction = 0;
    public boolean running = true;
    public boolean breakpoint = false;
    public boolean forceturn = false;
    public boolean forcebackwardsallow = true; //First one should be allowed
    public int[] integers;
    public boolean executepixel = true;
    public ArrayList<Integer> stack;
    public ArrayList<PointDirection> programstack;
    public String stdout = "";
    public boolean speedupwhite = false;
    public boolean waitforinput = false;
    public int inputmethod = 0;
    public int inputarg = 0;
    public long numits;
    public boolean datachanged = false;
    public String errorString = "";
    public boolean maskonlyNOPs = false;
    public ArrayList<PPoint> breakpoints = new ArrayList<PPoint>();
    public static int COLOR_BLACK = new Color(0, 0, 0).getRGB();
    public static int COLOR_WHITE = new Color(255, 255, 255).getRGB();
    public static int COLOR_TEALBRIGHT = new Color(0, 150, 150).getRGB();
    public static int COLOR_TEALDARK = new Color(0, 100, 100).getRGB();

    public Code(File file) {
        try {
            codeimg = new Bitmap(file);
            dispimg = new Bitmap(file);
        } catch (Exception e) {
            e.printStackTrace();
            errorString = "Errr. Something went wrong. Please select a valid/supported image type (PNG,BMP,GIF)";
        }
        integers = new int[256];
        for (int i = 0; i < 256; i++) {
            integers[i] = 0;
        }
        stack = new ArrayList<Integer>();
        programstack = new ArrayList<PointDirection>();
    }

    public void newdispimg() {
//        try {
//            //dispimg = (Bitmap) codeimg.clone();
//        } catch (CloneNotSupportedException ex) {
//            Logger.getLogger(Code.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public PPoint findpixel(int R, int G, int B) {
        System.out.println("FINDING PIXEL: ["+ R + "," + G + "," + B + "]");
        for (int iy = 0; iy < codeimg.getHeight(); iy++) {
            for (int ix = 0; ix < codeimg.getWidth(); ix++) {
                if ((codeimg.getPixel(ix, iy).getRed() == R || R == -1) && (codeimg.getPixel(ix, iy).getGreen() == G || G == -1) && (codeimg.getPixel(ix, iy).getBlue() == B || B == -1)) {
                    //System.out.println("FOUND ["+ R + "," + G + "," + B + "]  at [" + ix + "," + iy + "]");
                    return new PPoint(ix, iy);
                }
            }
        }
        return new PPoint(-1, -1);
    }

    public PPoint getpointfromdir(int lx, int ly, int dir) {
        int tx = 0, ty = 0;
        if (dir < 0) {
            dir = dir + 4;
        }
        switch (dir % 4) {
            case 0:
                tx = lx;
                ty = ly - 1;
                break;
            case 1:
                tx = lx + 1;
                ty = ly;
                break;
            case 2:
                tx = lx;
                ty = ly + 1;
                break;
            case 3:
                tx = lx - 1;
                ty = ly;
                break;
        }
        return new PPoint(tx, ty);
    }

    public boolean checkdir(int dir, int tx, int ty) {
        PPoint tpoint = getpointfromdir(x, y, dir);
        if (tpoint.X < 0 || tpoint.X >= codeimg.getWidth() || tpoint.Y < 0 || tpoint.Y >= codeimg.getHeight()) {
            return false;
        }
        if (codeimg.getPixel(tpoint.X, tpoint.Y).getRGB() != COLOR_BLACK) {
            return true;
        } else {
            return false;
        }
    }

    public PointDirection findnext(boolean allowbackwards) {
        ArrayList<Integer> checklist = new ArrayList<Integer>();
        if (!forceturn) {
            checklist.add(0);
        }
        if (clockwise) {
            checklist.add(1);
            if (allowbackwards) {
                checklist.add(2);
            }
            checklist.add(3);
        } else {
            checklist.add(3);
            if (allowbackwards) {
                checklist.add(2);
            }
            checklist.add(1);
        }
        if (forceturn) {
            checklist.add(0);
        }

        for (int i : checklist) {
            if (checkdir(direction + i, x, y)) {
                if ((i == 1 || i == 3) && forceturn) {
                    forceturn = false;
                }
                return new PointDirection(getpointfromdir(x, y, direction + i), direction + i);
            }
        }
        return new PointDirection(new PPoint(-1, -1), -1);
    }

    public void step() {
        PointDirection tpd = findnext(forcebackwardsallow);
        //System.err.println("direction: " + tpd);
        forcebackwardsallow = false;
        if (tpd.point.X != -1 && tpd.point.Y != -1) {
            PPoint breakptfound = null;
            for (PPoint p : breakpoints) {
                if (tpd.point.X == p.X) {
                    if (tpd.point.Y == p.Y) {
                        breakptfound = p;
                    }
                }
            }

            if (breakptfound == null) {

                numits++;
                direction = tpd.direction;
                x = tpd.point.X;
                y = tpd.point.Y;
                if (codeimg.getPixel(x, y).getRGB() == COLOR_WHITE || !maskonlyNOPs) {
                    if (dispimg.getPixel(x, y).getRGB() == COLOR_TEALBRIGHT) {
                        dispimg.setPixel(x, y, COLOR_TEALDARK);
                    } else {
                        dispimg.setPixel(x, y, COLOR_TEALBRIGHT);
                    }
                }
                if (executepixel) {
                    applylogic();
                } else {
                    executepixel = true;
                }
                if (speedupwhite && codeimg.getPixel(x, y).getRGB() == COLOR_WHITE) {
                    step();
                }
            } else {
                breakpoints.remove(breakptfound);
                breakpoint = true;
                //running = false;
            }
        } else {
            running = false;
        }
    }

    public void applylogic() {
        /*ALL PIXELS:
        255/0/0 - START OF PROGRAM
        0/0/255 - RETURN (will END PROGRAM if no position/direction is on the stack)
        0/1/255 - FORCE QUIT
        255/128/0 - ANTICLOCKWISE
        255/0/128 - CLOCKWISE
        255/128/128 - FORCE TURN
        1/128/x - JUMP TO, ID = x (matches JUMP TO TARGET)
        2/128/x - JUMP TO TARGET, ID = x (matches JUMP TO)
        255/255/x - SET DIRECTION TO x
        128/x/y - SET INTEGER x TO VALUE y
        129/x/y - ADD VALUE y TO INTEGER x
        127/x/y - SUBSTRACT VALUE y FROM INTEGER x
        126/x/y - SUBSTRACT INTEGER y FROM INTEGER x (RESULT IN INTEGER x)
        130/x/y - ADD INTEGER y TO INTEGER x (RESULT IN INTEGER x)
        120/x/y - COMPARE INTEGER x TO VALUE y (IF EQUAL, EXECUTE NEXT PROGRAM PIXEL; IF NOT, DO NOT EXECUTE IT).


        /*NEW:
        122/x/y - COMPARE INTEGER x TO INTEGER y (IF X>Y, EXECUTE NEXT PROGRAM PIXEL; IF NOT, DO NOT EXECUTE IT)
        1/200/x - CALL PROCEDURE, ID = x (matches METHOD POINTER)
        2/200/x - PROCEDURE POINTER, ID = x
        3/x/y - PUSH Y ONTO STACK (but only if 4/x/* exists), CALL FUNCTION X
        4/x/y - FUNCTION POINTER, ID = x, argument = y (if 3/x/y is called and no corresponding 4/x/y is found, the first occuring 4/x/* is used instead)
        (this allows you to exclude certain arguments)



        125/x/y - SET INTEGER X TO VALUE OF INTEGER Y
        121/x/y - COMPARE INTEGER x TO INTEGER y (IF EQUAL, EXECUTE NEXT PROGRAM PIXEL; IF NOT, DO NOT EXECUTE IT).
        100/0/x - Push x onto the stack
        100/1/x - Pop from the stack into INTEGER x
        100/2/0 - Pop from the stack and output as ASCII (stdout)
        100/3/x - Push INTEGER X onto the stack
        100/255/255 - Pop from the stack (discard)
        100/255/0 - Pop YXBGR and set pixel using those values
        100/255/1 - Pop from PROGRAMSTACK, then push X, Y, Direction to the stack
        100/255/2 - Pop Direction, Y, X. Push that to PROGRAMSTACK
        100/255/3 - Push current stack size to the stack
        100/255/4 - Pop YX and push RGB of pixel at (X,Y)
        255/128/255 - Set direction to a random direction
        80/0/x - Output X as ASCII (stdout)
        80/1/x - Output INTEGER X as ASCII (stdout)
        80/2/x - Output INTEGER X (convert to String, output to stdout)
        255/200/200 - Don't execute next pixel ('trampoline')

        240/0/x - Ask for input, convert to integer and store in INTEGER X
        240/1/0 - Ask for input, push input (ascii values) to stack
        240/1/1 - Ask for input, push input (converted to integer) to stack
         *

         * */


        Color clr = codeimg.getPixel(x, y);
        //System.err.println("Applying logic to: " + clr + " @ " + x + "," + y);

        if (clr.getRed() == 3) {
            PPoint temppt = findpixel(4, clr.getGreen(), clr.getBlue());
            if (temppt.X >= 0 && temppt.X < codeimg.getWidth() && temppt.Y >= 0 && temppt.Y < codeimg.getHeight()) {
                programstack.add(new PointDirection(new PPoint(x, y), direction));
                forcebackwardsallow = true;
                x = temppt.X;
                y = temppt.Y;
                stack.add(clr.getBlue());
                datachanged = true;
            } else {
                temppt = findpixel(4, clr.getGreen(), -1);
                if (temppt.X >= 0 && temppt.X < codeimg.getWidth() && temppt.Y >= 0 && temppt.Y < codeimg.getHeight()) {
                    programstack.add(new PointDirection(new PPoint(x, y), direction));
                    forcebackwardsallow = true;
                    x = temppt.X;
                    y = temppt.Y;
                    stack.add(clr.getBlue());
                    datachanged = true;
                }
            }
        }

        if (clr.getRed() == 100 && clr.getGreen() == 255 && clr.getBlue() == 4) {
            if (stack.size() > 1) {
                int tX, tY;
                tY = stack.get(stack.size() - 1);
                stack.remove(stack.size() - 1);
                tX = stack.get(stack.size() - 1);
                stack.remove(stack.size() - 1);
                Color colr = codeimg.getPixel(tX, tY);
                stack.add(colr.getRed());
                stack.add(colr.getGreen());
                stack.add(colr.getBlue());
                datachanged = true;
            }
        }

        if (clr.getRed() == 100 && clr.getGreen() == 255 && clr.getBlue() == 255) {
            if (stack.size() > 0) {
                stack.remove(stack.size() - 1);
                datachanged = true;
            }
        }


        if (clr.getRed() == 100 && clr.getGreen() == 255 && clr.getBlue() == 1) {
            if (programstack.size() > 0) {
                PointDirection tempptdr = programstack.get(programstack.size() - 1);
                programstack.remove(programstack.size() - 1);
                stack.add(tempptdr.point.X);
                stack.add(tempptdr.point.Y);
                stack.add(tempptdr.direction);
                datachanged = true;
            }
        }

        if (clr.getRed() == 100 && clr.getGreen() == 255 && clr.getBlue() == 2) {
            if (stack.size() > 2) {
                int tX, tY, tD;
                tD = stack.get(stack.size() - 1);
                stack.remove(stack.size() - 1);
                tY = stack.get(stack.size() - 1);
                stack.remove(stack.size() - 1);
                tX = stack.get(stack.size() - 1);
                stack.remove(stack.size() - 1);
                programstack.add(new PointDirection(new PPoint(tX, tY), tD));
                datachanged = true;
            }
        }

        if (clr.getRed() == 100 && clr.getGreen() == 255 && clr.getBlue() == 3) {
            stack.add(stack.size());
            datachanged = true;
        }

        if (clr.getRed() == 240 && clr.getGreen() == 0) {
            waitforinput = true;
            inputmethod = 0;
            inputarg = clr.getBlue();
        }

        if (clr.getRed() == 240 && clr.getGreen() == 1 && clr.getBlue() == 0) {
            waitforinput = true;
            inputmethod = 1;
        }

        if (clr.getRed() == 240 && clr.getGreen() == 1 && clr.getBlue() == 1) {
            waitforinput = true;
            inputmethod = 2;
        }


        if (clr.getRed() == 1 && clr.getGreen() == 200) {
            programstack.add(new PointDirection(new PPoint(x, y), direction));
            forcebackwardsallow = true;
            PPoint temppt = findpixel(2, 200, clr.getBlue());
            if (temppt.X > 0 && temppt.X < codeimg.getWidth() && temppt.Y > 0 && temppt.Y < codeimg.getHeight()) {
                x = temppt.X;
                y = temppt.Y;
            }
            datachanged = true;
        }

        if (clr.getRed() == 255 & clr.getGreen() == 128 && clr.getBlue() == 0) {
            clockwise = false;
        }
        if (clr.getRed() == 255 & clr.getGreen() == 0 && clr.getBlue() == 128) {
            clockwise = true;
        }
        if (clr.getRed() == 255 & clr.getGreen() == 128 && clr.getBlue() == 128) {
            forceturn = true;
        }
        if (clr.getRed() == 1 && clr.getGreen() == 128) {
            forcebackwardsallow = true;
            PPoint temppt = findpixel(2, 128, clr.getBlue());
            if (temppt.X > 0 && temppt.X < codeimg.getWidth() && temppt.Y > 0 && temppt.Y < codeimg.getHeight()) {
                x = temppt.X;
                y = temppt.Y;
            }
        }

        if (clr.getRed() == 0 && clr.getGreen() == 0 && clr.getBlue() == 255) {
            if (programstack.size() > 0) {
                //executepixel = false;
                PointDirection tempptdr = programstack.get(programstack.size() - 1);
                programstack.remove(programstack.size() - 1);
                x = tempptdr.point.X;
                y = tempptdr.point.Y;
                direction = tempptdr.direction;
                datachanged = true;
            } else {
                running = false;
            }
        }

        if (clr.getRed() == 0 && clr.getGreen() == 1 && clr.getBlue() == 255) {
            running = false;
        }


        if (clr.getRed() == 255 && clr.getGreen() == 255 && clr.getBlue() != 255) {
            direction = clr.getBlue() % 4;
        }

        if (clr.getRed() == 255 && clr.getGreen() == 128 && clr.getBlue() == 255) {
            direction = random.nextInt(4);
        }

        if (clr.getRed() == 128) {
            integers[clr.getGreen()] = clr.getBlue();
            datachanged = true;
        }
        if (clr.getRed() == 125) {
            integers[clr.getGreen()] = integers[clr.getBlue()];
            datachanged = true;
        }
        if (clr.getRed() == 129) {
            integers[clr.getGreen()] += clr.getBlue();
            datachanged = true;
        }
        if (clr.getRed() == 127) {
            integers[clr.getGreen()] -= clr.getBlue();
            datachanged = true;
        }
        if (clr.getRed() == 126) {
            integers[clr.getGreen()] -= integers[clr.getBlue()];
            datachanged = true;
        }
        if (clr.getRed() == 130) {
            integers[clr.getGreen()] += integers[clr.getBlue()];
            datachanged = true;
        }

        if (clr.getRed() == 120) {
            if (integers[clr.getGreen()] == clr.getBlue()) {
                executepixel = true;
            } else {
                executepixel = false;
            }
        }
        if (clr.getRed() == 121) {
            if (integers[clr.getGreen()] == integers[clr.getBlue()]) {
                executepixel = true;
            } else {
                executepixel = false;
            }
        }

        if (clr.getRed() == 122) {
            if (integers[clr.getGreen()] > integers[clr.getBlue()]) {
                executepixel = true;
            } else {
                executepixel = false;
            }
        }

        if (clr.getRed() == 100 && clr.getGreen() == 0) {
            stack.add(clr.getBlue());
            datachanged = true;
        }
        if (clr.getRed() == 100 && clr.getGreen() == 3) {
            stack.add(integers[clr.getBlue()]);
            datachanged = true;
        }
        if (clr.getRed() == 100 && clr.getGreen() == 1) {
            if (stack.size() > 0) {
                integers[clr.getBlue()] = stack.get(stack.size() - 1);
                stack.remove(stack.size() - 1);
                datachanged = true;
            }
        }
        if (clr.getRed() == 100 && clr.getGreen() == 2 && clr.getBlue() == 0) {
            if (stack.size() > 0) {
                stdout += "" + Character.toString((char) stack.get(stack.size() - 1).intValue());
                stack.remove(stack.size() - 1);
                datachanged = true;
            }
        }
        if (clr.getRed() == 100 && clr.getGreen() == 255 && clr.getBlue() == 0) {
            int X = 0, Y = 0, R = 0, G = 0, B = 0;
            if (stack.size() >= 5) {
                Y = Math.min(Math.max(0, stack.get(stack.size() - 1)), codeimg.getHeight() - 1);
                stack.remove(stack.size() - 1);
                X = Math.min(Math.max(0, stack.get(stack.size() - 1)), codeimg.getWidth() - 1);
                stack.remove(stack.size() - 1);
                B = Math.min(Math.max(0, stack.get(stack.size() - 1)), 255);
                stack.remove(stack.size() - 1);
                G = Math.min(Math.max(0, stack.get(stack.size() - 1)), 255);
                stack.remove(stack.size() - 1);
                R = Math.min(Math.max(0, stack.get(stack.size() - 1)), 255);
                stack.remove(stack.size() - 1);
                codeimg.setPixel(X, Y, new Color(R, G, B).getRGB());
                dispimg.setPixel(X, Y, new Color(R, G, B).getRGB());
                datachanged = true;
            }
        }

        if (clr.getRed() == 80 && clr.getGreen() == 0) {
            stdout += Character.valueOf((char) clr.getBlue());
        }
        if (clr.getRed() == 80 && clr.getGreen() == 1) {
            stdout += Character.valueOf((char) integers[clr.getBlue()]);
        }
        if (clr.getRed() == 80 && clr.getGreen() == 2) {
            stdout += "" + integers[clr.getBlue()];
        }

        if (clr.getRed() == 255 && clr.getGreen() == 200 && clr.getBlue() == 200) {
            executepixel = false;
        }
    }

    public void input(String input) {
        //0: 240/0/x - Ask for input, convert to integer and store in INTEGER X
        //1: 240/1/0 - Ask for input, push input (ascii values) to stack
        //2: 240/1/1 - Ask for input, push input (converted to integer) to stack
        if (waitforinput) {
            try {
                switch (inputmethod) {
                    case 0:
                        integers[inputarg] = Integer.valueOf(input);
                        inputarg = 0;
                        break;
                    case 1:
                        while (input.length() > 0) {
                            stack.add(Integer.valueOf(input.substring(input.length() - 1, 1).charAt(0))); //?? -- WAS: stack.Add(System.Convert.ToInt16(input.Substring(input.Length - 1, 1)[0]));
                            input = input.substring(0, input.length() - 1);
                        }
                        break;
                    case 2:
                        stack.add(Integer.valueOf(input));
                        break;
                }
                datachanged = true;
                waitforinput = false;
            } catch (Exception e) {
                errorString = "Error: wrong format given. Please provide input in the proper format.   OR    Error: input was too large (OverflowException)";
            }
        }
    }
}
