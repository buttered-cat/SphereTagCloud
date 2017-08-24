SphereTagCloud
-----------

Android上的一个球形标签云，效果类似WordPress插件*3D Tag Cloud*，如下图（图片来自[华联网](http://www.huavi.cn/tag/3d/)）：

![3D Tag Cloud](http://www.huavi.cn/wp-content/uploads/2017/06/wp-tag6.gif)

原理很简单，空间中任何一个向量绕某一轴的旋转可以分解为：

1. 向量在轴方向分量的的旋转
2. 向量在垂直于轴方向分量的旋转

数学细节可以在[这篇文档](http://ksuweb.kennesaw.edu/~plaval//math4490/rotgen.pdf)找到。实际上标签的排布可以是任何形状，而不仅仅是球形，算法的point-wise属性意味着其正确性与排布方式无关。

环境：IntelliJ IDEA
