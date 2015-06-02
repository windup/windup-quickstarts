package com.foo;
import javax.servlet.http.HttpServlet;
import acme.servlet.annotation.AcmeServlet;
import acme.servlet.annotation.AcmeInitParam;

/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
@AcmeServlet(name="FOO", runAs="SuperUser", initParams = { @AcmeInitParam (name="one", value="1") }, mapping = {"/foo/*"})
public class MyAcmeServletUsingClass extends HttpServlet {

}
