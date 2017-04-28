# RallyMe

CSCI 4300 Final Project
CRN 41126, Group 5

Authors: Nicholas Narsing, Michael Melatti, Sarah Ahmed, Jinghuan Chen
Date:    04/27/2017

## General information --------------------------------------------------------

Our project was designed for modern browsers. Recent versions of Chrome, 
Firefox, Edge, Safari, and Opera should all work. Internet Explorer probably 
doesn't work.

The website login information for the RallyMe admin user is:  
Username: admin  
Password: rallyme  

We used the Model-View-Controller paradigm in our project:

 * Controller classes can be found in "src/main/java/rallyme/controller".
 * Model classes can be found in "src/main/java/rallyme/model".
 * View templates can be found in "src/main/webapp/WEB-INF/templates".

## Configuring the MySQL database ---------------------------------------------

Create a new database on your MySQL server called "rallyme".

Then, import the included SQL dump, "database.sql", into that database.

A "rallyme" user will be automatically created and granted permissions to
the rallyme database. If your MySQL server is available at localhost:3306, 
then you will not need to make any configuration changes.

To change the database settings, open the "rallyme.core.Database" class.


## Deploying RallyMe ----------------------------------------------------------

For your convenience, a pre-built WAR file, "rallyme.war", is included in the 
root directory. Deploy it to your Java servlet container to install.

RallyMe should be deployed at: http://localhost:8080/rallyme/


## Building RallyMe from source -----------------------------------------------

To build RallyMe, you'll need to install:

 * Apache Maven                 https://maven.apache.org/
 * WildFly application server   http://wildfly.org/

Please read the Maven install guide: https://maven.apache.org/install.html  
Otherwise, the commands below may not work.


### Deploying directly to WildFly (for development) 

You can rebuild and deploy RallyMe directly to your running WildFly server.

Make sure WildFly is running and run this command in the terminal:

    mvn wildfly:deploy

RallyMe will be deployed at: http://localhost:8080/rallyme/


### Building a WAR archive (for final testing) 

To build the WAR file, start by opening a terminal in the rallyme folder.

Run this command in the terminal:

    mvn package

The resulting WAR file will be in the 'target' folder, with the filename
"RallyMe-x.y.z-SNAPSHOT.war".

To deploy this on WildFly, rename the WAR file to "rallyme.war", and copy it 
to WildFly's standalone/deployments folder. 

Assuming default settings, RallyMe should be deployed at: 
http://localhost:8080/rallyme/
