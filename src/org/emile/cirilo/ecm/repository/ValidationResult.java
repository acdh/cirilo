/*
 *  -------------------------------------------------------------------------
 *  Copyright 2014 
 *  Centre for Information Modeling - Austrian Centre for Digital Humanities
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 *  -------------------------------------------------------------------------
 */

package org.emile.cirilo.ecm.repository;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.*;

/**
 * The result of a validation operation. Contain jaxb serialisation tags
 */
@XmlRootElement(name = "validation")
public class ValidationResult {

    /**
     * Was the validation succesfull
     */
    private boolean valid;

    /**
     * The list of problems encountered
     */
    private List<String> problems;

    /**
     * Empty, succesful validation
     */
    public ValidationResult() {
        valid = true;
        problems = new ArrayList<String>();
    }

    /**
     * New validation
     * @param valid was the validation succesfull
     * @param problems the problems encountered
     */
    public ValidationResult(boolean valid, List<String> problems) {
        this.valid = valid;
        this.problems = problems;
    }

    @XmlAttribute(name = "valid")
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @XmlElement(name = "problem")
    @XmlElementWrapper()
    public List<String> getProblems() {
        return Collections.unmodifiableList(problems);
    }

    public void setProblems(List<String> problems) {
        this.problems = problems;
    }


    public boolean add(String s) {
        return problems.add(s);
    }

    public boolean addAll(Collection<? extends String> strings) {
        return problems.addAll(strings);
    }


    public ValidationResult combine(ValidationResult that) {
        ValidationResult result = new ValidationResult();
        result.setValid(this.isValid() && that.isValid());
        List<String> problems1 = this.getProblems();
        List<String> problems2 = that.getProblems();
        ArrayList<String> newproblems = new ArrayList<String>();
        newproblems.addAll(problems1);
        newproblems.addAll(problems2);
        result.setProblems(newproblems);
        return result;
    }
}
