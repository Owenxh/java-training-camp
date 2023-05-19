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
