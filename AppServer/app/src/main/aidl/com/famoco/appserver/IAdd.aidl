// IAdd.aidl
package com.famoco.appserver;

interface IAdd {
    int addNumbers(int num1, int num2);//2 argument method to add
    List<String> getStringList();
}