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


Execute Windup 
---------------------

To test the rule, follow the instructions to [Execute Windup](https://github.com/windup/windup/wiki/Execute-Windup). To see information reported by this rule, you must test using a WebLogic application that contains the proprietary annotations.


Remove the Rule
--------------------

Remove the rule from Windup using the `addon-remove` command.


Run the Arquillian Tests 
-------------------------

This quickstart provides Arquillian tests. 

1. Open a command prompt and navigate to the root directory of this quickstart.
2. Type the following command to run the test goal:

        mvn clean test
3. You should see the following results.

        Results :

        Tests run: 1, Failures: 0, Errors: 0, Skipped: 0


