ejb-beanutils-async: Java Rule Addon That Detects Seam Asynchronous Annotation in Remote EJBs
=============================================================================================
Author: Jess Sightler
Level: Intermediate
Technologies: 
Summary: Windup Java rule addon that reports on use of the Seam Asynchronous annotation in remote EJBs
Target Product: Windup
Product Versions: 2.0
Source: <https://github.com/windup/windup-quickstarts/>

What is it?
-----------

The Seam `@Asynchronous` annotation is not compatible with remote EJBs in Red Hat JBoss Enterprise Application Platform. This Java rule addon tests for for use of `org.jboss.seam.annotations.async.Asynchronous` annotations in remote EJBs and reports that it must be replaced with the standard Java EE 6 `@Asynchronous` annotation.

Review the Quickstart Code
-------------------------

The `EjbBeanUtilsAsyncUsageRuleProvider` class extends `RuleProvider` and overrides the following methods:

* `getConfiguration(GraphContext context)`: This method looks for annotations named "com.beanutils.async.AsynchronousMethod". When found, it adds the text "`REMOTE_EJB_CLASS_NAME` uses the Seam @Asynchronous annotation. It is not compatible with JBoss EAP Remote EJBs and should be replaced with the standard Java EE 6 @Asynchronous annotation." to the report and points the person to the JBoss EAP 6 documentation.

System requirements
-------------------

The rule this project produces is designed to be run on Windup 2.0 or later.

This project requires Java 6.0 (Java SDK 1.6) or later and Maven 3.0 or later.

 
Install Windup 2.0
-------------------

If you have not yet done so, [download and intall Windup](https://github.com/windup/windup/wiki/Install-Windup).


Configure Maven
---------------

If you have not yet done so, you must [install and configure Maven](https://github.com/windup/windup/wiki/Install-and-Configure-Maven).


Start Windup
------------

1. Open a terminal and navigate to the `WINDUP_HOME/bin` directory, where `WINDUP_HOME` denotes the path to the Windup installation.

2. Type the following command to start Windup:

        For Linux:    WINDUP_HOME/bin $ ./windup
        For Windows:  C:\WINDUP_HOME\bin> windup

3. You are presented with the following prompt.

        Using Windup at /home/username/windup-distribution-2.0.0.Final
        
         _       ___           __          
        | |     / (_)___  ____/ /_  ______ 
        | | /| / / / __ \/ __  / / / / __ \
        | |/ |/ / / / / / /_/ / /_/ / /_/ /
        |__/|__/_/_/ /_/\__,_/\__,_/ .___/ 
                                  /_/      
        
        JBoss Windup, Final [ 2.0.0.Final ] - JBoss, by Red Hat, Inc. [ http://windup.jboss.org ]
        
        [windup-distribution-2.0.0.Final]$ 

4. This prompt is the Windup console where you enter Windup commands.



Add the Quickstart to Windup
----------------------------

* [Build and Install the Quickstart as a Windup Addon in One Simple Step](#build-and-install-the-quickstart-as-a-windup-addon-in-one-simple-step): You can build and install the quickstart into the Maven repository and install it into Windup in one simple step. This is the fastest way to install a rule addon into Windup. 

* [Install the Quickstart into the Local Maven Repository](#install-the-quickstart-into-the-local-maven-repository): Follow these instructions to build and install the quickstart into the Maven repository using Maven command line. Use these instructions if you need specific build options. 

* [Install the Quickstart into Windup as an Addon](#install-the-quickstart-into-windup-as-an-addon): If you build the quickstart using Maven, follow these instructions to install the quickstart into Windup as a rule addon.


### Build and Install the Quickstart as a Windup Addon in One Simple Step

This is the easiest and fastest way to build the quickstart, install it into the local Maven repository, and install it into Windup as a rule addon.

1. If you have not started Windup, follow the instructions above to [Start Windup](#start-windup).

2. Build the quickstart and install the addon in Windup using the `addon-build-and-install` command in the Windup console. 

  The command uses the following syntax, where `QUICKSTART_HOME` refers the root directory of this `ejb-beanutils-async` quickstart:

        addon-build-and-install --projectRoot QUICKSTART_HOME/rules-java
        
   For example:
   
        addon-build-and-install --projectRoot /home/username/windup-quickstarts/ejb-beanutils-async/rules-java
        
   You should see the following result.
   
        ***SUCCESS*** Addon org.jboss.windup.quickstarts:ejb-beanutils-async:::2.0.0.Final was installed successfully.
3. You can now skip the next two sections and proceed to [Test the Quickstart Rule Addon](#test-the-quickstart-rule-addon).

### Install the Quickstart into the Local Maven Repository

Use these instructions to build the quickstart using the Maven command line and install it into your local Maven repository. This is useful if you need to use specific build options other than the default.

1. Open a command prompt and navigate to this quickstart's `rules-java/` directory.
2. Type this command to build and install the rule in your local Maven repository:

        mvn clean install
        
3. The quickstart is now installed in the local Maven repository. Continue to the next section to [Install the Quickstart into Windup as an Addon](#install-the-quickstart-into-windup-as-an-addon).

### Install the Quickstart into Windup as an Addon

After you build the quickstart and install it into the local Maven repository, use these instructions to install it into Windup as a rule addon.

1. If you have not started Windup, follow the instructions above to [Start Windup](#start-windup).

2. Be sure to navigate to this quickstart's `rules-java/` directory.

3. Add the rule to Windup using the `addon-install` command in the Windup console.

  Type the following command at the Windup prompt:

        addon-install  

  Windup responds with this prompt: 

        Coordinate (The addon's "groupId:artifactId,version" coordinate):
4.  The `groupId`, `artifactId`, and `version` are specified in the quickstart `pom.xml` file. At the prompt, enter the following response:
       
        org.jboss.windup.quickstarts:ejb-beanutils-async,2.0.0.Final

   You should see the following result:

        ***SUCCESS*** Addon org.jboss.windup.quickstarts:ejb-beanutils-async,2.0.0.Final was installed successfully.
5. You can now [Test the Quickstart Rule Addon](#test-the-quickstart-rule-addon).



Test the Quickstart Rule Addon
-----------------------------

To test this rule, you must run the migration tool against an application that contains the Seam `org.jboss.seam.annotations.async.Asynchronous` annotation. The `src_example` folder located in the `test-files/` directory contains XML and Java class files that can be used to test the rule addon.

1. Start Windup as described above. 

2. Test the rule quickstart application by running the `windup-migrate-app` command at the Windup prompt. 

  The command uses this syntax:

        windup-migrate-app [--sourceMode true] --input INPUT_ARCHIVE_OR_FOLDER --output OUTPUT_REPORT_DIRECTORY --packages PACKAGE_1 PACKAGE_2 PACKAGE_N

   To test this quickstart using the `test-files/src_example/` folder provided in the root directory of this quickstart, type the following commmand. Be sure to replace `QUICKSTART_HOME` with the fully qualified path to this quickstart.
   
        windup-migrate-app -sourceMode true --input QUICKSTART_HOME/test-files/src_example/ --output QUICKSTART_HOME/windup-reports-java --packages org.windup


For more information about how to run Windup, see: [Execute Windup](https://github.com/windup/windup/wiki/Execute-Windup). 


Review the Quickstart Report
----------------------------


1. Open the `QUICKSTART_HOME/windup-reports-xml/index.html` file in a browser.  

   You are presented with the following index page.  

![Index page](../images/windup-report-index-page.png)  
2. Click on the `src_example` link.  

   This opens an overview page showing a total of 9 story points and the list of the relevant files along with the warning messages, links to obtain more information, and the estimated story points for each item.  

*src/resources/sample-ejb-jar.xml* shows 0 story points

       
*org.windup.examples.ejb.BeanUtilsAsyncUsingRemote* show 9 story points

        4 points, 2 points for each of the two @WLInitParam references
        9 points for References annotation 'org.jboss.seam.annotations.async.Asynchronous'


![Overview page](../images/windup-report-overview-page.png)  
3. Click on the file links to drill down and find more information.  

* The **Information** section reports on the matching conditions and provides a link to the standard Java EE servlet annotation documentation.

* This is followed by the source code matching the condition with a detailed message desription.

![Detail page](../images/windup-report-detail-page.png)  


Remove the Quickstart Rule from Windup
--------------------

Remove the rule from Windup using the `addon-remove` command.

1. Start Windup as described above. 

2. Type the following command at the Windup prompt:

        [windup-distribution-2.0.0.Beta3]$ addon-remove  

3. Windup responds with a list of installed add-ons.

        [0] - org.jboss.forge.furnace.container:cdi,2.12.1.Final
        [1] - org.jboss.windup.quickstarts:ejb-beanutils-async,2.0.0.Beta3
        
        Installed addons (The installed addons in mutable addon repositories that may be removed): [0-1] 


4. Choose the number of this rule addon, in this case, type `1` and hit enter. Then leave it blank and hit enter to finish. You should see:

        ***SUCCESS*** Removed addons: org.jboss.windup.quickstarts:ejb-beanutils-async,2.0.0.Final

5. Type `exit` to leave Windup.


Run the Arquillian Tests 
-------------------------

This quickstart provides Arquillian tests. 

1. Open a command prompt and navigate to the root directory of this quickstart.
2. Type the following command to run the test goal:

        mvn clean test
3. You should see the following results.

        Results :

        Tests run: 1, Failures: 0, Errors: 0, Skipped: 0


