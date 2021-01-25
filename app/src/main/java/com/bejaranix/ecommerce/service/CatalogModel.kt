package com.bejaranix.ecommerce.service

data class CatalogModel(val items:List<ServiceCatalogItem>)

data class ServiceCatalogItem(val id:String, val title:String, val price:String, val image:String)

