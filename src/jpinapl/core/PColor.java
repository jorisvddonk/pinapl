///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package jpinapl.core;
//
//
//
//public class PColor {
//
//    int r;
//    int g;
//    int b;
//    int rgb;
//
//    public PColor(int r, int g, int b) {
//        this.r = r;
//        this.g = g;
//        this.b = b;
//    }
//
////    public PColor(Color color) {
////        this.r = color.getRed();
////        this.g = color.getGreen();
////        this.b = color.getBlue();
////    }
//
//    PColor(int rgb) {
//        this.rgb = 0x00ffffff & rgb;
//        this.r = (this.rgb >> 16) & 0xFF;
//        this.g = (this.rgb >> 8) & 0xFF;
//        this.b = (this.rgb >> 0) & 0xFF;
//    }
//
//    public int getRGB() {
//        return rgb;
//    }
//
//    public int getRed() {
//        return r;
//    }
//
//    public int getGreen() {
//        return g;
//    }
//
//    public int getBlue() {
//        return b;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        PColor po = (PColor) obj;
//        if (po.getRGB() == this.getRGB()) {
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 3;
//        hash = 53 * hash + this.r;
//        hash = 53 * hash + this.g;
//        hash = 53 * hash + this.b;
//        hash = 53 * hash + this.rgb;
//        return hash;
//    }
//
//    @Override
//    public String toString() {
//        return "PColor[" + r + "," + g + "," + b + "]";
//    }
//
//
//}
