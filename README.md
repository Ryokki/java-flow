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

