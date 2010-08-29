/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpinapl.core;

public class PPoint {

    public int X, Y;

    public PPoint(int ix, int iy) {
        X = ix;
        Y = iy;
    }

    @Override
    public String toString() {
        return "Point: [" + X + "," + Y + "]";
    }
}
