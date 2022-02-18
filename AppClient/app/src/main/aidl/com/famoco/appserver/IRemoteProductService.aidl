// IRemoteProductService.aidl
package com.famoco.appserver;
import com.famoco.appserver.models.Product;
import com.famoco.appserver.IRemoteCallBack;
// Declare any non-default types here with import statements

interface IRemoteProductService {
    void addProduct(String name , int quantity, float cost);
    Product getProduct(String name);
    List<Product> getAllProducts();
    void registerCallback(IRemoteCallBack callback);
    void unregisterCallback(IRemoteCallBack callback);
}