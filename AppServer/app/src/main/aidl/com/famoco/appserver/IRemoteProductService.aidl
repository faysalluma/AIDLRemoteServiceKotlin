// IRemoteProductService.aidl
package com.famoco.appserver;
import com.famoco.appserver.models.Product;
// Declare any non-default types here with import statements

interface IRemoteProductService {
    void addProduct(String name , int quantity, float cost);
    Product getProduct(String name);
    List<Product> getAllProducts();
}