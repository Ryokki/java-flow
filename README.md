这是一个简单的java stream实现，我称之为flow。写这个的起因是前一阵子看过stream的源码，看懂了非并发流的实现，但纸上学来终觉浅，尤其对于各个类的设计还不太清楚。因此，我决定自己写一版，通过实现来理解。

# 第一版
实现以下功能
```
List.of(1,2,4,3,5,6)
    .stream()
    .filter(i -> i % 2 == 0)
    .map(i -> i * 10)
    .count();
```
## 写前梳理

- `List.of(1,2,4,3,5,6).stream`: 从Collection的spliterator()方法产生的stream，spliterator先不做改动
- `.filter` or `.map`，这是中间过程，当执行的时候，把整个stream串起来一个List
- `.count`，这是结束语句

Questions:
- 不知道FLow里面泛型如何用



![Stream_pipeline_example](https://kkbabe-picgo.oss-cn-hangzhou.aliyuncs.com/img/Stream_pipeline_example.png)

图中通过`Collection.stream()`方法得到*Head*也就是stage0，紧接着调用一系列的中间操作，不断产生新的Stream。**这些Stream对象以双向链表的形式组织在一起，构成整个流水线，由于每个Stage都记录了前一个Stage和本次的操作以及回调函数，依靠这种结构就能建立起对数据源的所有操作**。这就是Stream记录操作的方式。



Stream的组织结构：

- 每个Stream都需要知道他从头到现在的所有stage，例如map这个stream，就是有source、filter、map这三个stage



执行时：

- 当执行count的时候，需要source去遍历一遍，每次遍历都拿下层stage (head和中间操作都是虚ace办法)