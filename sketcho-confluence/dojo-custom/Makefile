minify-libs:
	uglifyjs dojo.js.uncompressed.js \
			-o dojo.min.js \
			-c -m

move:
	mv dojo.min.js ../src/main/resources/js/dojo.min.js

clean:
	-rm dojo.min.js
	-rm src/main/resources/js/dojo.min.js

all: clean minify-libs move

help:
	@echo Dropbox contains original source of building dojo.js.uncompressed.js