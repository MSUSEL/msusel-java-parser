/**
 * The MIT License (MIT)
 *
 * MSUSEL Java Parser
 * Copyright (c) 2015-2019 Montana State University, Gianforte School of Computing,
 * Software Engineering Laboratory and Idaho State University, Informatics and
 * Computer Science, Empirical Software Engineering Laboratory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.montana.gsoc.msusel.datamodel.parsers;

import com.beust.jcommander.internal.Lists;
import edu.isu.isuese.datamodel.File;
import edu.isu.isuese.datamodel.Module;
import edu.isu.isuese.datamodel.Project;
import edu.isu.isuese.datamodel.System;
import lombok.extern.log4j.Log4j2;
import org.javalite.activejdbc.test.DBSpec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.fail;

@Log4j2
public class JDTeste extends DBSpec {

    JavaDirector fixture;
    List<File> files = Lists.newArrayList();
    String basePath = "data/example-classes";

    @BeforeEach
    public void setUp() throws Exception {
        System sys   = System.builder().name("Test").key("test").basePath(basePath).create();
        Project proj  = Project.builder().name("test").projKey("test").relPath("").create();
        Module mod   = Module.builder().name("default").moduleKey("default").relPath("").create();
        proj.addModule(mod);
        sys.addProject(proj);

        files.add(File.builder().name(basePath + "/AllInOne7.java").fileKey(basePath + "/AllInOne7.java").relPath("AllInOne7.java").create());
        files.add(File.builder().name(basePath + "/AllInOne8.java").fileKey(basePath + "/AllInOne8.java").relPath("AllInOne8.java").create());

        fixture = new JavaDirector(proj, log);
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void build() {

    }

    @Test
    public void identify() {
    }

    @Test
    public void process() {
        fixture.process(files);

        fail();
    }

    @Test
    public void onFiles() {
    }

    @Test
    public void gatherTypeAssociations() {
    }

    @Test
    public void gatherFileAndTypeInfo() {
    }

    @Test
    public void gatherMembersAndBasicRelationInfo() {
    }

    @Test
    public void gatherMemberUsageInfo() {
    }

    @Test
    public void gatherStatementInfo() {
    }

    @Test
    public void includeFile() {
    }

    @Test
    public void utilizeParser() {
    }
}
