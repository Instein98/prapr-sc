package org.mudebug.prapr.entry.report.compressedxml;

/*
 * #%L
 * prapr-plugin
 * %%
 * Copyright (C) 2018 - 2021 University of Texas at Dallas
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

import org.pitest.mutationtest.report.xml.XMLReportListener;
import org.pitest.util.ResultOutputStrategy;

import java.io.Writer;

public class PraprXMLReportListener extends XMLReportListener {
    public PraprXMLReportListener(ResultOutputStrategy outputStrategy, boolean fullMutationMatrix) {
        super(outputStrategy, fullMutationMatrix);
    }

    public PraprXMLReportListener(Writer out, boolean fullMutationMatrix) {
        super(out, fullMutationMatrix);
    }
}
