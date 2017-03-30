# RallyMe

[Demo PowerPoint](https://docs.google.com/presentation/d/1yf63zFsCgkxlgnwspXXayt-cxiavDVkgJ8bUzoWhSWM/edit#slide=id.g1dad1e7c08_0_229)

[Google Doc outline](https://docs.google.com/document/d/1RRb0AQKTRRRKakI_R6DX48wyuSwtJcjpE3VdAM7Qweg/edit)

## Committing your code

To commit your code to our GitHub repo, you'll need to install [Git](https://git-scm.com/).

If you don't already know how to use Git, GitHub has a neat and concise 
tutorial called [Try Git](https://try.github.io/). It's very hands-on and it 
only takes a few minutes to complete.

## Configuring the dev environment

To build RallyMe, you'll need to install [Apache Maven](https://maven.apache.org/) 
and the [WildFly application server](http://wildfly.org/).

Please read the [Maven install guide](https://maven.apache.org/install.html)! 
Otherwise, the commands below may not work.

## Building RallyMe

### Deploying directly to WildFly (for development)

You can rebuild and deploy RallyMe directly to your running WildFly server.

You'll need to add a user to WildFly's Management Realm by going to the WildFly 
folder and running "add-user.sh" in the bin folder (or "add-user.bat" if you're 
on Windows.) Enter "rallyme" for both the username and password. Type "no" for 
the final question about slave configurations. Finally, if WildFly is already 
running, restart it.

After you've added that user, make sure WildFly is running and run this command 
in the terminal:

    mvn wildfly:deploy

RallyMe will be deployed at http://localhost:8080/rallyme/.

### Building a WAR archive (for final testing)

To build the WAR file, start by opening a terminal in the rallyme folder.  
(On Windows, open the folder in Windows Explorer, then hold Shift and right-click 
in the folder; you'll see an Open in Command Prompt option in the menu.)

Run this command in the terminal:

    mvn package

The resulting WAR file will be in the *target* folder, with the filename
"RallyMe-x.y.z-SNAPSHOT.jar".

To deploy this on WildFly, copy the folder to WildFly's standalone/deployments 
folder.
