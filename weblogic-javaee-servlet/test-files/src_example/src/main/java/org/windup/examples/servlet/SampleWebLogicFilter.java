package org.windup.examples.servlet;

import javax.servlet.Filter;

import weblogic.servlet.annotation.WLFilter;
​import weblogic.servlet.annotation.WLInitParam;
​
@WLFilter (
            name = "MyHintsRuleFilter",
            initParams = { @WLInitParam (name="language", value="Emglish") }
            mapping = {"/info/*"}
          )
public class SampleWebLogicFilter implements Filter {
    // no-op
}