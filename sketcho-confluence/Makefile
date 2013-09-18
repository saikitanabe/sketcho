help:
	@echo 1. pom.xml correct version number
	@echo 2. make tag VERSION="conf-2.3.1" TAG="Freehand element improvements"
	@echo 3. make build
	@echo 4. Test on Confluence 4.0 and Confluence 5.x (newest)
	@echo 5. Release

# rememeber to update pom.xml version number!!!
# make tag VERSION="conf-2.3.1" TAG="Freehand element improvements"
tag:
	git tag -a $(VERSION) -m"$(TAG)"
	git push origin --tags

build:
	make -C ../sketcho-confluence-editor prod
	atlas-mvn clean package