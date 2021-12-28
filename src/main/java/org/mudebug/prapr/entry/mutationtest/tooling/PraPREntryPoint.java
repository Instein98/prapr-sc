package org.mudebug.prapr.entry.mutationtest.tooling;

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

import org.mudebug.prapr.entry.coverage.execute.PraPRCoverageGenerator;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classpath.ClassPath;
import org.pitest.classpath.ClassPathByteArraySource;
import org.pitest.classpath.ClassloaderByteArraySource;
import org.pitest.classpath.CodeSource;
import org.pitest.classpath.ProjectClassPaths;
import org.pitest.coverage.execute.CoverageOptions;
import org.pitest.maven.PraPRReportOptions;
import org.pitest.mutationtest.HistoryStore;
import org.pitest.mutationtest.MutationResultListenerFactory;
import org.pitest.mutationtest.config.ReportOptions;
import org.pitest.mutationtest.config.SettingsFactory;
import org.pitest.mutationtest.incremental.NullHistoryStore;
import org.pitest.mutationtest.incremental.NullWriterFactory;
import org.pitest.mutationtest.incremental.ObjectOutputStreamHistoryStore;
import org.pitest.mutationtest.incremental.WriterFactory;
import org.pitest.mutationtest.tooling.AnalysisResult;
import org.pitest.mutationtest.tooling.EntryPoint;
import org.pitest.mutationtest.tooling.JarCreatingJarFinder;
import org.pitest.mutationtest.tooling.KnownLocationJavaAgentFinder;
import org.pitest.mutationtest.tooling.MutationStrategies;
import org.pitest.process.JavaAgent;
import org.pitest.process.LaunchOptions;
import org.pitest.util.PitError;
import org.pitest.util.ResultOutputStrategy;
import org.pitest.util.Timings;

import java.io.File;
import java.io.Reader;
import java.util.Map;
import java.util.Optional;

/**
 * CREDIT: The body of this class is mostly copied from PIT's <code>EntryPoint</code> class
 *
 * @author Ali Ghanbari (ali.ghanbari@utdallas.edu)
 * @since 2.0.3
 */
public class PraPREntryPoint extends EntryPoint {
    @Override
    public AnalysisResult execute(final File baseDir,
                                  final ReportOptions data,
                                  final SettingsFactory settings,
                                  final Map<String, String> environmentVariables) {
        settings.checkRequestedFeatures();

        checkMatrixMode(data);

        final ClassPath cp = data.getClassPath();

        // workaround for apparent java 1.5 JVM bug . . . might not play nicely
        // with distributed testing
        final JavaAgent jac = new JarCreatingJarFinder(new ClassPathByteArraySource(cp));

        final KnownLocationJavaAgentFinder ja = new KnownLocationJavaAgentFinder(jac.getJarLocation().get());

        final ResultOutputStrategy reportOutput = settings.getOutputStrategy();

        final MutationResultListenerFactory reportFactory = settings.createListener();

        final CoverageOptions coverageOptions = settings.createCoverageOptions();
        final LaunchOptions launchOptions =
                new LaunchOptions(ja, settings.getJavaExecutable(), data.getJvmArgs(), environmentVariables);
        final ProjectClassPaths cps = data.getMutationClassPaths();

        final CodeSource code = new CodeSource(cps);

        final Timings timings = new Timings();
        final PraPRCoverageGenerator coverageDatabase = new PraPRCoverageGenerator(coverageOptions,
                baseDir, timings, settings.createCoverageExporter(),
                code, launchOptions, data.getVerbosity(), (PraPRReportOptions) data);

        final Optional<WriterFactory> maybeWriter = data.createHistoryWriter();
        WriterFactory historyWriter = maybeWriter.orElse(new NullWriterFactory());
        final HistoryStore history = makeHistoryStore(data, maybeWriter);

        final MutationStrategies strategies = new MutationStrategies(settings.createEngine(),
                history, coverageDatabase, reportFactory, reportOutput);

        final MutationCoverage report = new MutationCoverage(strategies,
                baseDir, code, data, settings, timings,
                coverageDatabase.getInferredFailingTests(),
                coverageDatabase.getAllTestsCount());

        try {
            return AnalysisResult.success(report.runReport());
        } catch (final Exception e) {
            return AnalysisResult.fail(e);
        } finally {
            jac.close();
            ja.close();
            historyWriter.close();
        }
    }

    private HistoryStore makeHistoryStore(ReportOptions data, Optional<WriterFactory> historyWriter) {
        Optional<Reader> reader = data.createHistoryReader();
        return (HistoryStore)(!reader.isPresent() && !historyWriter.isPresent() ? new NullHistoryStore() : new ObjectOutputStreamHistoryStore((WriterFactory)historyWriter.orElse(new NullWriterFactory()), reader));
    }

    private void checkMatrixMode(ReportOptions data) {
        if (data.isFullMutationMatrix() && !data.getOutputFormats().contains("XML")) {
            throw new PitError("Full mutation matrix is only supported in the output format XML.");
        }
    }
}
