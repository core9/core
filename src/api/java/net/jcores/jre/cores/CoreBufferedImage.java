/*
 * CoreFile.java
 * 
 * Copyright (c) 2010, Ralf Biedert All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the author nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package net.jcores.jre.cores;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.jcores.jre.CommonCore;
import net.jcores.jre.interfaces.functions.F1;
import net.jcores.jre.options.MessageType;
import net.jcores.jre.options.Option;
import net.jcores.jre.utils.internal.Options;

/**
 * Helper functions like <code>scale()</code> or <code>copy()</code> for 
 * {@link BufferedImage} objects. For example, to copy a buffered image you would write:<br/><br/>
 * 
 * <code>BufferedImage bi = $(image).copy().get(0)</code>
 * 
 * @author Ralf Biedert
 * 
 * @since 1.0
 */
public class CoreBufferedImage extends CoreObject<BufferedImage> {

    /** Used for serialization */
    private static final long serialVersionUID = -9069210371713045372L;

    /**
     * Creates an BufferedImage core.
     * 
     * @param supercore The common core.
     * @param objects The BufferedImage to wrap.
     */
    public CoreBufferedImage(CommonCore supercore, BufferedImage... objects) {
        super(supercore, objects);
    }

    /**
     * Copies the buffered images, creating deep clones of the given image data. Altering
     * a copy will not alter the source image.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(image).copy().get(0)</code> - Returns a copy of the passed image.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @return A CoreBufferedImage containing the copies.
     */
    public CoreBufferedImage copy() {
        return new CoreBufferedImage(this.commonCore, map(new F1<BufferedImage, BufferedImage>() {
            public BufferedImage f(final BufferedImage bi) {
                // Code shamelessly stolen from stackoverflow.com
                ColorModel cm = bi.getColorModel();
                boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
                WritableRaster raster = bi.copyData(null);
                return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
            }
        }).array(BufferedImage.class));
    }

    /**
     * Scales all contained images by the given factor.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(image).scale(2.0).get(0)</code> - Scales the image to twice its size.</li>
     * <li><code>$(image).scale(0.5).get(0)</code> - Scales the image to half its size.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @param factor The factor by which to scale the image.
     * 
     * @return A CoreBufferedImage containing the scaled images.
     */
    public CoreBufferedImage scale(final float factor) {
        return new CoreBufferedImage(this.commonCore, map(new F1<BufferedImage, BufferedImage>() {
            public BufferedImage f(final BufferedImage bi) {
                // Code shamelessly stolen from stackoverflow.com
                final AffineTransform af = new AffineTransform();
                af.scale(factor, factor);
                final AffineTransformOp operation = new AffineTransformOp(af, AffineTransformOp.TYPE_BILINEAR);
                return operation.filter(bi, null);
            }
        }).array(BufferedImage.class));
    }
    

    /**
     * Scales all contained images to the given dimensions.<br/>
     * <br/>
     * 
     * 
     * Examples:
     * <ul>
     * <li><code>$(image).scale(640, 480).get(0)</code> - Scales the image to the given size, ignoring the original aspect ratio.</li>
     * <li><code>$(image).scale(1024, 0).get(0)</code> - Scales the image to the given width, keeping the aspect ratio.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @param width The new width of all images. If <code>0</code> or negative, only height 
     * will be used and the aspect ratio will be kept. 
     * @param height The new height of all images. If <code>0</code> or negative, only width 
     * will be used and the aspect ratio will be kept. 
     * 
     * @return A CoreBufferedImage containing the scaled images.
     */
    public CoreBufferedImage scale(final int width, final int height) {
        if(width == 0 && height == 0) return this;
                
        return new CoreBufferedImage(this.commonCore, map(new F1<BufferedImage, BufferedImage>() {
            public BufferedImage f(final BufferedImage bi) {
                final AffineTransform af = new AffineTransform();

                // Compute new width and height for this image
                float w = bi.getWidth();
                float h = bi.getHeight();
                
                if(width <= 0) {
                    h = height;
                    w = ((height / h) * w);
                }

                if(height <= 0) {
                    h = ((width/ w) * h);
                    w = width;
                }
                
                if(width > 0 && height > 0) {
                    h = height;
                    w = width;
                }

                // And scale
                af.scale(w / bi.getWidth(), h / bi.getHeight());
                final AffineTransformOp operation = new AffineTransformOp(af, AffineTransformOp.TYPE_BILINEAR);
                return operation.filter(bi, null);
            }
        }).array(BufferedImage.class));
    }
    

    /**
     * Writes the enclosed image at position <code>0</code> to the given file. The file type will be
     * recognized by the suffix.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(image).scale(16, 0).write("output.png")</code> - Creates a thumbnail of the given image and writes it to the file <code>output.png</code>.</li>
     * </ul>
     * 
     * Single-threaded, size-of-one.<br/>
     * <br/>
     * 
     * @param file The file to write the image get(0) to.
     * @param options The default options.
     * @return This CoreBufferedImage object.
     */
    public CoreBufferedImage write(String file, final Option... options) {
        if (file == null) return this;

        if (size() != 1) {
            this.commonCore.report(MessageType.MISUSE, "CoreBufferedImage.write() needs exactly one image to write, at the moment.");
        }

        String type = "png";
        type = file.toLowerCase().endsWith(".gif") ? "GIF" : type;
        type = file.toLowerCase().endsWith(".jpg") ? "JPG" : type;
        type = file.toLowerCase().endsWith(".jpeg") ? "JPG" : type;
        type = file.toLowerCase().endsWith(".bmp") ? "BMP" : type;
        type = file.toLowerCase().endsWith(".wbmp") ? "WBMP" : type;

        try {
            ImageIO.write(get(0), type, new File(file));
        } catch (IOException e) {
            Options.$(this.commonCore, options).failure(file, e, "write:ioerror", "Unable to write the image to the given file.");
        }

        return this;
    }
}
