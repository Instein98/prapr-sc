package org.mudebug.prapr.entry.mutationtest;

/*
 * #%L
 * prapr-plugin
 * %%
 * Copyright (C) 2018 - 2019 University of Texas at Dallas
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.mudebug.prapr.core.SuspStrategy;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.coverage.CoverageDatabase;
import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.SourceLocator;
import org.pitest.mutationtest.config.ReportOptions;
import org.pitest.mutationtest.engine.MutationEngine;
import org.pitest.plugin.FeatureSetting;
import org.pitest.util.ResultOutputStrategy;

import java.util.Collection;

/**
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 * @since 2.0.3
 */
public class AugmentedListenerArguments extends ListenerArguments {
    private final Collection<String> failingTests;

    private final int allTestsCount;

    private final SuspStrategy suspStrategy;

    private final ClassByteArraySource cbas;

    private final boolean dumpMutations;

    // ListenerArguments fields
    private final ResultOutputStrategy outputStrategy;
    private final CoverageDatabase     coverage;
    private final long                 startTime;
    private final SourceLocator        locator;
    private final MutationEngine       engine;
    private final boolean              fullMutationMatrix;
    private final ReportOptions        data;
    private final FeatureSetting       setting;

    public AugmentedListenerArguments(final ResultOutputStrategy outputStrategy,
                                      final CoverageDatabase coverage,
                                      final SourceLocator locator,
                                      final MutationEngine engine,
                                      final ClassByteArraySource cbas,
                                      final long startTime,
                                      final boolean fullMutationMatrix,
                                      final ReportOptions data,
                                      final SuspStrategy suspStrategy,
                                      final Collection<String> failingTests,
                                      final int allTestsCount,
                                      final boolean dumpMutations) {
        this(outputStrategy, coverage, locator, engine, cbas, startTime, fullMutationMatrix, data,
                null, suspStrategy, failingTests, allTestsCount, dumpMutations);
    }

    AugmentedListenerArguments(final ResultOutputStrategy outputStrategy,
                                      final CoverageDatabase coverage,
                                      final SourceLocator locator,
                                      final MutationEngine engine,
                                      final ClassByteArraySource cbas,
                                      final long startTime,
                                      final boolean fullMutationMatrix,
                                      final ReportOptions data,
                                      final FeatureSetting setting,
                                      final SuspStrategy suspStrategy,
                                      final Collection<String> failingTests,
                                      final int allTestsCount,
                                      final boolean dumpMutations) {
        super(outputStrategy, coverage, locator, engine, startTime, fullMutationMatrix, data);
        this.failingTests = failingTests;
        this.allTestsCount = allTestsCount;
        this.suspStrategy = suspStrategy;
        this.cbas = cbas;
        this.dumpMutations = dumpMutations;

        // ListenerArguments fields
        this.outputStrategy = outputStrategy;
        this.coverage = coverage;
        this.locator = locator;
        this.startTime = startTime;
        this.engine = engine;
        this.fullMutationMatrix = fullMutationMatrix;
        this.data = data;
        this.setting = setting;
    }

    public Collection<String> getFailingTests() {
        return this.failingTests;
    }

    public int getAllTestsCount() {
        return this.allTestsCount;
    }

    public SuspStrategy getSuspStrategy() {
        return this.suspStrategy;
    }

    public ClassByteArraySource getClassByteArraySource() {
        return this.cbas;
    }

    public boolean shouldDumpMutations() {
        return this.dumpMutations;
    }

    @Override
    public ListenerArguments withSetting(FeatureSetting setting) {
        return new AugmentedListenerArguments(outputStrategy,
                coverage,
                locator,
                engine,
                cbas,
                startTime,
                fullMutationMatrix,
                data,
                setting,
                suspStrategy,
                failingTests,
                allTestsCount,
                dumpMutations);
    }
}
