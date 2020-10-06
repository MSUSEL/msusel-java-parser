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

import com.google.common.collect.Sets
import edu.isu.isuese.datamodel.*
import edu.isu.isuese.datamodel.util.DBCredentials
import edu.isu.isuese.datamodel.util.DBManager
import groovyx.gpars.GParsExecutorsPool
import groovyx.gpars.GParsPool
import jsr166y.ForkJoinPool

import java.util.concurrent.ExecutorService

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
class AssociationExtractor {

    Project project

    DBCredentials credentials

    AssociationExtractor(Project proj, DBCredentials credentials) {
        this.project = proj
        this.credentials = credentials
    }

    void extractAssociations() {
        Set<Type> types = Sets.newHashSet()
        DBManager.instance.open(credentials)
        types.addAll(project.getAllTypes())
        DBManager.instance.close()

//        GParsExecutorsPool.withPool(8) { ExecutorService srvc ->
//            types.eachParallel { Type type ->
            types.each { Type type ->
                DBManager.instance.open(credentials)
                handleTypeAssociation(type)
                DBManager.instance.close()
            }
//        }
    }

    private void handleTypeAssociation(Type type/*, ExecutorService pool*/) {
//        DBManager.instance.open(credentials)
        List<Field> fields = type.getFields()
//        DBManager.instance.close()

//        GParsExecutorsPool.withExistingPool(pool) {
            fields.each { Field f ->
//                DBManager.instance.open(credentials)
                if (f.getType() != null && f.getType().getReference() != null)
                    createAssociation(type, f.getType())
//                DBManager.instance.close()
            }
//        }
    }

    private void createAssociation(Type type, TypeRef ref) {
        Reference reference = ref.getReference()
        if (reference && ref.getType() == TypeRefType.Type) {
            Type dep = ref.getType(project.getProjectKey())
            if (type != null)
                type.associatedTo(dep)
        }
    }
}
