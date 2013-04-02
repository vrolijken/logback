/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2011, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.access.joran;


import ch.qos.logback.access.PatternLayout;
import ch.qos.logback.access.PatternLayoutEncoder;
import ch.qos.logback.access.boolex.JaninoEventEvaluator;
import ch.qos.logback.access.joran.action.ConfigurationAction;
import ch.qos.logback.access.joran.action.EvaluatorAction;
import ch.qos.logback.access.net.SSLSocketAppender;
import ch.qos.logback.access.net.server.SSLServerSocketAppender;
import ch.qos.logback.access.sift.SiftAction;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.filter.EvaluatorFilter;
import ch.qos.logback.core.joran.JoranConfiguratorBase;
import ch.qos.logback.core.joran.action.AppenderRefAction;
import ch.qos.logback.core.joran.action.IncludeAction;
import ch.qos.logback.core.joran.action.NOPAction;
import ch.qos.logback.core.joran.conditional.ElseAction;
import ch.qos.logback.core.joran.conditional.IfAction;
import ch.qos.logback.core.joran.conditional.ThenAction;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.spi.Pattern;
import ch.qos.logback.core.joran.spi.RuleStore;
import ch.qos.logback.core.net.ssl.SSLConfiguration;
import ch.qos.logback.core.net.ssl.SSLNestedComponentRegistryRules;



/**
 * This JoranConfiguratorclass adds rules specific to logback-access.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class JoranConfigurator extends JoranConfiguratorBase {

  @Override
  public void addInstanceRules(RuleStore rs) {
    super.addInstanceRules(rs);
    
    rs.addRule(new Pattern("configuration"), new ConfigurationAction());
    rs.addRule(new Pattern("configuration/appender-ref"), new AppenderRefAction());
    
    rs.addRule(new Pattern("configuration/appender/sift"), new SiftAction());
    rs.addRule(new Pattern("configuration/appender/sift/*"), new NOPAction());
    
    rs.addRule(new Pattern("configuration/evaluator"), new EvaluatorAction());

    // add if-then-else support
    rs.addRule(new Pattern("*/if"), new IfAction());
    rs.addRule(new Pattern("*/if/then"), new ThenAction());
    rs.addRule(new Pattern("*/if/then/*"), new NOPAction());
    rs.addRule(new Pattern("*/if/else"), new ElseAction());
    rs.addRule(new Pattern("*/if/else/*"), new NOPAction());

    rs.addRule(new Pattern("configuration/include"), new IncludeAction());
  }

  @Override
  protected void addDefaultNestedComponentRegistryRules(
      DefaultNestedComponentRegistry registry) {
    registry.add(AppenderBase.class, "layout", PatternLayout.class);
          registry
        .add(EvaluatorFilter.class, "evaluator", JaninoEventEvaluator.class);

    registry.add(AppenderBase.class, "encoder", PatternLayoutEncoder.class);
    registry.add(UnsynchronizedAppenderBase.class, "encoder", PatternLayoutEncoder.class);
    registry.add(SSLSocketAppender.class, "ssl", SSLConfiguration.class);
    registry.add(SSLServerSocketAppender.class, "ssl", SSLConfiguration.class);
    SSLNestedComponentRegistryRules.addDefaultNestedComponentRegistryRules(registry);
  }

}
