# Sku
Android Sku 选择器, 类似于淘宝，天猫，京东，支持多维属性，购物车动画，支持MVVM架构

[Demo下载](https://www.pgyer.com/i29u)

功能描述
=======
1. 支持多维属性，库存为空判断；
2. 支持手动输入数量；
3. demo提供添加购物车动画

<img src="screenshots/20171201_131459.gif" width = "300" />
<img src="screenshots/20171201_131516.gif" width = "300" />

绑定数据
-------

```java
SkuSelectScrollView.setSRkuList(List<Sku> skuList);
```


设置监听
-------

```java
SkuSelectScrollView.setListener(new OnSkuListener() {
    @Override
    public void onUnselect(SkuAttribute unselectAttribute) {}

    @Override
    public void onSelect(SkuAttribute selectAttribute) {}

    @Override
    public void onSkuSelected(Sku sku) {});
```

使用MVVM架构 
---------- 
  
组件提供[SkuViewProxy](library/src/main/java/com/wuhenzhizao/widget/SkuViewProxy.java)来对MVVM数据绑定进行支持  

```xml
<SkuSelectScrollView
    xmlns:sku="http://schemas.android.com/apk/res-auto"
    android:id="@+id/sku_databinding"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginTop="50dp"
    sku:skuList="@{vm.skuList}"
    sku:skuViewProxy="@{vm.proxy}" />
```
