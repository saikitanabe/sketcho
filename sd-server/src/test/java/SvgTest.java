import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;


public class SvgTest {
//  private static String svgString = "<?xml version='1.0' encoding='UTF-8'?><svg xmlns:xlink='http://www.w3.org/1999/xlink' xmlns='http://www.w3.org/2000/svg'  width='700' height='500'><polyline points='280,145 320,215 480,195' style='fill:none;stroke:black;stroke-width:1;stroke-dasharray:none;'/><polyline points='469,201 480,195 469,191' style='fill:none;stroke:black;stroke-width:1;stroke-dasharray:none;'/><text x='464' y='185' style='font-weight:bold; font-size: 12px; text-anchor: start; font-family: Arial;'></text><text x='320' y='203' style='font-weight:bold; font-size: 12px; text-anchor: start; font-family: Arial;'></text><text x='304' y='166' style='font-weight:bold; font-size: 12px; text-anchor: start; font-family: Arial;'></text><ellipse cx='280' cy='120' rx='50' ry='25' style='fill:rgb(240,240,202);stroke:rgb(0,0,0);stroke-width:1'/><text x='280' y='123' style='font-weight:bold; font-size: 12px; text-anchor: middle; font-family: Arial;'>Ellipse</text><ellipse cx='530' cy='195' rx='50' ry='25' style='fill:rgb(240,240,202);stroke:rgb(0,0,0);stroke-width:1'/><text x='530' y='198' style='font-weight:bold; font-size: 12px; text-anchor: middle; font-family: Arial;'>Ellipse</text></svg>";
    private static String svgString = "<?xml version='1.0' encoding='UTF-8'?><svg xlink='http://www.w3.org/1999/xlink' xmlns='http://www.w3.org/2000/svg' width='700' height='500' id='__svg__random___0'><circle cx='0' cy='0' r='0' stroke-width='1.0' visibility='hidden'></circle><text font-family='Arial' x='225' y='89'></text><text font-family='Arial' x='210' y='82'></text><text font-family='Arial' x='255' y='83'></text><polyline points='0,0 0,0 0,0 0,0' stroke='black' fill='white' visibility='hidden'></polyline><polyline points='259,101 270,95 259,89' stroke='black' visibility='visible'></polyline><polyline points='0,0 0,0 0,0 0,0 0,0' stroke='black' fill='white' visibility='hidden'></polyline><polyline points='180,95 270,95' stroke='red' style='Solid'></polyline><polyline points='180,89 270,89 270,101 180,101 180,89' stroke='red' fill='white' visibility='hidden'></polyline><circle cx='0' cy='0' r='0' stroke-width='1.0' visibility='hidden'></circle><circle cx='180' cy='95' r='10' fill='white' stroke='black' visibility='hidden'></circle><circle cx='0' cy='0' r='0' stroke-width='1.0' visibility='hidden'></circle><circle cx='225' cy='95' r='5' fill='white' stroke='black' visibility='hidden'></circle><circle cx='0' cy='0' r='0' stroke-width='1.0' visibility='hidden'></circle><circle cx='270' cy='95' r='10' fill='white' stroke='black' visibility='hidden'></circle></svg>";
  public static void main(String[] args) throws IOException {
    // Create a PNG transcoder
    PNGTranscoder transcoder = new PNGTranscoder();

    // Create the transcoder input
//    String svgInputURI = ...;
//    File insvg = new File("svghops.svg");
//    FileInputStream fis = new FileInputStream(insvg);
    ByteArrayInputStream fis = new ByteArrayInputStream(svgString.getBytes("UTF-8"));

    TranscoderInput input = new TranscoderInput(fis);
//    TranscoderInput input = new TranscoderInput("file://snaphops.svg");

    // Create the transcoder output
    File png = new File("mycontent.png");
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
  }
}
