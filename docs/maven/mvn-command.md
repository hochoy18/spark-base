
## 依赖树
mvn dependency:tree  -Dverbose 
mvn dependency:tree  

## 源码和Java docs下载
mvn dependency:sources
mvn dependency:sources -DdownloadSources=true -DdownloadJavadocs=true

## package
mvn pacakge  -DskipTests
