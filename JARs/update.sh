#!/bin/bash

# Step 1
mvn -f ../WhatsappSeguro/pom.xml clean install

# Step 2
mvn -f ../WhatsappServer/pom.xml clean install

# Step 4
cp ../WhatsappSeguro/target/WhatsappSeguro-1.0-SNAPSHOT-shaded.jar ClientWhatsUP.jar

# Step 5
cp ../WhatsappServer/target/WhatsappServer-1.0-SNAPSHOT-shaded.jar ServerWhatsUP.jar


