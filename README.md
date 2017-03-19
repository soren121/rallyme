# RallyMe

some cool shit

## Committing your code

To commit your code to our GitHub repo, you'll need to install [Git](https://git-scm.com/).

If you don't already know how to use Git, GitHub has a neat and concise 
tutorial called [Try Git](https://try.github.io/). It's very hands-on and it 
only takes a few minutes to complete.

We'll be using Git branches and pull requests to commit our code. Here's how
it works:
 
 * The `master` branch will contain our tested & ready code. You won't be able 
   to push directly to the master branch.

 * Instead, you'll need to create your own branch to work on. You can do this 
   by running `git checkout -b <new branch name>` in a terminal.

 * You can (and should) commit & push your changes in your branch often, and 
   always remember to pull new changes before making changes.

 * When your feature is ready to be merged into the master branch, you'll need 
   to create a pull request in GitHub. Here's how to do that:

   1. Go to [our GitHub repository](https://github.com/soren121/rallyme) and 
      click on the Pull Requests tab, then create a new pull request. 
   2. Choose the branch you've been working on.
   3. Give your PR a title and write a short description of your changes.
   4. When you're done, click the Submit pull request button.

 * Someone else in our group will need to review your pull request before it 
   can be merged. They can either approve your PR and merge it, or they 
   can reject it and ask for changes. Don't fret, this happens a lot.

## Configuring the dev environment

To build RallyMe, you'll need to install [Apache Maven](https://maven.apache.org/) 
and a Java application server like [Apache Tomcat](https://tomcat.apache.org/) or 
[WildFly](http://wildfly.org/).

Please read the [Maven install guide](https://maven.apache.org/install.html)! 
Otherwise, the commands below may not work.

## Building RallyMe

To build the WAR file, start by opening a terminal in the rallyme directory.  
(On Windows, open the folder in Windows Explorer, then hold Shift and right-click 
in the folder; you'll see an Open in Command Prompt option in the menu.)

Run this command in the terminal:

    mvn package

The resulting WAR file will be in the *target* folder.
