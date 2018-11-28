# Guide to Continous Integration

- **add .travis.yml to your repository.** Below is a good example to start with. 
```
# specify the programming language
language: java

# specify the jdk version
jdk:
  - oraclejdk8

sudo: false

# cache particular directories between travis builds
cache:
  directories:
    - $HOME/.m2

# specify remote branches to run travis builds on
branches:
  only:
    - master
    - develop

# install dependencies and configure the build system before running primary scripts
install: 
  - cp .travis.settings.xml $HOME/.m2/settings.xml
  - chmod +x jfrog
  - PATH=$TRAVIS_BUILD_DIR:$PATH
  - jfrog rt config "cishell-artifactory" --url "https://cishell.jfrog.io/cishell/" --user $CI_DEPLOY_USERNAME --apikey $CI_DEPLOY_PASSWORD --interactive=false

# run build and deploy scripts
script: 
  - mvn deploy &&
    cd update-site/target/repository &&
    jfrog rt delete "milestones/core-updates/*" --quiet &&
    jfrog rt upload "./*" "milestones/core-updates/" --flat=false
```
- **How to add credentials as travis environmental variables**  
Open repository specific page on travis-ci.com. Click on *More Options* button on the page.  
![](https://github.com/CIShell/CIShell/blob/209c1fdca2d6c6b87353f861ae666b26cfb21499/docs/img/more-options.png)
Enter the names and values of variables under *Environment Variables* and click *Add*.  
![](https://github.com/CIShell/CIShell/blob/ae5b290f8f8b161cb061754eb923c4823c77625b/docs/img/env-variables.png)

## Best practices for developers
- Dont commit directly to develop or master branch. Travis runs on these branches. Create your own feature branch and ask the product owner to merge it with the develop branch.
- Always test your changes locally by running ```mvn clean install``` before pushing the changes to github.
- Add ```[ci skip]``` or ```[skip ci]``` anywhere in the commit message if you want to skip travis build for the commit
