weblogic-javaee-servlet: Windup Rule That Detects WebLogic Proprietary Servlet Annotations
=============================================================================================
Author: Ondra Zizka  
Level: Beginner  
Technologies: 
Summary: Windup rule that reports on WebLogic servlet annotations  
Target Product: Windup  
Product Versions: 2.0  
Source: <https://github.com/windup/windup-quickstarts/>  

What is it?
-----------

WebLogic provides its own proprietary servlet and filter annotations for dependency injection. If the application uses them, they must be replaced with the standard Java EE 6 annotations. This example demonstrates how to create a Windup rule that searches for these proprietary annotations and reports on them.

The rule searches for the following annotations:

* *@WLServlet*: This is the equivalent of the Java EE 6 *@WebServlet* annotation.

* *@WLFilter*: This is the equivalent of the Java EE 6 *@WebFilter* annotation.

* *@WLInitParam*: This is the equivalent of the Java EE 6 *@WebInitParam* annotation.


Review the Quickstart Code
-------------------------

The `MyHintsRuleProvider` class extends `WindupRuleProvider` and overrides the following methods:

* `getPhase()`: This method returns `RulePhase.MIGRATION_RULES`.

* `getExecuteAfter()`: Nothing executes after this, so this method returns an empty list.

* `getConfiguration(GraphContext context)`: This method looks for packages named "weblogic.servlet.annotation.WLServlet". When found, it adds the text "Migrate to Java EE 6 @WebServlet." to the report and points the person to the JBoss EAP 6 documentation.


System requirements
-------------------

The rule this project produces is designed to be run on Windup 2.0 or later.

This project requires Java 6.0 (Java SDK 1.6) or later and Maven 3.0 or later.

 
Install Windup 2.0
------------------

If you have not yet done so, follow the instructions to [Download and install Windup](https://github.com/windup/windup/wiki/Install-Windup).


Configure Maven
---------------

If you have not yet done so, you must configure your Maven settings to access the Windup artifacts. The instructions are located here: [Install and Configure Maven](https://github.com/windup/windup/wiki/Install-and-Configure-Maven).

An example `settings.xml` file is provided  in the root directory of the quickstarts.


Start Windup
------------

1. Open a terminal and navigate to the `WINDUP_HOME/bin` directory, where `WINDUP_HOME` denotes the path to the Windup installation.

2. Type the following command to start Windup:

        For Linux:    WINDUP_HOME/bin $ ./windup
        For Windows:  C:\WINDUP_HOME\bin> windup

3. You are presented with the following prompt.

        Using Windup at /home/username/windup-distribution-2.0.0.Beta4
        
         _       ___           __          
        | |     / (_)___  ____/ /_  ______ 
        | | /| / / / __ \/ __  / / / / __ \
        | |/ |/ / / / / / /_/ / /_/ / /_/ /
        |__/|__/_/_/ /_/\__,_/\__,_/ .___/ 
                                  /_/      
        
        JBoss Windup, version [ 2.0.0.Beta4 ] - JBoss, by Red Hat, Inc. [ http://windup.jboss.org ]
        
        [windup-distribution-2.0.0.Beta4]$ 

4. This prompt is the Windup console where you enter Windup commands.


Add the Quickstart to Windup
----------------------------

* [Build and Install the Quickstart as a Windup Addon in One Simple Step](#build-and-install-the-quickstart-as-a-windup-addon-in-one-simple-step): You can build and install the quickstart into the Maven repository and install it into Windup in one simple step. This is the fastest way to install a rule addon into Windup. 

* [Install the Quickstart into the Local Maven Repository](#install-the-quickstart-into-the-local-maven-repository): Follow these instructions to build and install the quickstart into the Maven repository using Maven command line. Use these instructions if you need specific build options. 

* [Install the Quickstart into Windup as an Addon](#install-the-quickstart-into-windup-as-an-addon): If you build the quickstart using Maven, follow these instructions to install the quickstart into Windup as a rule addon.


### Build and Install the Quickstart as a Windup Addon in One Simple Step

This is the easiest and fastest way to build the quickstart, install it into the local Maven repository, and install it into Windup as a rule addon.

1. If you have not started Windup, follow the instructions above to [Start Windup](#start-windup).

2. Build the quickstart and install the addon in Windup using the `addon-build-and-install` command in the Windup console. This command uses the following syntax, where `QUICKSTART_HOME` refers the root directory of this quickstart:

        addon-build-and-install --projectRoot QUICKSTART_HOME
        
   For example:
   
        addon-build-and-install --projectRoot /home/username/windup-quickstarts/weblogic-javaee-servlet/
        
   You should see the following result.
   
        ***SUCCESS*** Addon org.jboss.windup.quickstarts:windup-weblogic-javaee-servlet:::2.0.0.Beta4 was installed successfully.


### Install the Quickstart into the Local Maven Repository

Use these instructions to build the quickstart using the Maven command line and install it into your local Maven repository. This is useful if you need to use specific build options other than the default.

1. Open a command prompt and navigate to the root directory of this quickstart.
2. Type this command to build and install the rule in your local Maven repository:

        mvn clean install
        
The quickstart is now installed in the local Maven repository.


### Install the Quickstart into Windup as an Addon

After you build the quickstart and install it into the local Maven repository, use these instructions to install it into Windup as a rule addon.

1. If you have not started Windup, follow the instructions above to [Start Windup](#start-windup).

2. Add the rule to Windup using the `addon-install` command in the Windup console.

* Type the following command at the Windup prompt:

        addon-install  

* Windup responds with this prompt: 

        Coordinate (The addon's "groupId:artifactId,version" coordinate):

*  The `groupId`, `artifactId`, and `version` are specified in the quickstart `pom.xml` file. At the prompt, enter the following response:
       
        org.jboss.windup.quickstarts:windup-weblogic-javaee-servlet,2.0.0.Beta4

   You should see the following result:

        ***SUCCESS*** Addon org.jboss.windup.quickstarts:windup-weblogic-javaee-servlet,2.0.0.Beta4 was installed successfully.



Test the Quickstart
--------------

To test this quickstart, you must run Windup against a WebLogic application that contains the proprietary annotations.

1. If you have not started Windup, follow the instructions above to [Start Windup](#start-windup).

2. Test the rule WebLogic application by running the `windup-migrate-app` command at the Windup prompt. The command uses this syntax:

        windup-migrate-app [--sourceMode true] --input INPUT_ARCHIVE_OR_FOLDER --output OUTPUT_REPORT_DIRECTORY --packages PACKAGE_1 PACKAGE_2 PACKAGE_N

   The following example shows how to run Windup against an archive:
   
        windup-migrate-app --input /home/username/my-weblogic-app.ear --output /home/username/WindupOutput/my-weblogic-app-report --packages weblogic.servlet

   The following example shows how to run Windup against a source directory:

        windup-migrate-app --input /home/username/my-weblogic-app/ --output /home/username/WindupOutput/my-weblogic-app-report --packages weblogic.servlet

For more information about how to run Windup, see: [Execute Windup](https://github.com/windup/windup/wiki/Execute-Windup). 


Remove the Quickstart from Windup
--------------------

Remove the quickstart rule addon from Windup using the `addon-remove` command.

1. If you have not started Windup, follow the instructions above to [Start Windup](#start-windup).

2. Type the following command at the Windup prompt:

        addon-remove  

3. Windup responds with a list of installed add-ons.

        [0] - org.jboss.forge.furnace.container:cdi,2.12.1.Final
        [1] - org.jboss.windup.quickstarts:windup-weblogic-javaee-servlet,2.0.0.Beta4
        
        Installed addons (The installed addons in mutable addon repositories that may be removed): [0-1] 


4. Choose the number of this rule addon, in this case, type `1` and hit enter. Then leave it blank and hit enter to finish. You should see:

        ***SUCCESS*** Removed addons: org.jboss.windup.quickstarts:windup-weblogic-javaee-servlet,2.0.0.Beta4


Stop Windup
-----------

To stop Windup, type the following command in the Windup console:

        exit


Run the Quickstart Arquillian Tests 
-------------------------

This quickstart provides Arquillian tests. 

1. Open a command prompt and navigate to the root directory of this quickstart.
2. Type the following command to run the test goal:

        mvn clean test
3. You should see the following results.

        Results :

        Tests run: 1, Failures: 0, Errors: 0, Skipped: 0


