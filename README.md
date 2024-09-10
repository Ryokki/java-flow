这是一个简单的java stream实现，我称之为flow。写这个的起因是前一阵子看过stream的源码，看懂了非并发流的实现，但纸上学来终觉浅，尤其对于各个类的设计还不太清楚。因此，我决定自己写一版，通过实现来理解。

**TODO**
这次先做非并行流～

- [x] 中间操作 filter & map
- [x] 终结逻辑 count
- [x] sort 
- [x] 支持短路逻辑. findAny、findFirst
- [ ] collect、reduce
- [ ] 看看Comparator逻辑, 实现sort

![image-20240801140650293](https://kkbabe-picgo.oss-cn-hangzhou.aliyuncs.com/img/image-20240801140650293.png)
