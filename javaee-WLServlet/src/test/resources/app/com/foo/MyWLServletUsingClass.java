package com.foo;
import javax.servlet.http.HttpServlet;
import weblogic.servlet.annotation.WLServlet;
import weblogic.servlet.annotation.WLInitParam;

/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
@WLServlet(name="FOO", runAs="SuperUser", initParams = { @WLInitParam (name="one", value="1") }, mapping = {"/foo/*"})
public class MyWLServletUsingClass extends HttpServlet {

}
