dev: clean
	cp src/main/java/net/sevenscales/sketchoconfluenceapp/Sketcho_confluence_app.gwt-dev.xml \
	   src/main/java/net/sevenscales/sketchoconfluenceapp/Sketcho_confluence_app.gwt.xml
	ant dev
	./codeserver.sh 

prod: clean
	cp src/main/java/net/sevenscales/sketchoconfluenceapp/Sketcho_confluence_app.gwt-prod.xml \
	   src/main/java/net/sevenscales/sketchoconfluenceapp/Sketcho_confluence_app.gwt.xml
	ant prod

clean:
	rm -rf gwt-unitCache
	ant clean
