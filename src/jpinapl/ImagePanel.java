/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpinapl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import jpinapl.core.IStepNotifier;
import jpinapl.core.PPoint;
import jpinapl.core.Program;

public class ImagePanel extends JPanel {

    public boolean showPos_N_BPs = true;
    public Program program;

    public BufferedImage image;
    public double zoom = 2;
    public Dimension size;
    public Point location = new Point(0, 0);

    public IStepNotifier stepNotifier = new IStepNotifier() {

        public void prod() {
            repaint();
        }
    };

    public void setImage(BufferedImage image) {
        this.image = image;
        zoom = 2;        
        location = new Point(0, 0);

        recomputeSize();
        recomputeLocation();
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public void recomputeSize() {
        size = new Dimension((int)(image.getWidth() * zoom), (int)(image.getHeight() * zoom));
        setPreferredSize(size);
        setSize(size);
    }

    public void recomputeLocation() {
        setLocation(location);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        dopaint(g);
    }

    private void dopaint(Graphics g) {
        if (image != null) {
            g.drawImage(image, 0, 0, (int)size.getWidth(), (int)size.getHeight(), this);
            if (showPos_N_BPs && program != null) {
                g.setColor(Color.red);
                g.drawOval((int)(program.code.x * zoom)-5+(int)(zoom*0.5), (int)(program.code.y * zoom)-5+(int)(zoom*0.5), 10,10);
            }
            g.setColor(Color.MAGENTA);
            for (PPoint p : program.code.breakpoints) {
                g.drawOval((int)(p.X * zoom)-5+(int)(zoom*0.5), (int)(p.Y * zoom)-5+(int)(zoom*0.5), 10,10);
            }
        }
        //System.err.println("PAINT COMPONENT");
    }
}
