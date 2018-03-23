# Sku
Android Sku选择器, 类似于淘宝，天猫，京东，支持多维属性，购物车动画，支持MVVM架构，可以直接使用

[Demo下载](https://fir.im/seyb)

功能描述
=======
1. 支持多维属性，库存为空判断；
2. 支持设置选中指定的sku；
3. 支持手动输入数量；

<div style="float:left;border:solid 1px 000;margin:5px;">
	<img src="screenshots/20171201_131459.gif" alt="screenshot" title="20171201_131459.gif" width="300">
	<img src="screenshots/20171201_131516.gif" alt="screenshot" title="20171201_131516.gif" width="300">
</div>

添加依赖
=======
[![Download](https://api.bintray.com/packages/wuhenzhizao/maven/sku/images/download.svg) ](https://bintray.com/wuhenzhizao/maven/sku/_latestVersion)

```xml
buildscript {
    allprojects {
        repositories {
            jcenter()
        }
    }
}

dependencies {
    compile 'com.wuhenzhizao:sku:1.0.4'
}
```

绑定数据
-------

```java
SkuSelectScrollView.setSkuList(List<Sku> skuList);
```

设置选中的sku(一个sku时，默认选中）
-------

```java
SkuSelectScrollView.setSelectedSku(Sku sku);
```


设置监听
-------

```java
SkuSelectScrollView.setListener(new OnSkuListener() {
    /**
     * 属性取消选中
     *
     * @param unselectedAttribute
     */
    public void onUnselected(SkuAttribute unselectedAttribute) {}

    /**
     * 属性选中
     *
     * @param selectAttribute
     */
    public void onSelect(SkuAttribute selectAttribute) {}

    /**
     * sku选中
     *
     * @param sku
     */
    public void onSkuSelected(Sku sku) {});
```

使用MVVM架构 
---------- 
  
组件提供[SkuViewDelegate](library/src/main/java/com/wuhenzhizao/sku/view/SkuViewDelegate.java)来对MVVM模式下的交互进行支持 

```xml
<SkuSelectScrollView
    xmlns:sku="http://schemas.android.com/apk/res-auto"
    android:id="@+id/sku_databinding"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginTop="50dp"
    sku:skuList="@{vm.skuList}"
    sku:skuViewDelegate="@{vm.delegate}" />
```

技术交流
======
|QQ交流群|
|:---:|
|<img src="screenshots/qq_group.jpeg" alt="screenshot"  width="200">|
