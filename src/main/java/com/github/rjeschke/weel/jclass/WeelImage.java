/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rjeschke.weel.jclass;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.github.rjeschke.weel.ValueMap;
import com.github.rjeschke.weel.WeelOop;
import com.github.rjeschke.weel.annotations.WeelClass;
import com.github.rjeschke.weel.annotations.WeelMethod;

/**
 * Weel BufferedImage implementation.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
@WeelClass(name = "java.Image", usesOop = true)
public final class WeelImage
{
    private WeelImage()
    {
        // image
    }

    /**
     * Constructor.
     * 
     * @param thiz
     *            This.
     * @param width
     *            The width.
     * @param height
     *            The height.
     */
    @WeelMethod
    public final static void ctor(final ValueMap thiz, final int width,
            final int height)
    {
        WeelOop.setInstance(thiz, new ImageWrapper(width, height, 0));
    }

    /**
     * Constructor.
     * 
     * @param thiz
     *            This.
     * @param width
     *            The width.
     * @param height
     *            The height.
     * @param color
     *            The color to fill the image with.
     */
    @WeelMethod
    public final static void ctor(final ValueMap thiz, final int width,
            final int height, final int color)
    {
        WeelOop.setInstance(thiz, new ImageWrapper(width, height, color));
    }

    /**
     * Sets a pixel with the defined color in 'aarrggbb'
     * 
     * @param thiz
     *            This.
     * @param x
     *            X.
     * @param y
     *            Y.
     * @param color
     *            Color.
     */
    @WeelMethod
    public static final void setPixel(final ValueMap thiz, final int x,
            final int y, final int color)
    {
        final ImageWrapper img = WeelOop.getInstance(thiz, ImageWrapper.class);
        img.setPixel(x, y, color);
    }

    /**
     * Seta a pixel with the define color.
     * 
     * @param thiz
     *            This.
     * @param x
     *            X.
     * @param y
     *            Y.
     * @param r
     *            Red (0 - 255).
     * @param g
     *            Green (0 - 255).
     * @param b
     *            Blue (0 - 255).
     */
    @WeelMethod
    public static final void setPixel(final ValueMap thiz, final int x,
            final int y, final int r, final int g, final int b)
    {
        final ImageWrapper img = WeelOop.getInstance(thiz, ImageWrapper.class);
        img.setPixel(x, y, 0xff000000 | (ImageWrapper.clamp(r, 0, 255) << 16)
                | (ImageWrapper.clamp(g, 0, 255) << 8)
                | ImageWrapper.clamp(b, 0, 255));
    }

    /**
     * Seta a pixel with the define color.
     * 
     * @param thiz
     *            This.
     * @param x
     *            X.
     * @param y
     *            Y.
     * @param a
     *            Alpha (0 - 255).
     * @param r
     *            Red (0 - 255).
     * @param g
     *            Green (0 - 255).
     * @param b
     *            Blue (0 - 255).
     */
    @WeelMethod
    public static final void setPixel(final ValueMap thiz, final int x,
            final int y, final int a, final int r, final int g, final int b)
    {
        final ImageWrapper img = WeelOop.getInstance(thiz, ImageWrapper.class);
        img.setPixel(x, y, (ImageWrapper.clamp(a, 0, 255) << 24)
                | (ImageWrapper.clamp(r, 0, 255) << 16)
                | (ImageWrapper.clamp(g, 0, 255) << 8)
                | ImageWrapper.clamp(b, 0, 255));
    }

    /**
     * Gets a pixel.
     * 
     * @param thiz
     *            This.
     * @param x
     *            X.
     * @param y
     *            Y.
     * @return The pixel/color.
     */
    @WeelMethod
    public static final int getPixel(final ValueMap thiz, final int x,
            final int y)
    {
        final ImageWrapper img = WeelOop.getInstance(thiz, ImageWrapper.class);
        return img.getPixel(x, y);
    }

    /**
     * Draws a line.
     * 
     * @param thiz
     *            Thiz.
     * @param x1
     *            Start X.
     * @param y1
     *            Start Y.
     * @param x2
     *            End X.
     * @param y2
     *            End Y.
     * @param color
     *            The color.
     */
    @WeelMethod
    public static final void drawLine(final ValueMap thiz, final int x1,
            final int y1, final int x2, final int y2, final int color)
    {
        final ImageWrapper img = WeelOop.getInstance(thiz, ImageWrapper.class);
        img.drawLine(x1, y1, x2, y2, color);
    }

    /**
     * Draws a line.
     * 
     * @param thiz
     *            Thiz.
     * @param x1
     *            Start X.
     * @param y1
     *            Start Y.
     * @param x2
     *            End X.
     * @param y2
     *            End Y.
     * @param a
     *            Alpha (0 - 255).
     * @param r
     *            Red (0 - 255).
     * @param g
     *            Green (0 - 255).
     * @param b
     *            Blue (0 - 255).
     */
    @WeelMethod
    public static final void drawLine(final ValueMap thiz, final int x1,
            final int y1, final int x2, final int y2, final int a, final int r,
            final int g, final int b)
    {
        final ImageWrapper img = WeelOop.getInstance(thiz, ImageWrapper.class);
        img.drawLine(x1, y1, x2, y2, (ImageWrapper.clamp(a, 0, 255) << 24)
                | (ImageWrapper.clamp(r, 0, 255) << 16)
                | (ImageWrapper.clamp(g, 0, 255) << 8)
                | ImageWrapper.clamp(b, 0, 255));
    }

    /**
     * Saves this image as a png.
     * 
     * @param thiz
     *            Thiz.
     * @param filename
     *            The filename.
     * @return <code>true</code> on success.
     */
    @WeelMethod
    public static final boolean toPng(final ValueMap thiz, final String filename)
    {
        final ImageWrapper img = WeelOop.getInstance(thiz, ImageWrapper.class);
        return img.toPng(filename);
    }

    /**
     * BufferedImage wrapper.
     * 
     * @author René Jeschke <rene_jeschke@yahoo.de>
     */
    private final static class ImageWrapper
    {
        /** The image. */
        private final BufferedImage image;
        /** The width. */
        private final int width;
        /** The height. */
        private final int height;
        /** The pixel data. */
        private final int[] pixels;
        /** The Graphics2D. */
        private Graphics2D graphics;

        /**
         * Constructor.
         * 
         * @param width
         *            The width.
         * @param height
         *            The height.
         */
        public ImageWrapper(final int width, final int height, final int color)
        {
            this.image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);
            this.graphics = this.image.createGraphics();
            this.width = width;
            this.height = height;
            this.pixels = ((DataBufferInt) this.image.getRaster()
                    .getDataBuffer()).getData();
            Arrays.fill(this.pixels, color);
        }

        /**
         * Clamp.
         * 
         * @param x
         *            X.
         * @param min
         *            Min.
         * @param max
         *            Max.
         * @return <code>x &lt; min ? min : x > max ? max : x;</code>
         */
        public final static int clamp(final int x, final int min, final int max)
        {
            return x < min ? min : x > max ? max : x;
        }

        /**
         * Sets a pixel.
         * 
         * @param x
         *            X.
         * @param y
         *            Y.
         * @param color
         *            Color.
         */
        public void setPixel(final int x, final int y, final int color)
        {
            this.pixels[clamp(x, 0, this.width - 1)
                    + clamp(y, 0, this.height - 1) * this.width] = color;
        }

        /**
         * Gets a pixel.
         * 
         * @param x
         *            X.
         * @param y
         *            Y.
         * @return The color.
         */
        public int getPixel(final int x, final int y)
        {
            return this.pixels[clamp(x, 0, this.width - 1)
                    + clamp(y, 0, this.height - 1) * this.width];
        }

        /**
         * Draws a line.
         * 
         * @param x1
         *            Start X.
         * @param y1
         *            Start Y.
         * @param x2
         *            End X.
         * @param y2
         *            End Y.
         * @param color
         *            The color.
         */
        public void drawLine(final int x1, final int y1, final int x2,
                final int y2, final int color)
        {
            this.graphics.setColor(new Color(color, true));
            this.graphics.drawLine(x1, y1, x2, y2);
        }

        /**
         * Saves this image as a png.
         * 
         * @param filename
         *            The filename.
         * @return <code>true</code> on success.
         */
        public boolean toPng(final String filename)
        {
            try
            {
                ImageIO.write(this.image, "png", new File(filename));
                return true;
            }
            catch (IOException e)
            {
                return false;
            }
        }
    }
}
