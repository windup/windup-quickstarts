Windup Quickstarts
=================

Summary: The quickstarts in this repository provide examples of how to create rules for Windup 2.0.

Introduction
------------

These quickstarts provide examples of how to create custom rules for Windup 2.0.

Windup documentation is located on the Windup Wiki here: <https://github.com/windup/windup/wiki>


Available Quickstarts
---------------------

The Windup quickstarts are located in GitHub here: <https://github.com/windup/windup-quickstarts>

* [ejb-beanutils-async](ejb-beanutils-async/README.md)
* [weblogic-javaee-servlet](weblogic-javaee-servlet/README.md)

[TOC-quickstart]


System requirements
-------------------

The rules in this repository are designed to be run on Windup 2.0 or later.

This project requires Java 6.0 (Java SDK 1.6) or later and Maven 3.0 or later.

 

Configure Maven
---------------

If you have not yet done so, you must configure your Maven settings to access the Windup artifacts. The instructions are located here: [Install and Configure Maven](https://github.com/windup/windup/wiki/Install-and-Configure-Maven).

An example `settings.xml` file is provided  in the root directory of the quickstarts.


Install Windup 2.0
------------------

If you have not yet done so, follow the instructions to [Download and install Windup](https://github.com/windup/windup/wiki/Install-Windup).


Use of WINDUP_HOME Variable
---------------------------------

The quickstart README files use the *replaceable* value `WINDUP_HOME` to denote the path to the Windup installation. When you encounter this value in a README file, be sure to replace it with the actual path to your Windup installation. 


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


Stop Windup
-----------

To stop Windup, type the following command in the Windup console:

        exit



