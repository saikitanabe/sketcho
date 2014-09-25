OK_COLOR=\033[32m
RESET=\033[0m

tags: 
	git tag -l -n1

help: tags
	@echo
	@echo "$(OK_COLOR)"'BUILD STEPS'"$(RESET)"
	@echo 1. pom.xml UPDATE to correct version number
	@echo '2. make tag VERSION="conf-2.3.1" TAG="Freehand element improvements"'
	@echo 3. make build
	@echo '4. Test on Confluence 4.0 and Confluence 5.x (newest)'
	@echo 5. Release
	@echo DEV
	@echo ---
	@echo atlas-clean
	@echo atlas-run
	@echo http://localhost:1990/confluence
	@echo Get Plugin Timebomb License
	@echo https://developer.atlassian.com/display/MARKET/Timebomb+Licenses+for+Testing
	@echo Update plugin
	@echo atlas-cli
	@echo atlas-pi

tag:
	git tag -a $(VERSION) -m"$(TAG)"
	git push origin --tags
	git-graph

build:
	make -C dojo-custom all
	make -C ../sketcho-confluence-editor prod
	make -C ../../marked-token prod
	make -C ../../paper.js-custom copy-min
	atlas-mvn clean package

run:
	atlas-run