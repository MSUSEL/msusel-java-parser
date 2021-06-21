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
package edu.montana.gsoc.msusel.datamodel.parsers

import edu.isu.isuese.datamodel.File
import edu.isu.isuese.datamodel.FileType
import edu.isu.isuese.datamodel.Project
import edu.isu.isuese.datamodel.util.DBCredentials
import edu.isu.isuese.datamodel.util.DBManager
import groovy.util.logging.Log4j2
import groovyx.gpars.GParsExecutorsPool
import groovyx.gpars.GParsPool
import org.apache.logging.log4j.Logger

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
@Log4j2
abstract class BaseDirector {

    boolean statements
    boolean useSinglePass
    boolean createCFG
    boolean useExpressions
    protected Project proj
    ArtifactIdentifier identifier
    DBCredentials credentials
    BaseModelBuilder builder

    BaseDirector(Project proj, ArtifactIdentifier identifier, DBCredentials creds, boolean statements = false, boolean useSinglePass = true, boolean createCFG = false, boolean useExpressions) {
        this.proj = proj
        this.identifier = identifier
        this.statements = statements
        this.credentials = creds
        this.useSinglePass = useSinglePass
        this.createCFG = createCFG
        this.useExpressions = useExpressions
    }

    void build(String path) {
        identify(proj, path)

        List<File> files = []
        DBManager.instance.open(credentials)
        files.addAll(proj.getFilesByType(FileType.SOURCE))
        files.removeIf { it.getAllTypes().size() > 0 }
        DBManager.instance.close()

        process(files)
        identifier = null
    }

    void identify(Project proj, String path) {
        log.info "Setting up the artifact identifier"
        identifier.setProj(proj)

        log.info "Traversing the project and gathering artifact info"
        identifier.identify(path)

        log.info "Artifact info gathering complete"
    }

    void process(final List<File> files) {
        log.info "Processing files and their contained info"

        if (useSinglePass) {
            log.info "Parsing and extracting file info"
            int total = files.size()
            int current = 1
//            GParsPool.withPool(8) {
//                files.eachParallel { File file ->
            files.each { File file ->
                if (includeFile(file)) {
                    gatherAllInfoAtOnce(file, current, total)
                }
                current++
            }
//            }
        } else {
            log.info "Gathering File and Type Info into Model"
            files.each { File file -> log.info "File: $file"; if (includeFile(file)) gatherFileAndTypeInfo(file) }

            log.info "Gathering Type Members and Basic Relation Info into Model"
            files.each { File file -> if (includeFile(file)) gatherMembersAndBasicRelationInfo(file) }

            log.info "Gathering Member Usage Info into Model"
            files.each { File file -> if (includeFile(file)) gatherMemberUsageInfo(file) }

            if (statements) {
                log.info "Gathering Statement Info and CFG into Model"
                files.each { File file -> if (includeFile(file)) gatherStatementInfo(file) }
            }
        }

        log.info "Gathering Type Associations into Model"
        gatherTypeAssociations()
        log.info "File processing complete"
    }

    abstract void gatherFileAndTypeInfo(File file)

    abstract void gatherMembersAndBasicRelationInfo(File file)

    abstract void gatherMemberUsageInfo(File file)

    abstract void gatherStatementInfo(File file)

    abstract void gatherAllInfoAtOnce(File file, int current, int total)

    abstract boolean includeFile(File file)

    void gatherTypeAssociations() {
        AssociationExtractor assocXtractor = new AssociationExtractor(proj, credentials)
        assocXtractor.extractAssociations()
    }
}