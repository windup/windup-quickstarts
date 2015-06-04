package org.windup.examples.servlet;

import javax.servlet.Filter;

import com.example.proprietary.servlet.annotation.ProprietaryFilter;
​import com.example.proprietary.servlet.annotation.ProprietaryInitParam;
​
/**
 * This is an example of a servlet that uses the propietary 
 * @ProprietaryFilter and @ProprietaryInitParam annotations.
 * 
 * This is not a fully functional class. Its sole purpose is 
 * to demonstrate Windup rule addon processing.
 * 
 * @author Windup-Team
 */
@ProprietaryFilter (
    name = "MyHintsRuleFilter",
    initParams = { 
​        @ProprietaryInitParam (name="catalog", value="spring"),
​        @ProprietaryInitParam (name="language", value="English")
​     },
​     mapping = {"/catalog/*"}
)
public class ExampleProprietaryFilter implements Filter {
    // no-op
}
