/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpinapl.core;

public class PointDirection {

    public PPoint point;
    public int direction;

    public PointDirection(PPoint pt, int dir) {
        point = pt;
        direction = dir;
    }

    @Override
    public String toString() {
        return point.toString() + "dir=" + direction;
    }
}
