/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.chua.image.support.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;

/**
 * 粉碎滤镜
 *
 * @author Administrator
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImageShatterFilter extends AbstractImageFilter {
    private float centreX = 0.5f, centreY = 0.5f;
    private float distance;
    private float transition;
    private float rotation;
    private float zoom;
    private float startAlpha = 1;
    private float endAlpha = 1;
    private int iterations = 5;
    private int tile;

    public ImageShatterFilter() {
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (dst == null) {
            dst = createCompatibleDestImage(src, null);
        }
        float width = (float) src.getWidth();
        float height = (float) src.getHeight();
        float cx = (float) src.getWidth() * centreX;
        float cy = (float) src.getHeight() * centreY;
        float imageRadius = (float) Math.sqrt(cx * cx + cy * cy);

        int numTiles = iterations * iterations;
        Tile[] shapes = new Tile[numTiles];
        float[] rx = new float[numTiles];
        float[] ry = new float[numTiles];
        float[] rz = new float[numTiles];

        Graphics2D g = dst.createGraphics();

        SecureRandom random = new SecureRandom();
        float lastx = 0, lasty = 0;
        for (int y = 0; y < iterations; y++) {
            int y1 = (int) height * y / iterations;
            int y2 = (int) height * (y + 1) / iterations;
            for (int x = 0; x < iterations; x++) {
                int i = y * iterations + x;
                int x1 = (int) width * x / iterations;
                int x2 = (int) width * (x + 1) / iterations;
                rx[i] = tile * random.nextFloat();
                ry[i] = tile * random.nextFloat();
                rx[i] = 0;
                ry[i] = 0;
                rz[i] = tile * (2 * random.nextFloat() - 1);
                Shape p = new Rectangle(x1, y1, x2 - x1, y2 - y1);
                shapes[i] = new Tile();
                shapes[i].shape = p;
                shapes[i].x = (x1 + x2) * 0.5f;
                shapes[i].y = (y1 + y2) * 0.5f;
                shapes[i].vx = width - (cx - x);
                shapes[i].vy = height - (cy - y);
                shapes[i].w = x2 - x1;
                shapes[i].h = y2 - y1;
            }
        }

        for (int i = 0; i < numTiles; i++) {
            float h = (float) i / numTiles;
            double angle = h * 2 * Math.PI;
            float x = transition * width * (float) Math.cos(angle);
            float y = transition * height * (float) Math.sin(angle);

            Tile tile = shapes[i];
            Rectangle r = tile.shape.getBounds();
            AffineTransform t = g.getTransform();
            x = tile.x + transition * tile.vx;
            y = tile.y + transition * tile.vy;
            g.translate(x, y);
            g.rotate(transition * rz[i]);
            g.setColor(Color.getHSBColor(h, 1, 1));
            Shape clip = g.getClip();
            g.clip(tile.shape);
            g.drawImage(src, 0, 0, null);
            g.setClip(clip);
            g.setTransform(t);
        }

        g.dispose();
        return dst;
    }

    public Point2D getCentre() {
        return new Point2D.Float(centreX, centreY);
    }

    public void setCentre(Point2D centre) {
        this.centreX = (float) centre.getX();
        this.centreY = (float) centre.getY();
    }

    @Override
    public String toString() {
        return "Transition/Shatter...";
    }

    static class Tile {
        float x, y, vx, vy, w, h;
        float rotation;
        Shape shape;
    }
}
