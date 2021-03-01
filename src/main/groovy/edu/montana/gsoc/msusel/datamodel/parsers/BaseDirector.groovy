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
import groovyx.gpars.GParsExecutorsPool
import groovyx.gpars.GParsPool
import org.apache.logging.log4j.Logger

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
abstract class BaseDirector {

    Logger logger
    boolean statements
    protected Project proj
    ArtifactIdentifier identifier
    DBCredentials credentials

    BaseDirector(Project proj, ArtifactIdentifier identifier, Logger logger, DBCredentials creds, boolean statements = false) {
        this.proj = proj
        this.identifier = identifier
        this.logger = logger
        this.statements = statements
        this.credentials = creds
    }

    void build(String path) {
        identify(proj, path)

        List<File> files = []
        DBManager.instance.open(credentials)
        files.addAll(proj.getFilesByType(FileType.SOURCE))
        DBManager.instance.close()

        process(files)
        identifier = null
    }

    void identify(Project proj, String path) {
        logger.atInfo().log("Setting up the artifact identifier")
        identifier.setProj(proj)

        logger.atInfo().log("Traversing the project and gathering artifact info")
        identifier.identify(path)

        logger.atInfo().log("Artifact info gathering complete")
    }

    void process(final List<File> files) {
        logger.atInfo().log("Processing files and their contained info")

        logger.atInfo().log("Gathering File and Type Info into Model")
//        GParsExecutorsPool.withPool(4) {
            files.each { File file -> if (includeFile(file)) gatherFileAndTypeInfo(file) }
//        files.each { File file -> if (includeFile(file)) gatherFileAndTypeInfo(file) }
//        }

        logger.atInfo().log("Gathering Type Members and Basic Relation Info into Model")
//        GParsExecutorsPool.withPool(4) {
            files.each { File file -> if (includeFile(file)) gatherMembersAndBasicRelationInfo(file) }
//        }

        logger.atInfo().log("Gathering Member Usage Info into Model")
//        GParsExecutorsPool.withPool(4) {
//            files.eachParallel { File file -> if (includeFile(file)) gatherMemberUsageInfo(file) }
        files.each { File file -> if (includeFile(file)) gatherMemberUsageInfo(file) }
//        }

        if (statements) {
//            GParsExecutorsPool.withPool(8) {
                logger.atInfo().log("Gathering Statement Info and CFG into Model")
//                files.eachParallel { File file -> if (includeFile(file)) gatherStatementInfo(file) }
            files.each { File file -> if (includeFile(file)) gatherStatementInfo(file) }
//            }
        }

        logger.atInfo().log("Gathering Type Associations into Model")
        gatherTypeAssociations()
        logger.atInfo().log("File processing complete")
    }

    abstract void gatherFileAndTypeInfo(File file)

    abstract void gatherMembersAndBasicRelationInfo(File file)

    abstract void gatherMemberUsageInfo(File file)

    abstract void gatherStatementInfo(File file)

    abstract boolean includeFile(File file)

    void gatherTypeAssociations() {
        AssociationExtractor assocXtractor = new AssociationExtractor(proj, credentials)
        assocXtractor.extractAssociations()
    }
}