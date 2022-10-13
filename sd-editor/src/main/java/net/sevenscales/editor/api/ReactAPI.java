package net.sevenscales.editor.api;

public class ReactAPI {

  public static native boolean isNav2()/*-{
    return $wnd.isNav2()
  }-*/;

  public static native int getCanvasWidth()/*-{
    return $wnd.getCanvasWidth()
  }-*/;

  public static native int getCanvasHeight()/*-{
    return $wnd.getCanvasHeight()
  }-*/;

  public static native int getLeftPanelWidth()/*-{
    return $wnd.getLeftPanelWidth()
  }-*/;

  /**
   * Let's have a single place where properties are fixed.
   * 
   * @param elementType
   * @param props
   * @return
   */
  public static int fixShapeProperties(
    String elementType,
    Integer props
  ) {

    int _props = 0;
    if (props != null) {
      // Integer needs to be converted to int for javascript api
      _props = props;
    }

    return _fixShapeProperties(elementType, _props);
  }
  public static native int _fixShapeProperties(
    String elementType,
    int props
  )/*-{
    return $wnd.fixShapeProperties(elementType, props)
  }-*/;
}