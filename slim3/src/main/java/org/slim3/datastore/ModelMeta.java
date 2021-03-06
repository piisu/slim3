/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.slim3.datastore;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import org.slim3.util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A meta data of model.
 *
 * @param <M> the model type
 * @author higa
 * @since 1.0.0
 */
public abstract class ModelMeta<M> {

    /**
     * The kind of entity.
     */
    protected String kind;

    /**
     * The model class.
     */
    protected Class<M> modelClass;

    /**
     * The list of class hierarchies. If you create polymorphic models such as A
     * -> B -> C, the list of A is empty, the list of B is [B], the list of c
     * is[B, C].
     */
    protected List<String> classHierarchyList;

    /**
     * The bean descriptor.
     */
    protected BeanDesc beanDesc;

    /**
     * Constructor.
     *
     * @param kind       the kind of entity
     * @param modelClass the model class
     * @throws NullPointerException if the modelClass parameter is null
     */
    public ModelMeta(String kind, Class<M> modelClass)
            throws NullPointerException {
        this(kind, modelClass, null);
    }

    /**
     * Constructor.
     *
     * @param kind               the kind of entity
     * @param modelClass         the model class
     * @param classHierarchyList the list of class hierarchies
     * @throws NullPointerException if the modelClass parameter is null
     */
    @SuppressWarnings("unchecked")
    public ModelMeta(String kind, Class<M> modelClass,
                     List<String> classHierarchyList) throws NullPointerException {
        if (kind == null) {
            throw new NullPointerException("The kind parameter is null.");
        }
        if (modelClass == null) {
            throw new NullPointerException("The modelClass parameter is null.");
        }
        this.kind = kind;
        this.modelClass = modelClass;
        if (classHierarchyList == null) {
            this.classHierarchyList = Collections.EMPTY_LIST;
        } else {
            this.classHierarchyList =
                    Collections.unmodifiableList(classHierarchyList);
        }
    }

    /**
     * Constructor.
     */
    protected ModelMeta() {
    }

    /**
     * Returns the kind of entity.
     *
     * @return the kind of entity
     */
    public String getKind() {
        return kind;
    }

    /**
     * Returns the model class.
     *
     * @return the model class
     */
    public Class<M> getModelClass() {
        return modelClass;
    }

    /**
     * Returns the list of class hierarchies.
     *
     * @return the list of class hierarchies
     */
    public List<String> getClassHierarchyList() {
        return classHierarchyList;
    }

    /**
     * Returns the AND criterion.
     *
     * @param criteria the filter criteria
     * @return the AND criterion
     */
    public FilterCriterion and(FilterCriterion... criteria) {
        return new CompositeCriterion(
                this,
                CompositeFilterOperator.AND,
                criteria);
    }

    /**
     * Returns the OR criterion.
     *
     * @param criteria the filter criteria
     * @return the OR criterion
     */
    public FilterCriterion or(FilterCriterion... criteria) {
        return new CompositeCriterion(
                this,
                CompositeFilterOperator.OR,
                criteria);
    }

    /**
     * Returns the schemaVersion property name.
     *
     * @return the schemaVersion property name
     */
    public abstract String getSchemaVersionName();

    /**
     * Returns the classHierarchyList property name.
     *
     * @return the classHierarchyList property name
     */
    public abstract String getClassHierarchyListName();

    /**
     * Converts the entity to a model.
     *
     * @param entity the entity
     * @return a model
     */
    public abstract M entityToModel(Entity entity);

    /**
     * Converts the model to an entity.
     *
     * @param model the model
     * @return an entity
     */
    public abstract Entity modelToEntity(Object model);

    /**
     * Returns version property value of the model.
     *
     * @param model the model
     * @return a version property value of the model
     */
    protected abstract long getVersion(Object model);

    /**
     * Increments the version property value.
     *
     * @param model the model
     */
    protected abstract void incrementVersion(Object model);

    /**
     * This method is called before a model is put to datastore.
     *
     * @param model the model
     */
    protected abstract void prePut(Object model);

    /**
     * This method is called after a model is get from datastore.
     *
     * @param model the model
     */
    protected abstract void postGet(Object model);

    /**
     * Returns a key of the model.
     *
     * @param model the model
     * @return a key of the model
     */
    protected abstract Key getKey(Object model);

    /**
     * Sets the key to the model.
     *
     * @param model the model
     * @param key   the key
     */
    protected abstract void setKey(Object model, Key key);

    /**
     * Assigns a key to {@link ModelRef} if necessary.
     *
     * @param ds    the asynchronous datastore service
     * @param model the model
     * @throws NullPointerException if the ds parameter is null or if the model parameter is null
     */
    protected abstract void assignKeyToModelRefIfNecessary(
            AsyncDatastoreService ds, Object model) throws NullPointerException;

    /**
     * Validates the kind of the key.
     *
     * @param key the key
     * @throws IllegalArgumentException if the kind of the key is different from the kind of this
     *                                  model
     */
    protected void validateKey(Key key) throws IllegalArgumentException {
        if (key != null && !key.getKind().equals(kind)) {
            throw new IllegalArgumentException("The kind("
                    + key.getKind()
                    + ") of the key("
                    + key
                    + ") must be "
                    + kind
                    + ".");
        }
    }

    /**
     * Converts the long to a primitive short.
     *
     * @param value the long
     * @return a primitive short
     */
    protected short longToPrimitiveShort(Long value) {
        return value != null ? value.shortValue() : 0;
    }

    /**
     * Converts the long to a short.
     *
     * @param value the long
     * @return a short
     */
    protected Short longToShort(Long value) {
        return value != null ? value.shortValue() : null;
    }

    /**
     * Converts the long to a primitive int.
     *
     * @param value the long
     * @return a primitive int
     */
    protected int longToPrimitiveInt(Long value) {
        return value != null ? value.intValue() : 0;
    }

    /**
     * Converts the long to an integer.
     *
     * @param value the long
     * @return an integer
     */
    protected Integer longToInteger(Long value) {
        return value != null ? value.intValue() : null;
    }

    /**
     * Converts the long to a primitive long.
     *
     * @param value the long
     * @return a primitive long
     */
    protected long longToPrimitiveLong(Long value) {
        return value != null ? value : 0;
    }

    /**
     * Converts the double to a primitive float.
     *
     * @param value the double
     * @return a primitive float
     */
    protected float doubleToPrimitiveFloat(Double value) {
        return value != null ? value.floatValue() : 0;
    }

    /**
     * Converts the double to a float.
     *
     * @param value the double
     * @return a float
     */
    protected Float doubleToFloat(Double value) {
        return value != null ? value.floatValue() : null;
    }

    /**
     * Converts the double to a primitive double.
     *
     * @param value the double
     * @return a primitive double
     */
    protected double doubleToPrimitiveDouble(Double value) {
        return value != null ? value : 0;
    }

    /**
     * Converts the boolean to a primitive boolean.
     *
     * @param value the boolean
     * @return a primitive boolean
     */
    protected boolean booleanToPrimitiveBoolean(Boolean value) {
        return value != null ? value : false;
    }

    /**
     * Converts the {@link Enum} to a string representation.
     *
     * @param value the {@link Enum}
     * @return a string representation
     */
    protected String enumToString(Enum<?> value) {
        return value != null ? value.name() : null;
    }

    /**
     * Converts the string to an {@link Enum}.
     *
     * @param <T>   the enum type
     * @param clazz the enum class
     * @param value the String
     * @return an {@link Enum}
     */
    protected <T extends Enum<T>> T stringToEnum(Class<T> clazz, String value) {
        return value != null ? Enum.valueOf(clazz, value) : null;
    }

    /**
     * Converts the text to a string
     *
     * @param value the text
     * @return a string
     */
    protected String textToString(Text value) {
        return value != null ? value.getValue() : null;
    }

    /**
     * Converts the string to a text
     *
     * @param value the string
     * @return a text
     */
    protected Text stringToText(String value) {
        return value != null ? new Text(value) : null;
    }

    /**
     * Converts the short blob to an array of bytes.
     *
     * @param value the short blob
     * @return an array of bytes
     */
    protected byte[] shortBlobToBytes(ShortBlob value) {
        return value != null ? value.getBytes() : null;
    }

    /**
     * Converts the array of bytes to a short blob.
     *
     * @param value the array of bytes
     * @return a short blob
     */
    protected ShortBlob bytesToShortBlob(byte[] value) {
        return value != null ? new ShortBlob(value) : null;
    }

    /**
     * Converts the blob to an array of bytes.
     *
     * @param value the blob
     * @return an array of bytes
     */
    protected byte[] blobToBytes(Blob value) {
        return value != null ? value.getBytes() : null;
    }

    /**
     * Converts the array of bytes to a blob.
     *
     * @param value the array of bytes
     * @return a blob
     */
    protected Blob bytesToBlob(byte[] value) {
        return value != null ? new Blob(value) : null;
    }

    /**
     * Converts the short blob to a serializable object.
     *
     * @param <T>   the type
     * @param value the short blob
     * @return a serializable object
     */
    @SuppressWarnings("unchecked")
    protected <T> T shortBlobToSerializable(ShortBlob value) {
        return value != null ? (T) ByteUtil.toObject(value.getBytes()) : null;
    }

    /**
     * Converts the serializable object to a short blob.
     *
     * @param value the serializable object
     * @return a short blob
     */
    protected ShortBlob serializableToShortBlob(Object value) {
        return value != null
                ? new ShortBlob(ByteUtil.toByteArray(value))
                : null;
    }

    /**
     * Converts the blob to a serializable object.
     *
     * @param <T>   the type
     * @param value the blob
     * @return a serializable object
     */
    @SuppressWarnings("unchecked")
    protected <T> T blobToSerializable(Blob value) {
        return value != null ? (T) ByteUtil.toObject(value.getBytes()) : null;
    }

    /**
     * Converts the serializable object to a blob.
     *
     * @param value the serializable object
     * @return a blob
     */
    protected Blob serializableToBlob(Object value) {
        return value != null ? new Blob(ByteUtil.toByteArray(value)) : null;
    }

    /**
     * Converts the list to an array list.
     *
     * @param <T>   the type
     * @param clazz the class
     * @param value the list
     * @return an array list
     */
    @SuppressWarnings("unchecked")
    protected <T> ArrayList<T> toList(Class<T> clazz, Object value) {
        if (value == null) {
            return new ArrayList<T>();
        }
        return (ArrayList<T>) value;
    }

    /**
     * Converts the list of long to a list of short.
     *
     * @param value the list of long
     * @return a list of short
     */
    @SuppressWarnings("unchecked")
    protected ArrayList<Short> longListToShortList(Object value) {
        List<Long> v = (List<Long>) value;
        if (v == null) {
            return new ArrayList<Short>();
        }
        ArrayList<Short> collection = new ArrayList<Short>(v.size());
        int size = v.size();
        for (int i = 0; i < size; i++) {
            Long l = v.get(i);
            collection.add(l != null ? l.shortValue() : null);
        }
        return collection;
    }

    /**
     * Converts the list of long to a list of integer.
     *
     * @param value the list of long
     * @return a list of integer
     */
    @SuppressWarnings("unchecked")
    protected ArrayList<Integer> longListToIntegerList(Object value) {
        List<Long> v = (List<Long>) value;
        if (v == null) {
            return new ArrayList<Integer>();
        }
        ArrayList<Integer> collection = new ArrayList<Integer>(v.size());
        int size = v.size();
        for (int i = 0; i < size; i++) {
            Long l = v.get(i);
            collection.add(l != null ? l.intValue() : null);
        }
        return collection;
    }

    /**
     * Converts the list of double to a list of float.
     *
     * @param value the list of double
     * @return a list of float
     */
    @SuppressWarnings("unchecked")
    protected ArrayList<Float> doubleListToFloatList(Object value) {
        List<Double> v = (List<Double>) value;
        if (v == null) {
            return new ArrayList<Float>();
        }
        ArrayList<Float> collection = new ArrayList<Float>(v.size());
        int size = v.size();
        for (int i = 0; i < size; i++) {
            Double d = v.get(i);
            collection.add(d != null ? d.floatValue() : null);
        }
        return collection;
    }

    /**
     * Converts the list of {@link Enum}s to a list of strings.
     *
     * @param value the list of {@link Enum}
     * @return a list of strings
     */
    @SuppressWarnings("unchecked")
    protected List<String> enumListToStringList(Object value) {
        List<Enum<?>> v = (List<Enum<?>>) value;
        if (v == null) {
            return new ArrayList<String>();
        }
        List<String> list = new ArrayList<String>(v.size());
        for (Enum<?> e : v) {
            list.add(e.name());
        }
        return list;
    }

    /**
     * Converts the list of strings to a list of {@link Enum}s.
     *
     * @param <T>   the enum type
     * @param clazz the enum class
     * @param value the list of strings
     * @return a list of {@link Enum}s
     */
    @SuppressWarnings("unchecked")
    protected <T extends Enum<T>> List<T> stringListToEnumList(Class<T> clazz,
                                                               Object value) {
        List<String> v = (List<String>) value;
        if (v == null) {
            return new ArrayList<T>();
        }
        List<T> list = new ArrayList<T>(v.size());
        for (String s : v) {
            list.add(s != null ? Enum.valueOf(clazz, s) : null);
        }
        return list;
    }

    /**
     * Returns the bean descriptor.
     *
     * @return the bean descriptor
     */
    protected BeanDesc getBeanDesc() {
        if (beanDesc != null) {
            return beanDesc;
        }
        beanDesc = BeanUtil.getBeanDesc(modelClass);
        return beanDesc;
    }

    /**
     * Determines if the property is cipher.
     *
     * @param propertyName the property name
     * @return whether property is cipher
     * @since 1.0.6
     */
    protected boolean isCipherProperty(String propertyName) {
        return false;
    }

    /**
     * Encrypt the text.
     *
     * @param text the text
     * @return the encrypted text
     * @since 1.0.6
     */
    protected String encrypt(String text) {
        Cipher c = CipherFactory.getFactory().createCipher();
        return c.encrypt(text);
    }

    /**
     * Encrypt the text.
     *
     * @param text the text
     * @return the encrypted text
     * @since 1.0.6
     */
    protected Text encrypt(Text text) {
        if (text == null)
            return null;
        return new Text(encrypt(text.getValue()));
    }

    /**
     * Decrypt the encrypted text.
     *
     * @param encryptedText the encrypted text
     * @return the decrypted text
     * @since 1.0.6
     */
    protected String decrypt(String encryptedText) {
        Cipher c = CipherFactory.getFactory().createCipher();
        return c.decrypt(encryptedText);
    }

    /**
     * Decrypt the encrypted text.
     *
     * @param encryptedText the encrypted text
     * @return the decrypted text
     * @since 1.0.6
     */
    protected Text decrypt(Text encryptedText) {
        if (encryptedText == null)
            return null;
        return new Text(decrypt(encryptedText.getValue()));
    }
}