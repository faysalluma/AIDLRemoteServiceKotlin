// IRemoteCallBack.aidl
package com.famoco.appserver;
import com.famoco.appserver.models.Product;

// Declare any non-default types here with import statements

interface IRemoteCallBack {
    String resultMessage(String message);
    void getProduct(inout Product product);
    void getAllProducts(in List<Product> products);
}