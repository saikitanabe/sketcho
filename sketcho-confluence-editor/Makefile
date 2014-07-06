dev: clean
	cp src/main/java/net/sevenscales/sketchoconfluenceapp/Sketcho_confluence_app.gwt-dev.xml \
	   src/main/java/net/sevenscales/sketchoconfluenceapp/Sketcho_confluence_app.gwt.xml
	ant dev
	./codeserver.sh 

# NOTE enable 1 compile permutation
dev-ie: clean
	cp src/main/java/net/sevenscales/sketchoconfluenceapp/Sketcho_confluence_app.gwt-dev.xml \
	   src/main/java/net/sevenscales/sketchoconfluenceapp/Sketcho_confluence_app.gwt.xml
	ant dev

prod: clean
	cp src/main/java/net/sevenscales/sketchoconfluenceapp/Sketcho_confluence_app.gwt-prod.xml \
	   src/main/java/net/sevenscales/sketchoconfluenceapp/Sketcho_confluence_app.gwt.xml
	ant prod

clean:
	rm -rf gwt-unitCache
	ant clean

help:
	@echo use make dev for running codeserver on local machine
	@echo use make dev-ie for creating new compilation for IE only
	@echo there is a bug in super dev mode and correct url is not use, that's why the workaround.