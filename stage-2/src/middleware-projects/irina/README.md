### 配置管理
- 编辑配置(增加/修改)
```shell
curl -X POST http://localhost:8080/config -H "Content-Type: application/json" -d '{"dataId": "user.json", "type": "JSON", "content": "{\"owen.age\": 18,\"owen.name\": \"Owen Xue\"}"}'
```

- 删除配置
```shell
curl -X DELETE http://localhost:8080/config\?dataId\=user.json
```

- 查询配置
```shell
curl -X GET http://localhost:8080/config\?dataId\=user.json -s | jq
```

- 查询配置(指定配置类型)
```shell
curl -X GET http://localhost:8080/config\?dataId\=user.json&type=PROPERTIES -s | jq

curl -X GET http://localhost:8080/config\?dataId\=user.json&type=JSON -s | jq


curl -X GET http://localhost:8080/config\?dataId\=user.json\&type\=YAML -s | jq

# 截取 content 字段转换为 yaml 格式输出
curl -X GET http://localhost:8080/config\?dataId\=user.json\&type\=YAML -s | jq '.content'  | yq -P
```

- 监听配置
```shell
curl -X GET http://localhost:8080/config/watch\?dataId\=user.json -s | jq
```

### 可能存在的 BUG

客户端在持续监听配置变化的过程中，当收到服务端响应的的配置变化时，会执行以下步骤：
- 触发配置变更回调
- 再次发起 watch 接口调用

在监听到配置变化至下一次监听之前这个时间段，配置可能已经再次发生变化，错失了最新的配置变更。

容错方案
- 服务端启用一个滑动时间窗口，如 30min，保留配置变更事件；同时也可以给滑动窗口内保留的事件数量做限制。
- 客户端监听配置时可携带上一次获取的 revision 字段，服务端先去滑动时间窗口内检查有没有大于该 revision 的最新的事件，如果有直接返回给客户端。
- 客户端需要定时去服务端查询最新的配置
