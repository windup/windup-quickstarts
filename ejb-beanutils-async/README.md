ejb-beanutils-asyn: Windup Rule That Detects WebLogic Proprietary Servlet Annotations
=============================================================================================

Author: Jess Sightler
Level: Intermediate
Technologies: 
Summary: Windup rule that reports on com.beanutils.async.AsynchronousMethod in remote EJBs
Target Product: Windup
Product Versions: 2.0
Source: <https://github.com/windup/windup-quickstarts/>

What is it?
-----------

The BeanUtils AsynchronousMethod is not compatible with Red Hat JBoss Enterprise Application Platform remote EJBs and must be replaced with the Java EE 6 @Asynchronous annotation.

You can create Windup rules using XML or by writing Java addons. This quickstart demonstrates both approaches.

* [Java rule addon](rules-java/README.md): Follow the instructions here to see how to create this rule as a Java rule addon.

* [XML rule](rules-xml/README.md): Follow the instructions here to see how to create this rule using XML.


