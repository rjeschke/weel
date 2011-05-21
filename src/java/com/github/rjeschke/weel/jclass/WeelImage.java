/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel.jclass;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

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
        WeelOop.setInstance(thiz, new ImageWrapper(width, height));
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

        /**
         * Constructor.
         * 
         * @param width
         *            The width.
         * @param height
         *            The height.
         */
        public ImageWrapper(final int width, final int height)
        {
            this.image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);
            this.width = width;
            this.height = height;
            this.pixels = ((DataBufferInt) this.image.getRaster()
                    .getDataBuffer()).getData();
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
