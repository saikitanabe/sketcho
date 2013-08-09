package net.sevenscales.server.image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import com.thoughtworks.xstream.XStream;


public class SvgUtil {
//  private static String svgString = "<?xml version='1.0' encoding='UTF-8'?><svg xmlns:xlink='http://www.w3.org/1999/xlink' xmlns='http://www.w3.org/2000/svg'  width='700' height='500'><polyline points='280,145 320,215 480,195' style='fill:none;stroke:black;stroke-width:1;stroke-dasharray:none;'/><polyline points='469,201 480,195 469,191' style='fill:none;stroke:black;stroke-width:1;stroke-dasharray:none;'/><text x='464' y='185' style='font-weight:bold; font-size: 12px; text-anchor: start; font-family: Arial;'></text><text x='320' y='203' style='font-weight:bold; font-size: 12px; text-anchor: start; font-family: Arial;'></text><text x='304' y='166' style='font-weight:bold; font-size: 12px; text-anchor: start; font-family: Arial;'></text><ellipse cx='280' cy='120' rx='50' ry='25' style='fill:rgb(240,240,202);stroke:rgb(0,0,0);stroke-width:1'/><text x='280' y='123' style='font-weight:bold; font-size: 12px; text-anchor: middle; font-family: Arial;'>Ellipse</text><ellipse cx='530' cy='195' rx='50' ry='25' style='fill:rgb(240,240,202);stroke:rgb(0,0,0);stroke-width:1'/><text x='530' y='198' style='font-weight:bold; font-size: 12px; text-anchor: middle; font-family: Arial;'>Ellipse</text></svg>";

  public static File createPng(String svg, String name) throws IOException {
    // Create a PNG transcoder
    PNGTranscoder transcoder = new PNGTranscoder();
    
    svg = svg.replaceAll("&", "&amp;");

    // Create the transcoder input
//    String svgInputURI = ...;
//    File insvg = new File("svghops.svg");
//    FileInputStream fis = new FileInputStream(insvg);
    ByteArrayInputStream fis = new ByteArrayInputStream(svg.getBytes("UTF-8"));

    TranscoderInput input = new TranscoderInput(fis);
//    TranscoderInput input = new TranscoderInput("file://snaphops.svg");

    // Create the transcoder output
    File png = new File("./tmp/"+name+".png");
    FileOutputStream fos = new FileOutputStream(png);
//    OutputStream ostream = ...;
    TranscoderOutput output = new TranscoderOutput(fos);

    // Transform the svg document into a PNG image
    try {
      transcoder.transcode(input, output);
    } catch (TranscoderException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // Flush and close the stream
    fos.flush();
    fos.close();
    
    return png;
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
	    		System.out.println(png);
	    		png.delete();
	    	}
	    }
	}
}
