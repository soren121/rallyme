# RallyMe

some cool shit

## Committing your code

To commit your code to our GitHub repo, you'll need to install [Git](https://git-scm.com/).

If you don't already know how to use Git, GitHub has a neat and concise 
tutorial called [Try Git](https://try.github.io/). It's very hands-on and it 
only takes a few minutes to complete.

## Configuring the dev environment

To build RallyMe, you'll need to install [Apache Maven](https://maven.apache.org/) 
and a Java application server like [Apache Tomcat](https://tomcat.apache.org/) or 
[WildFly](http://wildfly.org/).

Please read the [Maven install guide](https://maven.apache.org/install.html)! 
Otherwise, the commands below may not work.

## Building RallyMe

### Building a WAR archive

To build the WAR file, start by opening a terminal in the rallyme folder.  
(On Windows, open the folder in Windows Explorer, then hold Shift and right-click 
in the folder; you'll see an Open in Command Prompt option in the menu.)

Run this command in the terminal:

    mvn package

The resulting WAR file will be in the *target* folder, with the filename
"RallyMe-x.y.z-SNAPSHOT.jar".

To deploy this on WildFly, copy the folder to WildFly's standalone/deployments 
folder.

### Building an exploded WAR

Exploded WAR folders are helpful for development, since you don't have to wait 
for the WAR to be extracted and deployed every time; you can just replace the 
files that changed instead.

To build an exploded WAR, run this in the terminal:

    mvn compile war:exploded

The exploded WAR folder will be in the *target* folder, with the name 
"RallyMe-x.y.z-SNAPSHOT".

To deploy this on WildFly, copy the folder to WildFly's standalone/deployments 
folder.
