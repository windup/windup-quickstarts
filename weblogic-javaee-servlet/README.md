weblogic-javaee-servlet: Windup Rule That Detects WebLogic Proprietary Servlet Annotations
=============================================================================================

What is it?
-----------

WebLogic provides its own proprietary servlet and filter annotations for dependency injection. If the application uses them, they must be replaced with the standard Java EE 6 annotations. These examples demonstrates how to create a Windup rule addon that searches for these proprietary annotations and reports on them.

The rule searches for the following annotations:

* *@WLServlet*: This is the equivalent of the Java EE 6 *@WebServlet* annotation.

* *@WLFilter*: This is the equivalent of the Java EE 6 *@WebFilter* annotation.

* *@WLInitParam*: This is the equivalent of the Java EE 6 *@WebInitParam* annotation.


You can create Winupup rule addons using Java or XML. This quickstart demonstrates both.

* [Java-based rule addon](rules-java/README.md): Follow the instructions here to see how to create a java-based rule addon.

* [XML-based rule addon](rules-xml/README.md): Follow the instructions here to see how to create an xml-based rule addon.


