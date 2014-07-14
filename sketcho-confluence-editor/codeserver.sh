GWT=/Users/saikitanabe/tools/gwt-2.6.1
SKETCHO=/Users/saikitanabe/Documents/sources/sketcho
WORK_DIR=/Users/saikitanabe/sketchboard-confluence-gwt
RESOURCES=($SKETCHO/sd-domain/src/main/resources:$SKETCHO/hibernate4gwt-domain/src/main/resources:$SKETCHO/sd-app-frame/src/main/resources:$SKETCHO/sd-editor/src/main/resources)
SOURCES=($SKETCHO/sd-domain/src/main/java:$SKETCHO/hibernate4gwt-domain/src/main/java:$SKETCHO/sd-app-frame/src/main/java:$SKETCHO/sd-editor/src/main/java)
MEM="-Xmx1024M -XX:MaxPermSize=256M"

#echo "RESOURCES: ${RESOURCES[*]}"
#echo "SOURCES: ${SOURCES[*]}"

#PARAMS="-Xmx1024M -cp $GWT/gwt-codeserver.jar:$GWT/gwt-dev.jar:$GWT/gwt-user.jar:${RESOURCES[*]}:${SOURCES[*]}
#com.google.gwt.dev.codeserver.CodeServer -bindAddress $GWT_BIND_ADDRESS -workDir $WORK_DIR -src
#/Users/saikitanabe/Documents/sources/sketcho/sketcho-confluence-editor/src/main/java
#net.sevenscales.sketchoconfluenceapp.Sketcho_confluence_app"

PARAMS="$MEM -cp $GWT/gwt-codeserver.jar:$GWT/gwt-dev.jar:$GWT/gwt-user.jar:${RESOURCES[*]}:${SOURCES[*]}
com.google.gwt.dev.codeserver.CodeServer $BIND_ADDRESS -workDir $WORK_DIR -src
/Users/saikitanabe/Documents/sources/sketcho/sketcho-confluence-editor/src/main/java
net.sevenscales.sketchoconfluenceapp.Sketcho_confluence_app"

echo $PARAMS

java $PARAMS
