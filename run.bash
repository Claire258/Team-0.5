#!/bin/bash

# Navigate to the src directory
cd src || exit

# Set the classpath to include the PostgreSQL JDBC driver from the lib folder
CLASSPATH=".:../lib/postgresql-42.7.4.jar"

# Compile all Java files
javac -cp $CLASSPATH view/*.java model/*.java database/*.java controller/*.java network/*.java Main.java

# Run the main program
java -cp $CLASSPATH Main

# Navigate back to the root directory
cd ..
