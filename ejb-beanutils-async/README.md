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

The BeanUtils AsynchronousMethod is not compatible with Red Hat JBoss Enterprise Application Platform Remote EJBs and should be replaced with the Java EE 6 @Asynchronous annotation

You can create Windup rule addons using Java or XML. This quickstart demonstrates a Java-based rule addon.

* [Java-based rule addon](rules-java/README.md): Follow the instructions here to see how to create a Java-based rule addon.



