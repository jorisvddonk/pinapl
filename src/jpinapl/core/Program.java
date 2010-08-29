/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpinapl.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Program {

    public boolean running;
    public Code code;
    public long timerInterval = 1;
    public int stepsPerTick = 1;
    private ArrayList<IStepNotifier> stepNotifiers = new ArrayList<IStepNotifier>();
    private Timer timer = new Timer();
    private TimerTask timerTask;
    public String prgStats = "";

    public Program(File file) {
        code = new Code(file);
        PPoint temppt = code.findpixel(255, 0, 0);
        int pxsize = 1;
        while (code.codeimg.getPixel(temppt.X + 1, temppt.Y + 1).getRGB() == Color.RED.getRGB()) {
            temppt.X += 1;
            temppt.Y += 1;
            pxsize += 1;
        }
        if (pxsize > 1) {
            System.err.println("SCALED IMAGE DETECTED!!!");
            code.codeimg.image = getScaledInstance(code.codeimg.image, code.codeimg.image.getWidth() / pxsize, code.codeimg.image.getHeight() / pxsize, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, false);
            code.dispimg.image = getScaledInstance(code.dispimg.image, code.dispimg.image.getWidth() / pxsize, code.dispimg.image.getHeight() / pxsize, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, false);
            temppt.X = temppt.X / pxsize;
            temppt.Y = temppt.Y / pxsize;
        }


        if (temppt.X != -1 && temppt.Y != -1) {
            code.x = temppt.X;
            code.y = temppt.Y;
        }
    }

    public void updateStats() {
        prgStats = "X: " + code.x + "\n" +
                "Y: " + code.y + "\n" +
                "Direction: " + code.direction + "\n" +
                (code.clockwise ? "Clockwise->" : "<-Counterclockwise") + "\n" +
                "Force turn: " + (code.forceturn ? "true" : "false") + "\n" +
                "Force backwards allow: " + (code.forcebackwardsallow ? "true" : "false") + "\n" +
                "Execute pixel: " + (code.executepixel ? "true" : "false") + "\n" +
                "Waiting for input: " + (code.waitforinput ? "true (" + code.inputmethod + ")" : "false") + "\n" +
                "Running: " + (code.running ? "true" : "false") + "\n" +
                "Num iterations: " + code.numits + "\n" +
                "Stack count: " + code.stack.size() + "\n" +
                "Programstack count: " + code.programstack.size();
    }

    public void resched(long timeout) {
        timerTask = new TimerTask() {

            @Override
            public void run() {
                steps();
            }
        };
        timer.scheduleAtFixedRate(timerTask, timeout, timerInterval);
    }

    public void setTimerInterval(long interval) {
        timerInterval = Math.max(1, interval);
        if (running) {
            long timeout = System.currentTimeMillis() - timerTask.scheduledExecutionTime();
            //System.err.println("timeout: " + timeout);
            if (timeout < 0) {
                timeout = 0;
            }
            timerTask.cancel();
            resched(timeout);
        }
    }

    private void start() {
        running = true;
        resched(0);
    }

    private void stop() {
        if (running) {
            timerTask.cancel();
            running = false;
        }
    }

    public void startstop() {
        if (running) {
            stop();
        } else {
            start();
        }
    }

    public void addStepNotifier(IStepNotifier stepNotifier) {
        this.stepNotifiers.add(stepNotifier);
    }

    private void steps() {
        stepN(stepsPerTick);
    }

    private void stepN(int n) {
        for (int i = 0; i < n; i++) {
            if (step(false) == true) {
                break;
            }
        }
        notifyStepNotifiers();
    }

    public void step() {
        step(false);
    }

    /**
     *
     * @param prod
     * @return true if broken, false if proper proceedings have occured!
     */
    public boolean step(boolean prod) {
        code.step();
        updateStats();
        if (prod) {
            notifyStepNotifiers();
        }
        if (code.breakpoint) {
            stop();
            code.breakpoint = false;
            return true;
        }
        return false;
        //PColor p = code.codeimg.getPixel(code.x, code.y);
        //System.out.println("[" + code.x + "," + code.y + "] -- " + p + " --- " + p.getRed() + "," + p.getGreen() + "," + p.getBlue());
    }

    public void notifyStepNotifiers() {
        for (IStepNotifier stepNotifier : stepNotifiers) {
            stepNotifier.prod();
        }
    }

    /**
     * Convenience method that returns a scaled instance of the
     * provided {@code BufferedImage}.
     * FROM: http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
     *
     * @param img the original image to be scaled
     * @param targetWidth the desired width of the scaled instance,
     *    in pixels
     * @param targetHeight the desired height of the scaled instance,
     *    in pixels
     * @param hint one of the rendering hints that corresponds to
     *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality if true, this method will use a multi-step
     *    scaling technique that provides higher quality than the usual
     *    one-step technique (only useful in downscaling cases, where
     *    {@code targetWidth} or {@code targetHeight} is
     *    smaller than the original dimensions, and generally only when
     *    the {@code BILINEAR} hint is specified)
     * @return a scaled version of the original {@code BufferedImage}
     */
    public BufferedImage getScaledInstance(BufferedImage img,
            int targetWidth,
            int targetHeight,
            Object hint,
            boolean higherQuality) {
        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage) img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }
}
