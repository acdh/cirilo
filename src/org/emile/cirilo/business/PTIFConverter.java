package org.emile.cirilo.business;


import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOMetadata;

import javax.media.jai.*;

import com.sun.media.imageio.plugins.tiff.TIFFImageWriteParam;
import com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriter;
import com.sun.media.jai.codec.TIFFEncodeParam;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;


public class PTIFConverter {

	private static final RenderingHints RH_BORDER_REFLECT = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
            BorderExtender.createInstance(BorderExtender.BORDER_REFLECT));


	
	public static void pyramidGenerator(TIFFImageWriter writer, BufferedImage aInputImage, int tileWidth, int tileHeight) 
	{
	
		try {
			TIFFEncodeParam tep = new TIFFEncodeParam();
			tep.setWriteTiled(true);
			tep.setTileSize(tileWidth,tileHeight);
		    
			TIFFImageWriteParam param = (TIFFImageWriteParam) writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionType("ZLib");
			param.setCompressionQuality(0.7f);

//			param.setCompressionType("JPEG");
//			param.setCompressionType("LZW");
			
			param.setTilingMode(ImageWriteParam.MODE_EXPLICIT);
			param.setTiling(tileWidth, tileHeight, 0, 0);

			RenderedImage renderedImg = aInputImage;
			IIOMetadata metadata = writer.getDefaultImageMetadata(new ImageTypeSpecifier(renderedImg), param);
						
			writer.prepareWriteSequence(metadata);		
			writer.writeToSequence(new IIOImage(renderedImg, null, metadata), param);
			
			writer.endWriteSequence();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}      
  
}

