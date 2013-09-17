# rememeber to update pom.xml version number!!!
# make tag VERSION="conf-2.3.1" TAG="Freehand element improvements"
tag:
	git tag -a $(VERSION) -m"$(TAG)"
	git push origin --tags

build:
	make -C ../sketcho-confluence-editor prod
	atlas-mvn clean package