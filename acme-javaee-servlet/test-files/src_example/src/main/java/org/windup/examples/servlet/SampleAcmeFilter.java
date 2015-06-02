package org.windup.examples.servlet;

import javax.servlet.Filter;

import acme.servlet.annotation.AcmeFilter;
​import acme.servlet.annotation.AcmeInitParam;
​
/**
 * This is an example of a servlet that uses the propietary 
 * @AcmeFilter and @AcmeInitParam annotations.
 * 
 * This is not a fully functional class. Its sole purpose is 
 * to demonstrate Windup rule addon processing.
 * 
 * @author Windup-Team
 */
@AcmeFilter (
    name = "MyHintsRuleFilter",
    initParams = { 
​        @AcmeInitParam (name="catalog", value="spring"),
​        @AcmeInitParam (name="language", value="English")
​     },
​     mapping = {"/catalog/*"}
)
public class SampleAcmeFilter implements Filter {
    // no-op
}
