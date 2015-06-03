package org.windup.examples.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

​import com.example.proprietary.servlet.annotation.ProprietaryInitParam;
​import com.example.proprietary.servlet.annotation.ProprietaryServlet;
​ 
​/**
 * This is an example of a servlet that uses the propietary 
 * @ProprietaryServlet and @ProprietaryInitParam annotations.
 * 
 * This is not a fully functional class. Its sole purpose is 
 * to demonstrate Windup rule addon processing.
 * 
 * @author Windup-Team
 */
@ProprietaryServlet (
​    name = "catalog",
​    runAs = "SuperEditor"
​    initParams = { 
​        @ProprietaryInitParam (name="catalog", value="spring"),
​        @ProprietaryInitParam (name="language", value="English")
​     },
​     mapping = {"/catalog/*"}
​)
​public class ExampleProprietaryServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        // noop
    }

}
