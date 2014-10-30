ejb-beanutils-async: Java-based Rule Addon That Detects BeanUtils AsyncronousMethod
=============================================================================================
Author: Jess Sightler
Level: Intermediate
Technologies: 
Summary: Windup rule that reports on com.beanutils.async.AsyncronousMethod in remote EJBs
Target Product: Windup
Product Versions: 2.0
Source: <https://github.com/windup/windup-quickstarts/>

What is it?
-----------

The BeanUtils AsyncronousMethod is not compatible with JBoss EAP Remote EJBs, and should be replaced with the Java EE 6 @Asyncronous annotation

Review the Quickstart Code
-------------------------

The `EjbBeanUtilsAsyncUsageRuleProvider` class extends `WindupRuleProvider` and overrides the following methods:

* `getConfiguration(GraphContext context)`: This method looks for annotations named "com.beanutils.async.AsyncronousMethod". When found, it adds the text "BeanUtils Asyncronous is not compatible with JBoss EAP Remote EJBs, and should be replaced with the Java EE 6 @Asyncronous annotation." to the report and points the person to the JBoss EAP 6 documentation.

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


Build and Install the Quickstart
-------------------------

1. Open a command prompt and navigate to the root directory of this quickstart.
2. Type this command to build and install the rule in your local Maven repository:

        mvn clean install
        
The rule is now accessible to Windup.


Install the Rule as a Windup Addon
---------------------------------

1. Start Windup

* Open a terminal and navigate to the `WINDUP_HOME/bin` directory

* Type the following command to start Windup:

        For Linux:    WINDUP_HOME/bin $ ./windup
        For Windows:  C:\WINDUP_HOME\bin> windup

* You are presented with the following prompt.

        Using Windup at /home/username/windup-distribution-2.0.0.Beta3
        
         _       ___           __          
        | |     / (_)___  ____/ /_  ______ 
        | | /| / / / __ \/ __  / / / / __ \
        | |/ |/ / / / / / /_/ / /_/ / /_/ /
        |__/|__/_/_/ /_/\__,_/\__,_/ .___/ 
                                  /_/      
        
        JBoss Windup, version [ 2.0.0.Beta3 ] - JBoss, by Red Hat, Inc. [ http://windup.jboss.org ]
        
        [windup-distribution-2.0.0.Beta3]$ 
2. Add the rule to Windup using the `addon-install` command.

* Type the following command at the Windup prompt:

        [windup-distribution-2.0.0.Beta3]$ addon-install  

* Windup responds with this prompt: 

        Coordinate (The addon's "groupId:artifactId,version" coordinate):

*  The `groupId`, `artifactId`, and `version` are specified in the quickstart `pom.xml` file. At the prompt, type the following response:
       
        org.jboss.windup.quickstarts:windup-ejb-beanutils-async,2.0.0.Beta3

   You should see the following result:

        ***SUCCESS*** Addon org.jboss.windup.quickstarts:windup-ejb-beanutils-async,2.0.0.Beta3 was installed successfully.

Test the Rule
---------------------

To test this rule, you must run the migration tool against a WebLogic application that contains the proprietary annotations.

1. Start Windup as described above. 

2. Test the rule WebLogic application by running the `windup-migrate-app` command at the Windup prompt. The command uses this syntax:

        windup-migrate-app --input INPUT_ARCHIVE_OR_FOLDER --output OUTPUT_REPORT_DIRECTORY --packages PACKAGE_1 PACKAGE_2 PACKAGE_N

   For example:
        windup-migrate-app --input /home/username/my-weblogic-app/ --output /home/username/WindupOutput/my-weblogic-app-report --packages weblogic.servlet


For more information about how to run Windup, see: [Execute Windup](https://github.com/windup/windup/wiki/Execute-Windup). 


Remove the Rule from Windup
--------------------

Remove the rule from Windup using the `addon-remove` command.

1. Start Windup as described above. 

2. Type the following command at the Windup prompt:

        [windup-distribution-2.0.0.Beta3]$ addon-remove  

3. Windup responds with a list of installed add-ons.

        [0] - org.jboss.forge.furnace.container:cdi,2.12.1.Final
        [1] - org.jboss.windup.quickstarts:windup-weblogic-javaee-servlet,2.0.0.Beta3
        
        Installed addons (The installed addons in mutable addon repositories that may be removed): [0-1] 


4. Choose the number of this rule addon, in this case, type `1` and hit enter. Then leave it blank and hit enter to finish. You should see:

        ***SUCCESS*** Removed addons: org.jboss.windup.quickstarts:windup-weblogic-javaee-servlet,2.0.0.Beta3

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


