package org.windup.examples.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

​import acme.servlet.annotation.AcmeInitParam;
​import acme.servlet.annotation.AcmeServlet;
​ 
​/**
 * This is an example of a servlet that uses the propietary 
 * @AcmeServlet and @AcmeInitParam annotations.
 * 
 * This is not a fully functional class. Its sole purpose is 
 * to demonstrate Windup rule addon processing.
 * 
 * @author Windup-Team
 */
@AcmeServlet (
​    name = "catalog",
​    runAs = "SuperEditor"
​    initParams = { 
​        @AcmeInitParam (name="catalog", value="spring"),
​        @AcmeInitParam (name="language", value="English")
​     },
​     mapping = {"/catalog/*"}
​)
​public class SampleAcmeServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        // noop
    }

}
