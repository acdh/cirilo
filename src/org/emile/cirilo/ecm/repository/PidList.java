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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * A class representing a collection of pids. Contains the jaxb annotations
 * to give good xml serialization. Used in FedoraConnector as a return type
 * to users
 * <br/>
 * <pre>
 * {@code
 * <xs:schema version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
 *
 *   <xs:element name="pidList" type="pidList"/>
 *
 *   <xs:complexType name="pidList">
 *     <xs:sequence>
 *       <xs:element name="pid" type="xs:string" minOccurs="0" maxOccurs="unbounded"/>
 *     </xs:sequence>
 *   </xs:complexType>
 * </xs:schema>
 * }
 * </pre>
 * @see FedoraConnector
 */
@XmlRootElement(name = "pidList")
public class PidList implements List<String> {

    @XmlElement(name = "pid")
    private List<String> pids;

    public PidList(List<String> objects) {
        pids = objects;
    }

    public PidList(String... objects){
        pids = Arrays.asList(objects);
    }

    public PidList() {
        pids = new ArrayList<String>();
    }

    public int size() {
        return pids.size();
    }

    public boolean isEmpty() {
        return pids.isEmpty();
    }

    public boolean contains(Object o) {
        return pids.contains(o);
    }

    public Iterator<String> iterator() {
        return pids.iterator();
    }

    public Object[] toArray() {
        return pids.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return pids.toArray(a);
    }

    public boolean add(String s) {
        return pids.add(s);
    }

    public boolean remove(Object o) {
        return pids.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return pids.containsAll(c);
    }

    public boolean addAll(Collection<? extends String> c) {
        return pids.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends String> c) {
        return pids.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return pids.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return pids.retainAll(c);
    }

    public void clear() {
        pids.clear();
    }

    public boolean equals(Object o) {
        return pids.equals(o);
    }

    public int hashCode() {
        return pids.hashCode();
    }

    public String get(int index) {
        return pids.get(index);
    }

    public String set(int index, String element) {
        return pids.set(index, element);
    }

    public void add(int index, String element) {
        pids.add(index, element);
    }

    public String remove(int index) {
        return pids.remove(index);
    }

    public int indexOf(Object o) {
        return pids.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return pids.lastIndexOf(o);
    }

    public ListIterator<String> listIterator() {
        return pids.listIterator();
    }

    public ListIterator<String> listIterator(int index) {
        return pids.listIterator(index);
    }

    public List<String> subList(int fromIndex, int toIndex) {
        return pids.subList(fromIndex, toIndex);
    }
}
