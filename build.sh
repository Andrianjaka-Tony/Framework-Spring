#!/bin/bash.

# se mettre dans la repertoire des classes
cd /opt/tomcat/webapps/framework/WEB-INF/classes
# construire le fichier jar
jar -cvf ../etu1765.jar .
# copie du jar dans le projet de test
cd ../
cp etu1765.jar /opt/tomcat/webapps/ProjetDeTest/WEB-INF/lib/