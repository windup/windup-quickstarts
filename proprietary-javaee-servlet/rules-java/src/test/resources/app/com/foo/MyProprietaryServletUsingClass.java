package com.foo;
import javax.servlet.http.HttpServlet;
import com.example.proprietary.servlet.annotation.ProprietaryServlet;
import com.example.proprietary.servlet.annotation.ProprietaryInitParam;

/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
@ProprietaryServlet(name="FOO", runAs="SuperUser", initParams = { @ProprietaryInitParam (name="one", value="1") }, mapping = {"/foo/*"})
public class MyProprietaryServletUsingClass extends HttpServlet {

}
