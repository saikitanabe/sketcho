package net.sevenscales.sketchoconfluenceapp.server.utils;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SvgUtil {
//	private static final Logger log = LoggerFactory.getLogger(SvgUtil.class);

//  private static String svgString = "<?xml version='1.0' encoding='UTF-8'?><svg xmlns:xlink='http://www.w3.org/1999/xlink' xmlns='http://www.w3.org/2000/svg'  width='700' height='500'><polyline points='280,145 320,215 480,195' style='fill:none;stroke:black;stroke-width:1;stroke-dasharray:none;'/><polyline points='469,201 480,195 469,191' style='fill:none;stroke:black;stroke-width:1;stroke-dasharray:none;'/><text x='464' y='185' style='font-weight:bold; font-size: 12px; text-anchor: start; font-family: Arial;'></text><text x='320' y='203' style='font-weight:bold; font-size: 12px; text-anchor: start; font-family: Arial;'></text><text x='304' y='166' style='font-weight:bold; font-size: 12px; text-anchor: start; font-family: Arial;'></text><ellipse cx='280' cy='120' rx='50' ry='25' style='fill:rgb(240,240,202);stroke:rgb(0,0,0);stroke-width:1'/><text x='280' y='123' style='font-weight:bold; font-size: 12px; text-anchor: middle; font-family: Arial;'>Ellipse</text><ellipse cx='530' cy='195' rx='50' ry='25' style='fill:rgb(240,240,202);stroke:rgb(0,0,0);stroke-width:1'/><text x='530' y='198' style='font-weight:bold; font-size: 12px; text-anchor: middle; font-family: Arial;'>Ellipse</text></svg>";
	
	public static String FONT_FAMILY;
	private static final String[] CANDIDATES = new String[]{"helvetica", "arial", "arialmt", "arial mt", "liberationsans", "sans-serif", "nimbus sans l"};

	static {
		Map<String, String> candis = fontFamilyCandidates();
		FONT_FAMILY = selectOne(candis);
//		log.debug("FONT_FAMILY: " + FONT_FAMILY);
	}
	
	private static Map<String, String> fontFamilyCandidates() {
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
    Font[] fonts = e.getAllFonts(); // Get the fonts
    Map<String, String> result = new HashMap<String, String>();
    for (Font f : fonts) {
//    	log.debug("Font: {}", f.getFontName());
    	for (String c : CANDIDATES) {
    		if (c.equals(f.getFontName().toLowerCase())) {
    			result.put(c, f.getFontName());
    		}
    	}
    }
    return result;
	}

  private static String selectOne(Map<String, String> candis) {
  	for (String c : CANDIDATES) {
  		if (candis.containsKey(c)) {
  			return candis.get(c);
  		}
  	}
		return "";
	}

	public static byte[] createPng(String svg, String name) {
    // Create a PNG transcoder
    PNGTranscoder transcoder = new PNGTranscoder();
    TranscodingHints hints = transcoder.getTranscodingHints();
    hints.put(PNGTranscoder.KEY_DEFAULT_FONT_FAMILY, "Arial, Helvetica, sans-serif");
    transcoder.setTranscodingHints(hints);
    
//    transcoder.addTranscodingHint(PNGTranscoder.KEY_DEFAULT_FONT_FAMILY, "sans-serif, Helvetica, Arial");

    // Create the transcoder input
//    String svgInputURI = ...;
//    File insvg = new File("svghops.svg");
//    FileInputStream fis = new FileInputStream(insvg);
    ByteArrayInputStream fis;
    ByteArrayOutputStream fos;
    try {
      fis = new ByteArrayInputStream(svg.getBytes("UTF-8"));

    TranscoderInput input = new TranscoderInput(fis);

    fos = new ByteArrayOutputStream();
    // Create the transcoder output
//    File png = new File("./tmp/"+name+".png");
//    FileOutputStream fos = new FileOutputStream(png);
//    OutputStream ostream = ...;
    TranscoderOutput output = new TranscoderOutput(fos);

    // Transform the svg document into a PNG image
    transcoder.transcode(input, output);
      
      // Flush and close the stream
      fos.flush();
      fos.close();

    } catch (UnsupportedEncodingException e1) {
      e1.printStackTrace();
      throw new RuntimeException(e1);
    } catch (TranscoderException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    
    return fos.toByteArray();
  }

	public static void cleanHouse() {
		
		File dir = new File("./tmp");
		FilenameFilter filter = new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	            return name.endsWith(".png");
	        }
	    };
	    File[] pngs = dir.listFiles(filter);
	    Arrays.sort(pngs, new Comparator<File>() {
	    	public int compare(File o1, File o2) {
	    		return new Long(o2.lastModified()).compareTo(o1.lastModified());
	    	}
		});
	    
	    int treshold = 3;
	    int i = 0;
	    for (File png : pngs) {
	    	if (i++ > treshold) {
	    		png.delete();
	    	}
	    }
	}
}
