/*
 * Copyright 2017 mk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.mk5.gdx.fireapp.ios.database;

import org.moe.natj.objc.ObjCRuntime;

import java.util.List;
import java.util.Map;

import apple.NSObject;
import apple.foundation.NSArray;
import apple.foundation.NSDictionary;
import apple.foundation.NSNull;
import apple.foundation.NSNumber;
import apple.foundation.NSString;
import pl.mk5.gdx.fireapp.GdxFIRLogger;
import pl.mk5.gdx.fireapp.ios.exceptions.ConvertingException;

class DataProcessor {

    private DataProcessor() {

    }

    @SuppressWarnings("unchecked")
    static <T> T iosDataToJava(Object iosObject, Class<T> wantedType) {
        Class resultType = iosObject.getClass();
        T result = null;
        try {
            if (iosObject == null || NSNull.class.isAssignableFrom(iosObject.getClass())) {
                return null;
            } else if (wantedType.isAssignableFrom(iosObject.getClass())) {
                return (T) iosObject;
            } else if (NSString.class.isAssignableFrom(resultType) && wantedType == String.class) {
                result = processPrimitiveData(iosObject, wantedType);
            } else if (NSNumber.class.isAssignableFrom(resultType) && (Number.class.isAssignableFrom(wantedType) || wantedType == Boolean.class)) {
                result = processPrimitiveData(iosObject, wantedType);
            } else if (NSArray.class.isAssignableFrom(resultType) && List.class.isAssignableFrom(wantedType)) {
                // T is assignable from list. Result is ArrayList now.
                result = (T) NSArrayHelper.toList((NSArray) iosObject);
            } else if (NSDictionary.class.isAssignableFrom(resultType)) {
                Map map = NSDictionaryHelper.toMap((NSDictionary<NSString, NSObject>) iosObject);
                result = (T) map;
            } else {
                throw new ConvertingException("Result data type mismatch. Wanted type: " + wantedType
                        + ", result data type: " + resultType);
            }
        } catch (Exception e) {
            GdxFIRLogger.error("Can't convert " + resultType + " to " + wantedType, e);
            throw e;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    static <T> T processPrimitiveData(Object data, Class<T> wantedType) {
        T result = null;
        if (data.getClass() == wantedType) {
            return (T) data;
        }
        if (data instanceof NSNumber) {
            if (wantedType == Long.class) {
                result = (T) Long.valueOf(((NSNumber) data).longValue());
            } else if (wantedType == Integer.class) {
                result = (T) Integer.valueOf(((NSNumber) data).intValue());
            } else if (wantedType == Float.class) {
                result = (T) Float.valueOf(((NSNumber) data).floatValue());
            } else if (wantedType == Double.class) {
                result = (T) Double.valueOf(((NSNumber) data).doubleValue());
            } else if (wantedType == Boolean.class) {
                result = (T) Boolean.valueOf(((NSNumber) data).boolValue());
            }
        } else if (data instanceof NSString) {
            result = (T) ObjCRuntime.createJavaString(((NSString) data).getPeerPointer());
        } else {
            throw new ConvertingException("" + data.getClass() + " is not primitive data");
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    static Object iosDataToJava(Object iosObject) {
        if (iosObject instanceof NSString) {
            return DataProcessor.processPrimitiveData(iosObject, String.class);
        } else if (iosObject instanceof NSNumber) {
            return NSNumberHelper.getNSNumberPrimitive((NSNumber) iosObject);
        } else if (iosObject instanceof NSArray) {
            return NSArrayHelper.toList((NSArray) iosObject);
        } else if (iosObject instanceof NSDictionary) {
            return NSDictionaryHelper.toMap((NSDictionary<NSString, NSObject>) iosObject);
        }
        return iosObject;
    }

    @SuppressWarnings("unchecked")
    static NSObject javaDataToIos(Object javaObject) {
        if (javaObject instanceof String) {
            return NSString.alloc().initWithString((String) javaObject);
        } else if (javaObject instanceof Boolean) {
            return NSNumber.numberWithBool((Boolean) javaObject);
        } else if (javaObject instanceof Integer) {
            return NSNumber.numberWithInt((Integer) javaObject);
        } else if (javaObject instanceof Long) {
            return NSNumber.numberWithLong((Long) javaObject);
        } else if (javaObject instanceof Float) {
            return NSNumber.numberWithFloat((Float) javaObject);
        } else if (javaObject instanceof Double) {
            return NSNumber.numberWithDouble((Double) javaObject);
        } else if (javaObject instanceof java.util.List) {
            return NSArrayHelper.toArray((java.util.List) javaObject);
        } else {
            // Every other value try to convert to Map.
            try {
                return NSDictionaryHelper.toNSDictionary(javaObject);
            } catch (Exception e) {
                GdxFIRLogger.error("Can't convert " + javaObject.getClass().getSimpleName() + " to NSObject", e);
                return NSNull.alloc().init();
            }
        }
    }
}
