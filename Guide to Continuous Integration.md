# Guide to Continous Integration

- add .travis.yml
- add credentials as env variables  
![](https://github.com/CIShell/CIShell/blob/99699fd0051b2058b11e85da6ba6addcd71a85b9/docs/img/env-variables.png)

## Best practices for developers
- Dont commit directly to develop or master branch. Travis runs on these branches. Create your own feature branch and ask the product owner to merge it with the develop branch.
- Always test your changes locally by running ```mvn clean install``` before pushing the changes to github.
- Add ```[ci skip]``` or ```[skip ci]``` anywhere in the commit message if you want to skip travis build for the commit
