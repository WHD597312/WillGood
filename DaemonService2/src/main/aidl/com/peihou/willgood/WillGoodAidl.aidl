// WillGoodAidl.aidl
package com.peihou.willgood;

// Declare any non-default types here with import statements

interface WillGoodAidl {
 void startService2();
    	void stopService2();
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
