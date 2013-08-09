package net.sevenscales.editor.uicomponents;


public class AngleUtil2 {

  public static double beta(int x1, int y1, int x2, int y2) {
    boolean uptodown = y1 <= y2 ? true : false;
    boolean lefttoright = x1 <= x2 ? true : false;
    double vectory = y2 - y1;
    double vectorx = x2 - x1;
    double beta = 0;

    if (vectorx != 0) {
       beta = Math.atan( Math.abs(vectory/vectorx) );
      if (!uptodown && lefttoright) {
        beta = Math.toRadians(90) - beta + Math.toRadians(90);
      } else if (uptodown && !lefttoright) {
        beta = Math.toRadians(90) - beta - Math.toRadians(90);
      } else if (uptodown && lefttoright) {
        beta = Math.toRadians(180) + beta;
      }
    } else {
      beta = -Math.PI/2;
      if (!uptodown) {
        beta = Math.PI/2;
      }
    }
    return beta;
  }
  
  public static double slope(int x1, int y1, int x2, int y2) {
    boolean uptodown = y1 <= y2 ? true : false;
    boolean lefttoright = x1 <= x2 ? true : false;
    double vectory = y2 - y1;
    double vectorx = x2 - x1;
    double result = 0;
    if (vectorx != 0) {
      result = Math.atan( vectory/vectorx );
      
  //    System.out.println("result: " + Math.toDegrees(result));
      if (!uptodown && !lefttoright) {
        result -= Math.toRadians(180);
      } else if (uptodown && !lefttoright) {
        result += Math.toRadians(180);
      }
    } else {
      result = -Math.PI/2;
      if (!uptodown) {
        result = Math.PI/2;
      }
    }

    return result;
  }

  public static double slope2(int x1, int y1, int x2, int y2) {
    boolean uptodown = y1 <= y2 ? true : false;
    boolean lefttoright = x1 <= x2 ? true : false;
    double vectory = y2 - y1;
    double vectorx = x2 - x1;

//    double c = Math.sqrt((Math.pow(vectorx, 2)+Math.pow(vectory, 2)));
//    double result = Math.asin(vectory/c);
    double result = Math.atan( vectory/vectorx );
    
    if (!uptodown && !lefttoright) {
      result -= Math.toRadians(180);
    } else if (uptodown && !lefttoright) {
      result += Math.toRadians(180);
    }
    return result;
//    return Math.toRadians(45);
  }

  public static double align(double alpha, double[] angles) {
    int index = 0;
    double smallest = Double.MAX_VALUE;
    int i = 0;
    for (double a : angles) {
      double tmp = Math.abs(alpha - a);
      if (tmp < smallest) {
        index = i;
        smallest = tmp;
      }
      ++i;
    }
//    System.out.println("alpha: "+Math.toDegrees(alpha)+ " angles[index]: " +Math.toDegrees(angles[index]) + " jakojaannos: "+(alpha % angles[index]));
    return angles[index];
  }


}
